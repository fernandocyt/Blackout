package losmarinos.blackout.Actividades;

import losmarinos.blackout.R;
import losmarinos.blackout.Constantes;
import losmarinos.blackout.ObservadorGPS;
import losmarinos.blackout.GPSTracker;
import losmarinos.blackout.ConsultorAPI;
import losmarinos.blackout.Objetos.Reporte;

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

import java.util.ArrayList;
import java.util.List;

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

        for(int i = 0; i < ConsultorAPI.empresas.size(); i++)
        {
            if(ConsultorAPI.empresas.get(i).getTipoServicio() == servicio)
            {
                spinnerArray.add(ConsultorAPI.empresas.get(i).getNombre());
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
        String servicio = this.spinner_servicios.getSelectedItem().toString();
        String empresa = this.spinner_empresas.getSelectedItem().toString();
        Reporte nuevo_reporte = new Reporte(Constantes.stringToServicio(servicio), empresa, marcador_posicion_reporte.getPosition(), seekbar_radio.getProgress());

        ConsultorAPI.reportes.add(nuevo_reporte);

        this.radio_reporte = null;
        this.marcador_posicion_reporte = null;
        this.map_crear_reporte = null;
        this.finish();
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

            // Agrego radio
            this.radio_reporte = map_crear_reporte.addCircle(new CircleOptions()
                    .center(posicion_marcador)
                    .radius(seekbar_radio.getProgress())
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
