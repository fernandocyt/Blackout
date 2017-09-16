package losmarinos.blackout.Actividades;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import losmarinos.blackout.Constantes;
import losmarinos.blackout.ConsultorPOSTAPI;
import losmarinos.blackout.GPSTracker;
import losmarinos.blackout.Global;
import losmarinos.blackout.Objetos.Empresa;
import losmarinos.blackout.Objetos.Reporte;
import losmarinos.blackout.ObservadorGPS;
import losmarinos.blackout.ParserJSON;
import losmarinos.blackout.R;

import static android.provider.Settings.System.DATE_FORMAT;
import static losmarinos.blackout.Constantes.TAGAPI.REGISTRAR_CORTE_PROGRAMADO;
import static losmarinos.blackout.Constantes.TAGAPI.REGISTRAR_REPORTE;

public class CrearCorteProgramado extends AppCompatActivity implements OnMapReadyCallback,
        ObservadorGPS,
        GoogleMap.OnMapLongClickListener,
        SeekBar.OnSeekBarChangeListener{

    Empresa empresa_actual;

    // Mapa
    GoogleMap map_crear_corte_programado = null;
    Marker marcador_posicion_corte_programado = null;
    Circle radio_corte_programado = null;

    // Interfaz
    TextView textview_km_radio;
    SeekBar seekbar_radio;

    // Otros
    private LatLng posicion_gps = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Crear Corte Programado");
        setContentView(R.layout.activity_crear_corte_programado);

        empresa_actual = Global.encontrarEmpresaPorId(Global.usuario_actual.getSubId());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_crear_corte_programado);
        mapFragment.getMapAsync(this);

        this.textview_km_radio = (TextView)findViewById(R.id.lbl_km_radio_crear_corte_programado);
        this.seekbar_radio = (SeekBar)findViewById(R.id.skb_radio_crear_corte_programado);
        this.seekbar_radio.setOnSeekBarChangeListener(this);
        this.seekbar_radio.setMax(500);

        GPSTracker.addObserver(this);

        Toast.makeText(this, "Para seleccionar ubicación mantener presionado el mapa", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map_crear_corte_programado = googleMap;
        map_crear_corte_programado.setOnMapLongClickListener(this);
        map_crear_corte_programado.animateCamera(CameraUpdateFactory.newLatLngZoom(Constantes.BSAS, 11.0f));

        if(posicion_gps != null)
        {
            this.actualizarMapaCrearCorteProgramado(posicion_gps);
        }
    }

    public void crearCorteProgramado(View view)
    {
        if(this.marcador_posicion_corte_programado == null){
            Toast.makeText(this, "Debe marcar una posicion en el mapa", Toast.LENGTH_LONG).show();
            return;
        }

        Constantes.SERVICIO servicio = empresa_actual.getTipoServicio();

        LatLng posicion = marcador_posicion_corte_programado.getPosition();
        int radio = seekbar_radio.getProgress();

        StringBuilder fecha_inicio = new StringBuilder();
        StringBuilder fecha_fin = new StringBuilder();

        if(!this.construirFechas(fecha_inicio, fecha_fin)){
            return;
        }

        try{
            JSONObject nuevo_corte_prog = ParserJSON.crearJSONCorteProgramado(servicio, empresa_actual.getSubId(), posicion, radio, fecha_inicio.toString(), fecha_fin.toString());

            String resultado = new ConsultorPOSTAPI("corte-programado", Global.token_usuario_actual, nuevo_corte_prog, REGISTRAR_CORTE_PROGRAMADO, null).execute().get();
            StringBuilder mensaje_error = new StringBuilder();
            if(ParserJSON.esError(resultado, mensaje_error)){
                Toast.makeText(this, mensaje_error, Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
        }

        this.radio_corte_programado = null;
        this.marcador_posicion_corte_programado = null;
        this.map_crear_corte_programado = null;
        this.finish();
    }

    public boolean construirFechas(StringBuilder fecha_inicio, StringBuilder fecha_fin){

        String dia_inicio = ((EditText)findViewById(R.id.txt_fecha_inicio_dia_crear_corte_programado)).getText().toString();
        String mes_inicio = ((EditText)findViewById(R.id.txt_fecha_inicio_mes_crear_corte_programado)).getText().toString();
        String anio_inicio = ((EditText)findViewById(R.id.txt_fecha_inicio_anio_crear_corte_programado)).getText().toString();
        String hora_inicio = ((EditText)findViewById(R.id.txt_fecha_inicio_hora_crear_corte_programado)).getText().toString();
        String mins_inicio = ((EditText)findViewById(R.id.txt_fecha_inicio_minutos_crear_corte_programado)).getText().toString();

        String dia_fin = ((EditText)findViewById(R.id.txt_fecha_fin_dia_crear_corte_programado)).getText().toString();
        String mes_fin = ((EditText)findViewById(R.id.txt_fecha_fin_mes_crear_corte_programado)).getText().toString();
        String anio_fin = ((EditText)findViewById(R.id.txt_fecha_fin_anio_crear_corte_programado)).getText().toString();
        String hora_fin = ((EditText)findViewById(R.id.txt_fecha_fin_hora_crear_corte_programado)).getText().toString();
        String mins_fin = ((EditText)findViewById(R.id.txt_fecha_fin_minutos_crear_corte_programado)).getText().toString();

        if(dia_inicio.isEmpty() || mes_inicio.isEmpty() || anio_inicio.isEmpty() || hora_inicio.isEmpty() || mins_inicio.isEmpty()){
            Toast.makeText(this, "Debe completar la fecha y hora de inicio", Toast.LENGTH_LONG).show();
            return false;
        }

        if(dia_fin.isEmpty() || mes_fin.isEmpty() || anio_fin.isEmpty() || hora_fin.isEmpty() || mins_fin.isEmpty()){
            Toast.makeText(this, "Debe completar la fecha y hora de finalizado", Toast.LENGTH_LONG).show();
            return false;
        }

        String str_fecha_inicio = anio_inicio + "-" + mes_inicio + "-" + dia_inicio + " " + hora_inicio + ":" + mins_inicio;
        String str_fecha_fin = anio_fin + "-" + mes_fin + "-" + dia_fin + " " + hora_fin + ":" + mins_fin;

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            df.setLenient(false);
            df.parse(str_fecha_inicio);
        } catch (ParseException e) {
            Toast.makeText(this, "Error en el formato de fecha de inicio", Toast.LENGTH_LONG).show();
            return false;
        }

        try {
            df.setLenient(false);
            df.parse(str_fecha_fin);
        } catch (ParseException e) {
            Toast.makeText(this, "Error en el formato de fecha de fin", Toast.LENGTH_LONG).show();
            return false;
        }

        fecha_inicio.append(str_fecha_inicio);
        fecha_fin.append(str_fecha_fin);

        return true;
    }

    @Override
    public void actualizarPosicionActual(LatLng posicion)
    {
        this.posicion_gps = posicion;

        if (map_crear_corte_programado != null && this.marcador_posicion_corte_programado == null)
        {
            this.actualizarMapaCrearCorteProgramado(posicion);
        }
    }

    @Override
    public void onMapLongClick(LatLng point) {
        this.actualizarMapaCrearCorteProgramado(point);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        this.textview_km_radio.setText(String.valueOf(progress) + "m");

        if(this.radio_corte_programado != null)
        {
            this.radio_corte_programado.setRadius(progress);
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

    private void actualizarMapaCrearCorteProgramado(LatLng posicion_marcador)
    {
        if (map_crear_corte_programado == null) {
            return;
        }

        if(this.marcador_posicion_corte_programado == null) {

            // Agrego marcador
            this.marcador_posicion_corte_programado = map_crear_corte_programado.addMarker(new MarkerOptions()
                    .position(posicion_marcador));

            // Agrego radio
            this.radio_corte_programado = map_crear_corte_programado.addCircle(new CircleOptions()
                    .center(posicion_marcador)
                    .radius(seekbar_radio.getProgress())
                    .strokeColor(Constantes.STROKE_COLOR_CIRCLE)
                    .fillColor(Constantes.COLOR_CIRCLE)
                    .strokeWidth(5));
        }
        else
        {
            // Si el marcador ya fue agregado y solo hay que actualizar posicion
            this.marcador_posicion_corte_programado.setPosition(posicion_marcador);

            this.radio_corte_programado.setCenter(posicion_marcador);
            this.radio_corte_programado.setRadius(seekbar_radio.getProgress());
        }
    }

    public void centrarMapaEnMarcador(View view)
    {
        if(this.marcador_posicion_corte_programado != null) {
            this.map_crear_corte_programado.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(this.marcador_posicion_corte_programado.getPosition().latitude,
                            this.marcador_posicion_corte_programado.getPosition().longitude),
                    14.0f));
        }
    }

    public void marcarMapaEnPosicionGPS(View view)
    {
        if(this.posicion_gps != null)
        {
            this.actualizarMapaCrearCorteProgramado(this.posicion_gps);
            this.centrarMapaEnMarcador(view);
        }
    }
}