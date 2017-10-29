package losmarinos.blackout.Actividades;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
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
import losmarinos.blackout.Aviso;
import losmarinos.blackout.Constantes;
import losmarinos.blackout.ConsultorDELETEAPI;
import losmarinos.blackout.ConsultorGETAPI;
import losmarinos.blackout.Global;
import losmarinos.blackout.Objetos.Comentario;
import losmarinos.blackout.Objetos.Empresa;
import losmarinos.blackout.Objetos.Reporte;
import losmarinos.blackout.Objetos.Sucursal;
import losmarinos.blackout.ParserJSON;
import losmarinos.blackout.R;

import static losmarinos.blackout.Constantes.TAGAPI.BORRAR_CORTE_PROGRAMADO;
import static losmarinos.blackout.Constantes.TAGAPI.BORRAR_SUCURSAL;
import static losmarinos.blackout.Constantes.TAGAPI.OBTENER_COMENTARIOS_POR_EMPRESA;
import static losmarinos.blackout.Constantes.TAGAPI.OBTENER_SUCURSALES_POR_EMPRESA;
import static losmarinos.blackout.Global.token_usuario_actual;

public class VerSucursales extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap map_sucursales;
    Empresa empresa;

    ProgressDialog progress_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_sucursales);

        progress_dialog = Aviso.showProgressDialog(this, "Cargando sucursales...");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_ver_sucursales);
        mapFragment.getMapAsync(this);

        int id_empresa = getIntent().getIntExtra("idEmpresa", 0);
        this.empresa = Global.encontrarEmpresaPorId(id_empresa);

        setTitle("Sucursales " + empresa.getNombre());

        this.empresa.actualizarSucursales(this);

        this.cargarListView();

        new Thread(new Runnable() {
            @Override
            public void run() {
                empresa.actualizarSucursales(VerSucursales.this);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cargarListView();
                        cargarMapa();
                        Aviso.hideProgressDialog(VerSucursales.this, progress_dialog);
                    }
                });
            }
        }).start();
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
        List<Sucursal> suc = empresa.getSucursales();

        TextView no_sucursales = (TextView)findViewById(R.id.txt_no_sucursales_ver_sucursales);
        if(suc.size() == 0){
            no_sucursales.setVisibility(View.VISIBLE);
        }else{
            no_sucursales.setVisibility(View.GONE);
        }

        SucursalAdapter adapter = new SucursalAdapter(empresa, this, this);
        ListView mi_lista = (ListView)findViewById(R.id.lst_sucursal_ver_sucursales);
        mi_lista.setAdapter(adapter);
    }

    public void cargarMapa()
    {
        if(map_sucursales == null){
            return;
        }

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

    public boolean borrarSucursal(int id_sucursal)
    {
        try{
            String resultado = new ConsultorDELETEAPI("sucursal/" + String.valueOf(id_sucursal) + "/delete", token_usuario_actual, BORRAR_SUCURSAL, null).execute().get();
            StringBuilder mensaje_error = new StringBuilder();
            if(ParserJSON.esError(resultado, mensaje_error)){
                return false;
            }else{
                return true;
            }
        }catch (Exception e){
            return false;
        }
    }


}
