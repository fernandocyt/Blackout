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

    private GoogleMap map_crear_reporte = null;
    static Marker marcador_posicion_reporte = null;
    static Circle radio_reporte = null;
    TextView textview_km_radio;
    SeekBar seekbar_radio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_reporte);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_crear_reporte);
        mapFragment.getMapAsync(this);

        textview_km_radio = (TextView)findViewById(R.id.lbl_km_radio_crear_reporte);
        seekbar_radio = (SeekBar)findViewById(R.id.skb_radio_crear_reporte);
        seekbar_radio.setOnSeekBarChangeListener(this);
        seekbar_radio.setMax(500);

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
        Spinner sItems = (Spinner) findViewById(R.id.spn_servicios_crear_reporte);
        sItems.setAdapter(adapter);
    }

    private void cargarSpinnerEmpresas()
    {
        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("Edenor");
        spinnerArray.add("Edesur");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner sItems = (Spinner) findViewById(R.id.spn_empresas_crear_reporte);
        sItems.setAdapter(adapter);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map_crear_reporte = googleMap;
        map_crear_reporte.setOnMapLongClickListener(this);
        map_crear_reporte.animateCamera(CameraUpdateFactory.newLatLngZoom(Constantes.BSAS, 11.0f));
    }

    public void crearReporte(View view)
    {

    }

    @Override
    public void actualizarPosicionActual(LatLng posicion)
    {
        if (map_crear_reporte != null) {
            if (CrearReporte.marcador_posicion_reporte == null) {
                CrearReporte.marcador_posicion_reporte = map_crear_reporte.addMarker(new MarkerOptions().position(posicion));

                CrearReporte.radio_reporte = map_crear_reporte.addCircle(new CircleOptions()
                        .center(new LatLng(posicion.latitude, posicion.longitude))
                        .radius(0)
                        .fillColor(Color.BLUE));
            } else {
                CrearReporte.marcador_posicion_reporte.setPosition(posicion);
                CrearReporte.radio_reporte.setCenter(posicion);
            }
        }
    }

    @Override
    public void onMapLongClick(LatLng point) {
        if(CrearReporte.marcador_posicion_reporte != null) {
            CrearReporte.marcador_posicion_reporte.remove();
        }
        if(CrearReporte.radio_reporte != null) {
            CrearReporte.radio_reporte.remove();
        }

        CrearReporte.marcador_posicion_reporte = map_crear_reporte.addMarker(new MarkerOptions()
                .position(point)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        CrearReporte.radio_reporte = map_crear_reporte.addCircle(new CircleOptions()
                .center(new LatLng(point.latitude, point.longitude))
                .radius(0)
                .fillColor(Color.BLUE));
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
}

