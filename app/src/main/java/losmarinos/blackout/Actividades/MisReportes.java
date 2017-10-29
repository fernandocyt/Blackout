package losmarinos.blackout.Actividades;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import losmarinos.blackout.Adapters.ReportesAdapter;
import losmarinos.blackout.Adapters.RespuestaAdapter;
import losmarinos.blackout.Aviso;
import losmarinos.blackout.Constantes;
import losmarinos.blackout.ConsultorPOSTAPI;
import losmarinos.blackout.Global;
import losmarinos.blackout.Objetos.Reporte;
import losmarinos.blackout.R;

public class MisReportes extends AppCompatActivity implements OnMapReadyCallback, CompoundButton.OnCheckedChangeListener{

    GoogleMap map_mis_reportes = null;
    Switch switch_ver_historico;

    ProgressDialog progress_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Mis Reportes");
        setContentView(R.layout.activity_mis_reportes);

        progress_dialog = Aviso.showProgressDialog(this, "Cargando reportes...");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_mis_reportes);
        mapFragment.getMapAsync(this);

        switch_ver_historico = (Switch)findViewById(R.id.switch_ver_historico_mis_reportes);
        switch_ver_historico.setOnCheckedChangeListener(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean correcto = Global.usuario_actual.actualizarReportes(MisReportes.this);
                if(correcto) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cargarListView();
                            cargarMapa();
                            Aviso.hideProgressDialog(MisReportes.this, progress_dialog);
                        }
                    });
                }else{
                    Aviso.showToast(MisReportes.this, "Error al obtener reportes");
                    MisReportes.this.finish();
                }
            }
        }).start();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map_mis_reportes = googleMap;
        map_mis_reportes.animateCamera(CameraUpdateFactory.newLatLngZoom(Constantes.BSAS, 11.0f));

        this.cargarMapa();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        this.cargarListView();
        this.cargarMapa();
    }

    public void centrarMapaEnPosicion(LatLng posicion)
    {
        map_mis_reportes.animateCamera(CameraUpdateFactory.newLatLngZoom(posicion, 13.0f));
    }

    public void cargarListView(){
        List<Reporte> reportes = this.obtenerReportesAMostrar();

        TextView no_reportes = (TextView)findViewById(R.id.txt_no_reportes_mis_reportes);
        if(reportes.size() == 0){
            no_reportes.setVisibility(View.VISIBLE);
        }else{
            no_reportes.setVisibility(View.GONE);
        }

        ReportesAdapter adapter = new ReportesAdapter(reportes, this, this);
        ListView mi_lista = (ListView)findViewById(R.id.lst_reportes_mis_reportes);
        mi_lista.setAdapter(adapter);
    }

    public void cargarMapa()
    {
        if(map_mis_reportes == null){
            return;
        }

        map_mis_reportes.clear();

        List<Reporte> reportes = this.obtenerReportesAMostrar();

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

    public List<Reporte> obtenerReportesAMostrar()
    {
        List<Reporte> reportes = Global.usuario_actual.getReportes();

        if(!switch_ver_historico.isChecked()){
            List<Reporte> reportes_mostrar = new ArrayList<>();
            for(int i = 0; i < reportes.size(); i++){
                if(!reportes.get(i).isResuelto()){
                    reportes_mostrar.add(reportes.get(i));
                }
            }
            return reportes_mostrar;
        }
        else
        {
            return reportes;
        }

    }

}
