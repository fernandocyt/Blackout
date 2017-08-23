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
import losmarinos.blackout.Adapters.SucursalAdapter;
import losmarinos.blackout.Constantes;
import losmarinos.blackout.Global;
import losmarinos.blackout.Objetos.Empresa;
import losmarinos.blackout.Objetos.Reporte;
import losmarinos.blackout.Objetos.Sucursal;
import losmarinos.blackout.R;

public class VerSucursales extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap map_sucursales;
    Empresa empresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_objetos);

        int id_empresa = getIntent().getIntExtra("idEmpresa", 0);
        for(int i = 0; i < Global.empresas.size(); i++)
        {
            if(id_empresa == Global.empresas.get(i).getId())
            {
                this.empresa = Global.empresas.get(i);
            }
        }

        setTitle("Sucursales " + empresa.getNombre());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_mis_objetos);
        mapFragment.getMapAsync(this);

        this.cargarListView();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map_sucursales = googleMap;
        map_sucursales.animateCamera(CameraUpdateFactory.newLatLngZoom(Constantes.BSAS, 11.0f));

        this.cargarMapa();
    }

    public void centrarMapaEnPosicion(LatLng posicion)
    {
        map_sucursales.animateCamera(CameraUpdateFactory.newLatLngZoom(posicion, 13.0f));
    }

    public void cargarListView(){
        //Crea el adaptador de alarmas
        SucursalAdapter adapter = new SucursalAdapter(empresa.getSucursales(), this, this);

        //enlaza el list view del layout a la variable
        ListView mi_lista = (ListView)findViewById(R.id.lst_objetos_mis_objetos);

        //Le setea el adaptador a la lista
        mi_lista.setAdapter(adapter);
    }

    public void cargarMapa()
    {
        map_sucursales.clear();

        List<Sucursal> sucursales = empresa.getSucursales();
        for(int i = 0; i < sucursales.size(); i++)
        {
            Sucursal sucursal_actual = sucursales.get(i);

            map_sucursales.addMarker(new MarkerOptions()
                    .position(sucursal_actual.getUbicacion())
                    .title("Sucursal")
                    .icon(Constantes.getIconoReporte(empresa.getTipoServicio()))); //Poner el de sucursales

        }
    }

}
