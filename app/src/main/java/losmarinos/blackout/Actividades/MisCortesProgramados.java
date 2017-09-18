package losmarinos.blackout.Actividades;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import losmarinos.blackout.Adapters.CorteProgramadoAdapter;
import losmarinos.blackout.Adapters.PuntoInteresAdapter;
import losmarinos.blackout.Constantes;
import losmarinos.blackout.ConsultorDELETEAPI;
import losmarinos.blackout.Global;
import losmarinos.blackout.Objetos.Corte;
import losmarinos.blackout.Objetos.Empresa;
import losmarinos.blackout.Objetos.PuntoInteres;
import losmarinos.blackout.ParserJSON;
import losmarinos.blackout.R;

import static losmarinos.blackout.Constantes.TAGAPI.BORRAR_CORTE_PROGRAMADO;
import static losmarinos.blackout.Constantes.TAGAPI.BORRAR_PUNTO_DE_INTERES;
import static losmarinos.blackout.Global.token_usuario_actual;

public class MisCortesProgramados extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    GoogleMap map_mis_cortes_programados;
    Empresa empresaActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Mis Cortes Programados");
        setContentView(R.layout.activity_mis_cortes_programados);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_mis_cortes_programados);
        mapFragment.getMapAsync(this);

        empresaActual = Global.encontrarEmpresaPorId(Global.usuario_actual.getSubId());

        this.cargarListView();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map_mis_cortes_programados = googleMap;
        map_mis_cortes_programados.setOnMarkerClickListener(this);
        map_mis_cortes_programados.animateCamera(CameraUpdateFactory.newLatLngZoom(Constantes.BSAS, 11.0f));

        this.cargarMapa();
    }

    public void centrarMapaEnPosicion(LatLng posicion)
    {
        map_mis_cortes_programados.animateCamera(CameraUpdateFactory.newLatLngZoom(posicion, 13.0f));
    }

    public void cargarListView(){
        CorteProgramadoAdapter adapter = new CorteProgramadoAdapter(empresaActual.obtenerCortesProgramados(), this, this);
        ListView mi_lista = (ListView)findViewById(R.id.lst_cortes_mis_cortes_programados);
        mi_lista.setAdapter(adapter);
    }

    public void borrarCorteProgramado(int id_corte_programado)
    {
        try{
            String resultado = new ConsultorDELETEAPI("corte-programado/" + String.valueOf(id_corte_programado), token_usuario_actual, BORRAR_CORTE_PROGRAMADO, null).execute().get();
            StringBuilder mensaje_error = new StringBuilder();
            if(ParserJSON.esError(resultado, mensaje_error)){
                Toast.makeText(this, mensaje_error, Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
        }
    }

    public void cargarMapa()
    {
        map_mis_cortes_programados.clear();

        List<Corte> cortes_programados = empresaActual.obtenerCortesProgramados();
        for(int i = 0; i < cortes_programados.size(); i++)
        {
            Corte corte_prog_actual = cortes_programados.get(i);

            map_mis_cortes_programados.addMarker(new MarkerOptions()
                    .position(corte_prog_actual.getUbicacion())
                    .title("Corte Programado")
                    .icon(Constantes.getIconoCorte(corte_prog_actual.getServicio(), true, false)))
                    .setTag(corte_prog_actual);

            map_mis_cortes_programados.addCircle(new CircleOptions()
                    .center(corte_prog_actual.getUbicacion())
                    .radius(corte_prog_actual.getRadio())
                    .strokeColor(Constantes.STROKE_COLOR_CIRCLE)
                    .fillColor(Constantes.COLOR_CIRCLE)
                    .strokeWidth(5));
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if(marker.getTitle().equals("Corte Programado"))
        {
            Corte corte_seleccionado = (Corte)marker.getTag();

            Intent i = new Intent(getApplicationContext(), PerfilCorte.class);
            i.putExtra("idCorte", corte_seleccionado.getId());
            try {
                startActivity(i);
            }
            catch (Exception e)
            {
                throw e;
            }
        }
        return true;
    }

}