package losmarinos.blackout.Actividades;

import android.app.ProgressDialog;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
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
import java.util.List;
import java.util.Locale;

import losmarinos.blackout.Aviso;
import losmarinos.blackout.Constantes;
import losmarinos.blackout.ConsultorPOSTAPI;
import losmarinos.blackout.GPSTracker;
import losmarinos.blackout.Global;
import losmarinos.blackout.Objetos.Empresa;
import losmarinos.blackout.ObservadorGPS;
import losmarinos.blackout.ParserJSON;
import losmarinos.blackout.R;

import static losmarinos.blackout.Constantes.TAGAPI.REGISTRAR_CORTE_PROGRAMADO;
import static losmarinos.blackout.Constantes.TAGAPI.REGISTRAR_SUCURSAL;

public class CrearSucursal extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener{

    Empresa empresa_actual;

    // Mapa
    GoogleMap map_crear_sucursal = null;
    Marker marcador_posicion_sucursal = null;

    // Interfaz
    TextView textview_telefono;
    TextView textview_direccion;

    Geocoder geocoder;

    ProgressDialog progress_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Agregar sucursal");
        setContentView(R.layout.activity_crear_sucursal);

        empresa_actual = Global.encontrarEmpresaPorId(Global.usuario_actual.getSubId());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_crear_sucursal);
        mapFragment.getMapAsync(this);

        this.textview_direccion = (TextView)findViewById(R.id.txt_direccion_crear_sucursal);
        this.textview_telefono = (TextView)findViewById(R.id.txt_telefono_crear_sucursal);

        geocoder = new Geocoder(this, Locale.getDefault());

        Toast.makeText(this, "Para seleccionar ubicación mantener presionado el mapa", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map_crear_sucursal = googleMap;
        map_crear_sucursal.setOnMapLongClickListener(this);
        map_crear_sucursal.animateCamera(CameraUpdateFactory.newLatLngZoom(Constantes.BSAS, 11.0f));
    }

    public void crearSucursal(View view)
    {
        if(this.marcador_posicion_sucursal == null){
            Toast.makeText(this, "Debe marcar una posición en el mapa", Toast.LENGTH_LONG).show();
            return;
        }

        LatLng posicion = marcador_posicion_sucursal.getPosition();

        progress_dialog = Aviso.showProgressDialog(this, "Creando sucursal...");

        final String f_telefono = textview_telefono.getText().toString();
        final String f_direccion = textview_direccion.getText().toString();
        final LatLng f_posicion = posicion;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    JSONObject nueva_sucursal = ParserJSON.crearJSONSucursal(empresa_actual.getSubId(), f_telefono, f_direccion, f_posicion);

                    String resultado = new ConsultorPOSTAPI("sucursal", Global.token_usuario_actual, nueva_sucursal, REGISTRAR_SUCURSAL, null).execute().get();
                    StringBuilder mensaje_error = new StringBuilder();
                    if(ParserJSON.esError(resultado, mensaje_error)){
                        Aviso.hideProgressDialog(CrearSucursal.this, progress_dialog);
                        Aviso.showToast(CrearSucursal.this, mensaje_error.toString());
                    }else{
                        Aviso.hideProgressDialog(CrearSucursal.this, progress_dialog);
                        Aviso.showToast(CrearSucursal.this, "Sucursal creada exitosamente");
                        marcador_posicion_sucursal = null;
                        map_crear_sucursal = null;
                        CrearSucursal.this.finish();
                    }
                }catch (Exception e){
                    Aviso.hideProgressDialog(CrearSucursal.this, progress_dialog);
                    Aviso.showToast(CrearSucursal.this, "Error");
                }


            }
        }).start();
    }

    @Override
    public void onMapLongClick(LatLng point) {
        this.actualizarMapaCrearSucursal(point);
    }

    private void actualizarMapaCrearSucursal(LatLng posicion_marcador)
    {
        if (map_crear_sucursal == null) {
            return;
        }

        if(this.marcador_posicion_sucursal == null) {

            // Agrego marcador
            this.marcador_posicion_sucursal = map_crear_sucursal.addMarker(new MarkerOptions()
                    .position(posicion_marcador));

        }
        else
        {
            // Si el marcador ya fue agregado y solo hay que actualizar posicion
            this.marcador_posicion_sucursal.setPosition(posicion_marcador);

        }

        try {
            List<Address> addresses = geocoder.getFromLocation(posicion_marcador.latitude, posicion_marcador.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0);
            String[] separado_coma = address.split(",");
            if(separado_coma.length > 0) {
                textview_direccion.setText(separado_coma[0]);
            }else{
                textview_direccion.setText(address);
            }
        }catch (Exception e){

        }
    }

    public void centrarMapaEnMarcador(View view)
    {
        if(this.marcador_posicion_sucursal != null) {
            this.map_crear_sucursal.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(this.marcador_posicion_sucursal.getPosition().latitude,
                            this.marcador_posicion_sucursal.getPosition().longitude),
                    14.0f));
        }
    }
}