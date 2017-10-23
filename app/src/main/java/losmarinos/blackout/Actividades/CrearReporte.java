package losmarinos.blackout.Actividades;

import losmarinos.blackout.Aviso;
import losmarinos.blackout.Global;
import losmarinos.blackout.LocalDB;
import losmarinos.blackout.Objetos.Empresa;
import losmarinos.blackout.ParserJSON;
import losmarinos.blackout.R;
import losmarinos.blackout.Constantes;
import losmarinos.blackout.ObservadorGPS;
import losmarinos.blackout.GPSTracker;
import losmarinos.blackout.ConsultorPOSTAPI;
import losmarinos.blackout.Objetos.Reporte;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static losmarinos.blackout.Constantes.RADIO_MINIMO;
import static losmarinos.blackout.Constantes.TAGAPI.REGISTRAR_REPORTE;

public class CrearReporte extends AppCompatActivity implements OnMapReadyCallback,
        ObservadorGPS,
        GoogleMap.OnMapLongClickListener,
        SeekBar.OnSeekBarChangeListener,
        AdapterView.OnItemSelectedListener{

    // Mapa
    GoogleMap map_crear_reporte = null;
    Marker marcador_posicion_reporte = null;
    Circle radio_reporte = null;

    // Interfaz
    TextView textview_km_radio;
    SeekBar seekbar_radio;
    Spinner spinner_servicios;
    Spinner spinner_empresas;

    // Otros
    private LatLng posicion_gps = null;

    ProgressDialog progress_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Crear Reporte");
        setContentView(R.layout.activity_crear_reporte);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_crear_reporte);
        mapFragment.getMapAsync(this);

        this.textview_km_radio = (TextView)findViewById(R.id.lbl_km_radio_crear_reporte);
        this.seekbar_radio = (SeekBar)findViewById(R.id.skb_radio_crear_reporte);
        this.seekbar_radio.setOnSeekBarChangeListener(this);
        this.seekbar_radio.setMax(500);
        this.seekbar_radio.setProgress(RADIO_MINIMO);

        this.spinner_servicios = (Spinner) findViewById(R.id.spn_servicios_crear_reporte);
        this.spinner_servicios.setOnItemSelectedListener(this);
        this.spinner_empresas = (Spinner) findViewById(R.id.spn_empresas_crear_reporte);

        GPSTracker.addObserver(this);

        this.cargarSpinnerServicios();
        this.cargarSpinnerEmpresas(Constantes.SERVICIO.AGUA);

        Toast.makeText(this, "Para seleccionar ubicación mantener presionado el mapa", Toast.LENGTH_LONG).show();
    }

    private void cargarSpinnerServicios()
    {
        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add(Constantes.servicioToString(Constantes.SERVICIO.AGUA));
        spinnerArray.add(Constantes.servicioToString(Constantes.SERVICIO.LUZ));
        spinnerArray.add(Constantes.servicioToString(Constantes.SERVICIO.GAS));
        spinnerArray.add(Constantes.servicioToString(Constantes.SERVICIO.TELEFONO));
        spinnerArray.add(Constantes.servicioToString(Constantes.SERVICIO.INTERNET));
        spinnerArray.add(Constantes.servicioToString(Constantes.SERVICIO.CABLE));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinner_servicios.setAdapter(adapter);
    }

    private void cargarSpinnerEmpresas(Constantes.SERVICIO servicio)
    {
        List<String> spinnerArray =  new ArrayList<String>();

        for(int i = 0; i < Global.empresas.size(); i++)
        {
            if(Global.empresas.get(i).getTipoServicio() == servicio && Global.empresas.get(i).isHabilitada())
            {
                spinnerArray.add(Global.empresas.get(i).getNombre());
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinner_empresas.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
        String servicio = this.spinner_servicios.getSelectedItem().toString();

        this.cargarSpinnerEmpresas(Constantes.stringToServicio(servicio));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parentView) {
        // your code here
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map_crear_reporte = googleMap;
        map_crear_reporte.setOnMapLongClickListener(this);
        map_crear_reporte.animateCamera(CameraUpdateFactory.newLatLngZoom(Constantes.BSAS, 11.0f));

        if(posicion_gps != null)
        {
            this.actualizarMapaCrearReporte(posicion_gps);
        }
    }

    public void crearReporte(View view)
    {
        progress_dialog = Aviso.showProgressDialog(this, "Creando reporte...");

        final LatLng posicion = marcador_posicion_reporte.getPosition();

        new Thread(new Runnable() {
            @Override
            public void run() {

                if(marcador_posicion_reporte == null){
                    Aviso.hideProgressDialog(CrearReporte.this, progress_dialog);
                    Aviso.showToast(CrearReporte.this, "Debe marcar una posicion en el mapa");
                    return;
                }

                String nombre_servicio = spinner_servicios.getSelectedItem().toString();
                Constantes.SERVICIO servicio = Constantes.stringToServicio(nombre_servicio);

                // Me fijo si hay mas de 2 reportes activos con el mismo servicio no lo agrego
                int cant_mismo_servicio_activos = 0;

                boolean correcto = Global.usuario_actual.actualizarReportes(CrearReporte.this);
                if(!correcto){
                    Aviso.hideProgressDialog(CrearReporte.this, progress_dialog);
                    Aviso.showToast(CrearReporte.this, "Error. Comprueba tu conexion a internet.");
                    return;
                }

                List<Reporte> reportes_usuario = Global.usuario_actual.getReportes();
                for(int i = 0; i < reportes_usuario.size(); i++){
                    if(reportes_usuario.get(i).getServicio() == servicio && !reportes_usuario.get(i).isResuelto()){
                        cant_mismo_servicio_activos++;
                    }
                }
                if(cant_mismo_servicio_activos >= 2){
                    Aviso.hideProgressDialog(CrearReporte.this, progress_dialog);
                    Aviso.showToast(CrearReporte.this, "No puedes tener mas de 2 reportes de un servicio activos a la vez");
                    return;
                }

                String nombre_empresa = spinner_empresas.getSelectedItem().toString();

                int id_empresa = -1;
                if (!nombre_empresa.equals("No especificar")) {
                    id_empresa = Global.encontrarEmpresaPorNombre(nombre_empresa).getSubId();
                }


                int radio = seekbar_radio.getProgress();
                if(radio < RADIO_MINIMO){
                    radio = RADIO_MINIMO;
                }

                final Constantes.SERVICIO f_servicio = servicio;
                final int f_id_empresa = id_empresa;
                final LatLng f_posicion = posicion;
                final int f_radio = radio;

                try {
                    JSONObject nuevo_rep = ParserJSON.crearJSONReporte(Global.usuario_actual.getIdUsuario(), f_servicio, f_id_empresa, f_posicion, f_radio);

                    String resultado = new ConsultorPOSTAPI("reporte", Global.token_usuario_actual, nuevo_rep, REGISTRAR_REPORTE, null).execute().get();
                    StringBuilder mensaje_error = new StringBuilder();
                    if (ParserJSON.esError(resultado, mensaje_error)) {
                        Aviso.hideProgressDialog(CrearReporte.this, progress_dialog);
                        Aviso.showToast(CrearReporte.this, mensaje_error.toString());
                    } else {
                        Aviso.hideProgressDialog(CrearReporte.this, progress_dialog);
                        Aviso.showToast(CrearReporte.this, "Reporte creado exitosamente");
                        radio_reporte = null;
                        marcador_posicion_reporte = null;
                        map_crear_reporte = null;
                        CrearReporte.this.finish();
                    }
                } catch (Exception e) {
                    Aviso.hideProgressDialog(CrearReporte.this, progress_dialog);
                    Aviso.showToast(CrearReporte.this, "Error");
                }
            }
        }).start();

    }

    @Override
    public void actualizarPosicionActual(LatLng posicion)
    {
        this.posicion_gps = posicion;

        if (map_crear_reporte != null && this.marcador_posicion_reporte == null)
        {
            this.actualizarMapaCrearReporte(posicion);
        }
    }

    @Override
    public void onMapLongClick(LatLng point) {
        this.actualizarMapaCrearReporte(point);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(progress < RADIO_MINIMO){
            progress = RADIO_MINIMO;
        }

        this.textview_km_radio.setText(String.valueOf(progress) + "m");

        if(this.radio_reporte != null)
        {
            this.radio_reporte.setRadius(progress);
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
    }

    private void actualizarMapaCrearReporte(LatLng posicion_marcador)
    {
        if (map_crear_reporte == null) {
            return;
        }

        if(this.marcador_posicion_reporte == null) {

            // Agrego marcador
            this.marcador_posicion_reporte = map_crear_reporte.addMarker(new MarkerOptions()
                    .position(posicion_marcador));


            int radio = seekbar_radio.getProgress();
            if(radio < RADIO_MINIMO){
                radio = RADIO_MINIMO;
            }
            // Agrego radio
            this.radio_reporte = map_crear_reporte.addCircle(new CircleOptions()
                    .center(posicion_marcador)
                    .radius(radio)
                    .strokeColor(Constantes.STROKE_COLOR_CIRCLE)
                    .fillColor(Constantes.COLOR_CIRCLE)
                    .strokeWidth(5));
        }
        else
        {
            // Si el marcador ya fue agregado y solo hay que actualizar posicion
            this.marcador_posicion_reporte.setPosition(posicion_marcador);

            this.radio_reporte.setCenter(posicion_marcador);
            this.radio_reporte.setRadius(seekbar_radio.getProgress());
        }
    }

    public void centrarMapaEnMarcador(View view)
    {
        if(this.marcador_posicion_reporte != null) {
            this.map_crear_reporte.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(this.marcador_posicion_reporte.getPosition().latitude,
                            this.marcador_posicion_reporte.getPosition().longitude),
                    14.0f));
        }
    }

    public void marcarMapaEnPosicionGPS(View view)
    {
        if(this.posicion_gps != null)
        {
            this.actualizarMapaCrearReporte(this.posicion_gps);
            this.centrarMapaEnMarcador(view);
        }
    }
}

