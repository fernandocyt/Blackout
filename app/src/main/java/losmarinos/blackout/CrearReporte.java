package losmarinos.blackout;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

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
import java.util.concurrent.Semaphore;

public class CrearReporte extends AppCompatActivity implements OnMapReadyCallback,
        ObservadorGPS,
        GoogleMap.OnMapLongClickListener,
        SeekBar.OnSeekBarChangeListener {

    // Mapa
    private GoogleMap map_crear_reporte = null;
    static Marker marcador_posicion_reporte = null;
    static Circle radio_reporte = null;

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
        this.spinner_empresas = (Spinner) findViewById(R.id.spn_empresas_crear_reporte);

        GPSTracker.addObserver(this);

        this.cargarSpinnerServicios();
        this.cargarSpinnerEmpresas();
    }

    private void cargarSpinnerServicios()
    {
        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("Luz");
        spinnerArray.add("Gas");
        spinnerArray.add("Electricidad");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinner_servicios.setAdapter(adapter);
    }

    private void cargarSpinnerEmpresas()
    {
        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("Edenor");
        spinnerArray.add("Edesur");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinner_empresas.setAdapter(adapter);
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
        Reporte nuevo_reporte = new Reporte(servicio, empresa, marcador_posicion_reporte.getPosition(), seekbar_radio.getProgress());

        ConsultorAPI.reportes.add(nuevo_reporte);

        this.radio_reporte = null;
        this.marcador_posicion_reporte = null;
        this.map_crear_reporte = null;
        this.finish();
    }

    @Override
    public void actualizarPosicionActual(LatLng posicion)
    {
        if(map_crear_reporte == null)
        {
            this.posicion_gps = posicion;
        }
        else if (this.marcador_posicion_reporte == null)
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

        if(CrearReporte.radio_reporte != null)
        {
            CrearReporte.radio_reporte.setRadius(progress);
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

        if(CrearReporte.marcador_posicion_reporte == null) {

            // Si el marcador nunca fue agregado hasta ahora
            CrearReporte.marcador_posicion_reporte = map_crear_reporte.addMarker(new MarkerOptions()
                    .position(posicion_marcador));

            CrearReporte.radio_reporte = map_crear_reporte.addCircle(new CircleOptions()
                    .center(posicion_marcador)
                    .radius(seekbar_radio.getProgress())
                    .strokeColor(Color.TRANSPARENT)
                    .fillColor(0x220000FF)
                    .strokeWidth(5));
        }
        else
        {
            // Si el marcador ya fue agregado y solo hay que actualizar posicion
            CrearReporte.marcador_posicion_reporte.setPosition(posicion_marcador);

            CrearReporte.radio_reporte.setCenter(posicion_marcador);
            CrearReporte.radio_reporte.setRadius(seekbar_radio.getProgress());
        }
    }
}

