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

import losmarinos.blackout.Adapters.ReportesAdapter;
import losmarinos.blackout.Adapters.RespuestaAdapter;
import losmarinos.blackout.Constantes;
import losmarinos.blackout.ConsultorAPI;
import losmarinos.blackout.Objetos.Reporte;
import losmarinos.blackout.R;

public class MisReportes extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap map_mis_reportes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Mis Reportes");
        setContentView(R.layout.activity_mis_reportes);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_mis_reportes);
        mapFragment.getMapAsync(this);

        this.cargarListView();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map_mis_reportes = googleMap;
        map_mis_reportes.animateCamera(CameraUpdateFactory.newLatLngZoom(Constantes.BSAS, 11.0f));

        this.cargarReportesEnMapa();
    }

    public void centrarMapaEnPosicion(LatLng posicion)
    {
        map_mis_reportes.animateCamera(CameraUpdateFactory.newLatLngZoom(posicion, 13.0f));
    }

    public void cargarListView(){
        //Crea el adaptador de alarmas
        ReportesAdapter adapter = new ReportesAdapter(ConsultorAPI.reportes, this, this);

        //enlaza el list view del layout a la variable
        ListView mi_lista = (ListView)findViewById(R.id.lst_reportes_mis_reportes);

        //Le setea el adaptador a la lista
        mi_lista.setAdapter(adapter);
    }

    public void cargarReportesEnMapa()
    {
        List<Reporte> reportes = ConsultorAPI.reportes;
        for(int i = 0; i < reportes.size(); i++)
        {
            Reporte rep_actual = reportes.get(i);

            map_mis_reportes.addMarker(new MarkerOptions()
                    .position(rep_actual.getUbicacion())
                    .title("Reporte")
                    .icon(Constantes.getIconoReporte(rep_actual.getServicio())));

            map_mis_reportes.addCircle(new CircleOptions()
                    .center(rep_actual.getUbicacion())
                    .radius(rep_actual.getRadio())
                    .strokeColor(Constantes.STROKE_COLOR_CIRCLE)
                    .fillColor(Constantes.COLOR_CIRCLE)
                    .strokeWidth(5));
        }
    }

}
