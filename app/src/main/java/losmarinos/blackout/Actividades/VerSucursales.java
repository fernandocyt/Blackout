package losmarinos.blackout.Actividades;

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
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import losmarinos.blackout.Adapters.ReportesAdapter;
import losmarinos.blackout.Adapters.SucursalAdapter;
import losmarinos.blackout.Constantes;
import losmarinos.blackout.ConsultorGETAPI;
import losmarinos.blackout.Global;
import losmarinos.blackout.Objetos.Comentario;
import losmarinos.blackout.Objetos.Empresa;
import losmarinos.blackout.Objetos.Reporte;
import losmarinos.blackout.Objetos.Sucursal;
import losmarinos.blackout.ParserJSON;
import losmarinos.blackout.R;

import static losmarinos.blackout.Constantes.TAGAPI.OBTENER_COMENTARIOS_POR_EMPRESA;
import static losmarinos.blackout.Constantes.TAGAPI.OBTENER_SUCURSALES_POR_EMPRESA;

public class VerSucursales extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap map_sucursales;
    Empresa empresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_sucursales);

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
                .findFragmentById(R.id.map_ver_sucursales);
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

        List<Comentario> comentarios = new ArrayList<>();
        try {
            String respuesta = new ConsultorGETAPI("empresa/"+String.valueOf(this.empresa.getId())+"/sucursales",
                    Global.token_usuario_actual, OBTENER_SUCURSALES_POR_EMPRESA, null).execute().get();
            StringBuilder msg_error = new StringBuilder();
            if(ParserJSON.esError(respuesta, msg_error)){
                Toast.makeText(this, "No es posible cargar sucursales", Toast.LENGTH_LONG).show();
                return;
            }else{
                empresa.setSucursales(ParserJSON.obtenerSucursales(respuesta));
            }
        }catch (Exception e){}

        SucursalAdapter adapter = new SucursalAdapter(empresa.getSucursales(), this, this);
        ListView mi_lista = (ListView)findViewById(R.id.lst_sucursal_ver_sucursales);
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
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icono_sucursal)));

        }
    }


}
