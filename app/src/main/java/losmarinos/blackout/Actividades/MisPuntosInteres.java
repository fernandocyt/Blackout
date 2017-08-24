package losmarinos.blackout.Actividades;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import losmarinos.blackout.Adapters.PuntoInteresAdapter;
import losmarinos.blackout.Adapters.ReportesAdapter;
import losmarinos.blackout.Adapters.RespuestaAdapter;
import losmarinos.blackout.Constantes;
import losmarinos.blackout.ConsultorPOSTAPI;
import losmarinos.blackout.Global;
import losmarinos.blackout.Objetos.PuntoInteres;
import losmarinos.blackout.Objetos.Reporte;
import losmarinos.blackout.R;

public class MisPuntosInteres extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap map_mis_puntos_interes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Mis Puntos de Interes");
        setContentView(R.layout.activity_mis_objetos);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_mis_objetos);
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
        ListView mi_lista = (ListView)findViewById(R.id.lst_objetos_mis_objetos);
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

}
