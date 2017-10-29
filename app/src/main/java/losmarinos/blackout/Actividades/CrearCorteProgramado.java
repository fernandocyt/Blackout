package losmarinos.blackout.Actividades;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import losmarinos.blackout.Aviso;
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
import static losmarinos.blackout.Constantes.RADIO_MINIMO;
import static losmarinos.blackout.Constantes.TAGAPI.REGISTRAR_CORTE_PROGRAMADO;
import static losmarinos.blackout.Constantes.TAGAPI.REGISTRAR_REPORTE;

public class CrearCorteProgramado extends AppCompatActivity implements OnMapReadyCallback,
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
    TextView textview_fecha_inicio;
    TextView textview_hora_inicio;
    TextView textview_fecha_fin;
    TextView textview_hora_fin;

    DatePickerDialog.OnDateSetListener date_inicio;
    DatePickerDialog.OnDateSetListener date_fin;
    TimePickerDialog.OnTimeSetListener time_inicio;
    TimePickerDialog.OnTimeSetListener time_fin;

    Calendar calendar;
    Calendar calendar_inicio;
    Calendar calendar_fin;

    ProgressDialog progress_dialog;

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
        this.seekbar_radio.setMax(10000);
        this.seekbar_radio.setProgress(RADIO_MINIMO);

        calendar = Calendar.getInstance();
        calendar_inicio = Calendar.getInstance();
        calendar_fin = Calendar.getInstance();

        date_inicio = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar_inicio.set(Calendar.YEAR, year);
                calendar_inicio.set(Calendar.MONTH, monthOfYear);
                calendar_inicio.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                textview_fecha_inicio.setText(df.format(calendar_inicio.getTime()));
            }
        };
        date_fin = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar_fin.set(Calendar.YEAR, year);
                calendar_fin.set(Calendar.MONTH, monthOfYear);
                calendar_fin.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                textview_fecha_fin.setText(df.format(calendar_fin.getTime()));
            }
        };
        time_inicio = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                calendar_inicio.set(Calendar.HOUR_OF_DAY, selectedHour);
                calendar_inicio.set(Calendar.MINUTE, selectedMinute);

                DateFormat df = new SimpleDateFormat("HH:mm");
                textview_hora_inicio.setText(df.format(calendar_inicio.getTime()));
            }
        };
        time_fin = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                calendar_fin.set(Calendar.HOUR_OF_DAY, selectedHour);
                calendar_fin.set(Calendar.MINUTE, selectedMinute);

                DateFormat df = new SimpleDateFormat("HH:mm");
                textview_hora_fin.setText(df.format(calendar_fin.getTime()));
            }
        };

        DateFormat df_fecha = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat df_hora = new SimpleDateFormat("HH:mm");

        this.textview_fecha_inicio = (TextView)findViewById(R.id.lbl_fecha_inicio_crear_corte_programado);
        this.textview_fecha_inicio.setText(df_fecha.format(calendar_inicio.getTime()));
        this.textview_fecha_inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(CrearCorteProgramado.this,
                        date_inicio,
                        calendar_inicio.get(Calendar.YEAR),
                        calendar_inicio.get(Calendar.MONTH),
                        calendar_inicio.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });

        this.textview_fecha_fin = (TextView)findViewById(R.id.lbl_fecha_fin_crear_corte_programado);
        this.textview_fecha_fin.setText(df_fecha.format(calendar_fin.getTime()));
        this.textview_fecha_fin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(CrearCorteProgramado.this,
                        date_fin,
                        calendar_fin.get(Calendar.YEAR),
                        calendar_fin.get(Calendar.MONTH),
                        calendar_fin.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });

        this.textview_hora_inicio = (TextView)findViewById(R.id.lbl_hora_inicio_crear_corte_programado);
        this.textview_hora_inicio.setText(df_hora.format(calendar_inicio.getTime()));
        this.textview_hora_inicio.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 new TimePickerDialog(CrearCorteProgramado.this,
                 time_inicio,
                 calendar_inicio.get(Calendar.HOUR_OF_DAY),
                 calendar_inicio.get(Calendar.MINUTE),
                 true // es de 24 hs
                 ).show();
             }
         });

        this.textview_hora_fin = (TextView)findViewById(R.id.lbl_hora_fin_crear_corte_programado);
        this.textview_hora_fin.setText(df_hora.format(calendar_fin.getTime()));
        this.textview_hora_fin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(CrearCorteProgramado.this,
                        time_fin,
                        calendar_fin.get(Calendar.HOUR_OF_DAY),
                        calendar_fin.get(Calendar.MINUTE),
                        true // es de 24 hs
                ).show();
            }
        });

        Toast.makeText(this, "Para seleccionar ubicación mantener presionado el mapa", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map_crear_corte_programado = googleMap;
        map_crear_corte_programado.setOnMapLongClickListener(this);
        map_crear_corte_programado.animateCamera(CameraUpdateFactory.newLatLngZoom(Constantes.BSAS, 11.0f));
    }

    public void crearCorteProgramado(View view)
    {
        if(this.marcador_posicion_corte_programado == null){
            Toast.makeText(this, "Debe marcar una posición en el mapa", Toast.LENGTH_LONG).show();
            return;
        }

        Constantes.SERVICIO servicio = empresa_actual.getTipoServicio();

        LatLng posicion = marcador_posicion_corte_programado.getPosition();
        int radio = seekbar_radio.getProgress();
        if(radio < RADIO_MINIMO){
            radio = RADIO_MINIMO;
        }

        StringBuilder fecha_inicio = new StringBuilder();
        StringBuilder fecha_fin = new StringBuilder();

        if(!this.construirFechas(fecha_inicio, fecha_fin)){
            return;
        }

        progress_dialog = Aviso.showProgressDialog(this, "Creando corte programado...");

        final Constantes.SERVICIO f_servicio = servicio;
        final int f_id_empresa = empresa_actual.getSubId();
        final LatLng f_posicion = posicion;
        final int f_radio = radio;
        final String f_fecha_inicio = fecha_inicio.toString();
        final String f_fecha_fin = fecha_fin.toString();

        new Thread(new Runnable() {
            @Override
            public void run() {
            try{
                JSONObject nuevo_corte_prog = ParserJSON.crearJSONCorteProgramado(f_servicio, f_id_empresa, f_posicion, f_radio, f_fecha_inicio, f_fecha_fin);

                String resultado = new ConsultorPOSTAPI("corte-programado", Global.token_usuario_actual, nuevo_corte_prog, REGISTRAR_CORTE_PROGRAMADO, null).execute().get();
                StringBuilder mensaje_error = new StringBuilder();
                if(ParserJSON.esError(resultado, mensaje_error)){
                    Aviso.hideProgressDialog(CrearCorteProgramado.this, progress_dialog);
                    Aviso.showToast(CrearCorteProgramado.this, mensaje_error.toString());
                }else{
                    Aviso.hideProgressDialog(CrearCorteProgramado.this, progress_dialog);
                    Aviso.showToast(CrearCorteProgramado.this, "Corte programado creado exitosamente");
                    radio_corte_programado = null;
                    marcador_posicion_corte_programado = null;
                    map_crear_corte_programado = null;
                    finish();
                }
            }catch (Exception e){
                Aviso.hideProgressDialog(CrearCorteProgramado.this, progress_dialog);
                Aviso.showToast(CrearCorteProgramado.this, "Error");
            }
            }
        }).start();
    }

    public boolean construirFechas(StringBuilder fecha_inicio, StringBuilder fecha_fin){

        String str_fecha_inicio = textview_fecha_inicio.getText().toString() + " " + textview_hora_inicio.getText().toString();
        String str_fecha_fin = textview_fecha_fin.getText().toString() + " " + textview_hora_fin.getText().toString();

        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Date date_inicio = null;
        Date date_fin = null;

        try {
            //df.setLenient(false);
            date_inicio = df.parse(str_fecha_inicio);
        } catch (ParseException e) {
            Toast.makeText(this, "Error en el formato de fecha de inicio", Toast.LENGTH_LONG).show();
            return false;
        }

        try {
            //df.setLenient(false);
            date_fin = df.parse(str_fecha_fin);
        } catch (ParseException e) {
            Toast.makeText(this, "Error en el formato de fecha de fin", Toast.LENGTH_LONG).show();
            return false;
        }

        if(date_inicio.after(date_fin)){
            Toast.makeText(this, "Error: La fecha de inicio debe ser anterior a la de fin", Toast.LENGTH_LONG).show();
            return false;
        }

        Date actual = Calendar.getInstance().getTime();
        long diferencia_inicio = (date_inicio.getTime() - actual.getTime()) / 1000 / 60 / 60;
        if(diferencia_inicio < 24){
            Toast.makeText(this, "Error: La fecha de inicio debe ser por lo menos dentro de 24 horas", Toast.LENGTH_LONG).show();
            return false;
        }

        DateFormat df_base = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        fecha_inicio.append(df_base.format(date_inicio));
        fecha_fin.append(df_base.format(date_fin));

        return true;
    }

    @Override
    public void onMapLongClick(LatLng point) {
        this.actualizarMapaCrearCorteProgramado(point);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(progress < RADIO_MINIMO){
            progress = RADIO_MINIMO;
        }

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

            int radio = seekbar_radio.getProgress();
            if(radio < RADIO_MINIMO){
                radio = RADIO_MINIMO;
            }

            // Agrego radio
            this.radio_corte_programado = map_crear_corte_programado.addCircle(new CircleOptions()
                    .center(posicion_marcador)
                    .radius(radio)
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
}