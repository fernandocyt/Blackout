package losmarinos.blackout.Actividades;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.List;

import losmarinos.blackout.Adapters.PuntoInteresAdapter;
import losmarinos.blackout.Adapters.ReportesAdapter;
import losmarinos.blackout.Adapters.RespuestaAdapter;
import losmarinos.blackout.Constantes;
import losmarinos.blackout.ConsultorDELETEAPI;
import losmarinos.blackout.ConsultorPOSTAPI;
import losmarinos.blackout.Global;
import losmarinos.blackout.Objetos.PuntoInteres;
import losmarinos.blackout.Objetos.Reporte;
import losmarinos.blackout.ParserJSON;
import losmarinos.blackout.R;

import static losmarinos.blackout.Constantes.TAGAPI.BORRAR_PUNTO_DE_INTERES;
import static losmarinos.blackout.Constantes.TAGAPI.REGISTRAR_PUNTO_DE_INTERES;
import static losmarinos.blackout.Global.token_usuario_actual;

public class MisPuntosInteres extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap map_mis_puntos_interes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Mis Puntos de Interes");
        setContentView(R.layout.activity_mis_puntos_de_interes);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_mis_puntos_de_interes);
        mapFragment.getMapAsync(this);

        this.cargarListView();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map_mis_puntos_interes = googleMap;
        map_mis_puntos_interes.animateCamera(CameraUpdateFactory.newLatLngZoom(Constantes.BSAS, 11.0f));

        this.cargarMapa();
    }

    public void centrarMapaEnPosicion(LatLng posicion)
    {
        map_mis_puntos_interes.animateCamera(CameraUpdateFactory.newLatLngZoom(posicion, 13.0f));
    }

    public void cargarListView(){
        PuntoInteresAdapter adapter = new PuntoInteresAdapter(Global.usuario_actual.getPuntosInteres(), this, this);
        ListView mi_lista = (ListView)findViewById(R.id.lst_puntos_mis_puntos_de_interes);
        mi_lista.setAdapter(adapter);
    }

    public void cargarMapa()
    {
        map_mis_puntos_interes.clear();

        List<PuntoInteres> puntos_interes = Global.usuario_actual.getPuntosInteres();
        for(int i = 0; i < puntos_interes.size(); i++)
        {
            PuntoInteres pi_actual = puntos_interes.get(i);

            map_mis_puntos_interes.addMarker(new MarkerOptions()
                    .position(pi_actual.getUbicacion())
                    .title("PuntoInteres"));

            map_mis_puntos_interes.addCircle(new CircleOptions()
                    .center(pi_actual.getUbicacion())
                    .radius(pi_actual.getRadio())
                    .strokeColor(Constantes.STROKE_COLOR_CIRCLE)
                    .fillColor(Constantes.COLOR_CIRCLE)
                    .strokeWidth(5));
        }
    }

    public void borrarPuntoDeInteres(int id_punto_interes)
    {
        try{
            String resultado = new ConsultorDELETEAPI("punto-de-interes/" + String.valueOf(id_punto_interes) + "/delete", token_usuario_actual, BORRAR_PUNTO_DE_INTERES, null).execute().get();
            StringBuilder mensaje_error = new StringBuilder();
            if(ParserJSON.esError(resultado, mensaje_error)){
                Toast.makeText(this, mensaje_error, Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
        }
    }
}
