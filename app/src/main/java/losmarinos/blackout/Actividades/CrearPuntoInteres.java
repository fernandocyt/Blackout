package losmarinos.blackout.Actividades;

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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import losmarinos.blackout.Constantes;
import losmarinos.blackout.ConsultorPOSTAPI;
import losmarinos.blackout.GPSTracker;
import losmarinos.blackout.Global;
import losmarinos.blackout.Objetos.Empresa;
import losmarinos.blackout.Objetos.PuntoInteres;
import losmarinos.blackout.Objetos.Reporte;
import losmarinos.blackout.ObservadorGPS;
import losmarinos.blackout.ParserJSON;
import losmarinos.blackout.R;

import static losmarinos.blackout.Constantes.TAGAPI.REGISTRAR_PUNTO_DE_INTERES;
import static losmarinos.blackout.Constantes.TAGAPI.REGISTRAR_REPORTE;

public class CrearPuntoInteres extends AppCompatActivity implements OnMapReadyCallback,
        ObservadorGPS,
        GoogleMap.OnMapLongClickListener,
        SeekBar.OnSeekBarChangeListener,
        AdapterView.OnItemSelectedListener {


    // Mapa
    GoogleMap map_crear_punto_interes = null;
    Marker marcador_posicion_punto_interes = null;
    Circle radio_punto_interes = null;

    // Interfaz
    TextView textview_km_radio;
    SeekBar seekbar_radio;
    Spinner spinner_servicios;
    Spinner spinner_empresas;

    // Otros
    private LatLng posicion_gps = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Crear Punto de Interes");
        setContentView(R.layout.activity_crear_punto_interes);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_crear_punto_interes);
        mapFragment.getMapAsync(this);

        this.textview_km_radio = (TextView)findViewById(R.id.lbl_km_radio_crear_punto_interes);
        this.seekbar_radio = (SeekBar)findViewById(R.id.skb_radio_crear_punto_interes);
        this.seekbar_radio.setOnSeekBarChangeListener(this);
        this.seekbar_radio.setMax(500);
        this.spinner_servicios = (Spinner) findViewById(R.id.spn_servicios_crear_punto_interes);
        this.spinner_servicios.setOnItemSelectedListener(this);
        this.spinner_empresas = (Spinner) findViewById(R.id.spn_empresas_crear_punto_interes);

        GPSTracker.addObserver(this);

        this.cargarSpinnerServicios();
        this.cargarSpinnerEmpresas(Constantes.SERVICIO.AGUA);

        Toast.makeText(this, "Para seleccionar ubicación mantener presionado el mapa", Toast.LENGTH_LONG).show();
    }


    private void cargarSpinnerServicios()
    {
        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("No especificar");
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
        spinnerArray.add("No especificar");

        for(int i = 0; i < Global.empresas.size(); i++)
        {
            if(Global.empresas.get(i).getTipoServicio() == servicio)
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
        map_crear_punto_interes = googleMap;
        map_crear_punto_interes.setOnMapLongClickListener(this);
        map_crear_punto_interes.animateCamera(CameraUpdateFactory.newLatLngZoom(Constantes.BSAS, 11.0f));

        if(posicion_gps != null)
        {
            this.actualizarMapaPuntoInteres(posicion_gps);
        }
    }

    public void crearPuntoInteres(View view)
    {
        if(this.marcador_posicion_punto_interes == null){
            Toast.makeText(this, "Debe marcar una posicion en el mapa", Toast.LENGTH_LONG).show();
            return;
        }

        String nombre_servicio = this.spinner_servicios.getSelectedItem().toString();
        Constantes.SERVICIO servicio = Constantes.stringToServicio(nombre_servicio);

        String nombre_empresa = this.spinner_empresas.getSelectedItem().toString();
        int id_empresa = -1;
        if (!nombre_empresa.equals("No especificar")) {
            id_empresa = Global.encontrarEmpresaPorNombre(nombre_empresa).getId();
        }

        LatLng posicion = marcador_posicion_punto_interes.getPosition();
        int radio = seekbar_radio.getProgress();

        //PuntoInteres nuevo_punto_interes = new PuntoInteres(servicio, id_empresa, posicion, radio);
        //Global.usuario_actual.addPuntoInteres(nuevo_punto_interes);

        try{
            JSONObject nuevo_pto_interes = ParserJSON.crearJSONPuntoDeInteres(Global.usuario_actual.getId(), servicio, id_empresa, posicion, radio);

            String resultado = new ConsultorPOSTAPI("punto-de-interes", Global.token_usuario_actual, nuevo_pto_interes, REGISTRAR_PUNTO_DE_INTERES, null).execute().get();
            StringBuilder mensaje_error = new StringBuilder();
            if(ParserJSON.esError(resultado, mensaje_error)){
                Toast.makeText(this, mensaje_error, Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
        }

        this.radio_punto_interes = null;
        this.marcador_posicion_punto_interes = null;
        this.map_crear_punto_interes = null;
        this.finish();
    }

    @Override
    public void actualizarPosicionActual(LatLng posicion)
    {
        this.posicion_gps = posicion;

        if (map_crear_punto_interes != null && this.marcador_posicion_punto_interes == null)
        {
            this.actualizarMapaPuntoInteres(posicion);
        }
    }

    @Override
    public void onMapLongClick(LatLng point) {
        this.actualizarMapaPuntoInteres(point);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        this.textview_km_radio.setText(String.valueOf(progress) + "m");

        if(this.radio_punto_interes != null)
        {
            this.radio_punto_interes.setRadius(progress);
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

    private void actualizarMapaPuntoInteres(LatLng posicion_marcador)
    {
        if (map_crear_punto_interes == null) {
            return;
        }

        if(this.marcador_posicion_punto_interes == null) {

            // Agrego marcador
            this.marcador_posicion_punto_interes = map_crear_punto_interes.addMarker(new MarkerOptions()
                    .position(posicion_marcador));

            // Agrego radio
            this.radio_punto_interes = map_crear_punto_interes.addCircle(new CircleOptions()
                    .center(posicion_marcador)
                    .radius(seekbar_radio.getProgress())
                    .strokeColor(Constantes.STROKE_COLOR_CIRCLE)
                    .fillColor(Constantes.COLOR_CIRCLE)
                    .strokeWidth(5));
        }
        else
        {
            // Si el marcador ya fue agregado y solo hay que actualizar posicion
            this.marcador_posicion_punto_interes.setPosition(posicion_marcador);

            this.radio_punto_interes.setCenter(posicion_marcador);
            this.radio_punto_interes.setRadius(seekbar_radio.getProgress());
        }
    }

    public void centrarMapaEnMarcador(View view)
    {
        if(this.marcador_posicion_punto_interes != null) {
            this.map_crear_punto_interes.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(this.marcador_posicion_punto_interes.getPosition().latitude,
                            this.marcador_posicion_punto_interes.getPosition().longitude),
                    14.0f));
        }
    }

    public void marcarMapaEnPosicionGPS(View view)
    {
        if(this.posicion_gps != null)
        {
            this.actualizarMapaPuntoInteres(this.posicion_gps);
            this.centrarMapaEnMarcador(view);
        }
    }
}
