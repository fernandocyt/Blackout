package losmarinos.blackout.Actividades;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;

import losmarinos.blackout.Constantes;
import losmarinos.blackout.ConsultorAPI;
import losmarinos.blackout.Global;
import losmarinos.blackout.Objetos.Corte;
import losmarinos.blackout.Objetos.Respuesta;
import losmarinos.blackout.R;
import losmarinos.blackout.Adapters.RespuestaAdapter;

public class PerfilCorte extends AppCompatActivity {

    TextView textview_servicio;
    TextView textview_empresa;
    TextView textview_fecha;
    TextView textview_cantidad_reportes;
    EditText edittext_respuesta;

    Corte corte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Perfil de corte");
        setContentView(R.layout.activity_perfil_corte);

        int id_corte = getIntent().getIntExtra("idCorte", 0);
        for(int i = 0; i < Global.cortes.size(); i++)
        {
            if(id_corte == Global.cortes.get(i).getId())
            {
                this.corte = Global.cortes.get(i);
            }
        }
        //corte = ConsultorAPI.cortes.get(0);

        textview_servicio = (TextView)findViewById(R.id.lbl_servicio_perfil_corte);
        textview_empresa = (TextView)findViewById(R.id.lbl_empresa_perfil_corte);
        textview_fecha = (TextView)findViewById(R.id.lbl_fecha_inicio_perfil_corte);
        textview_cantidad_reportes = (TextView)findViewById(R.id.lbl_cant_reportes_perfil_corte);
        edittext_respuesta = (EditText)findViewById(R.id.txt_respuesta_perfil_corte);

        this.cargarCorte();
    }

    void cargarCorte()
    {
        textview_servicio.setText(Constantes.servicioToString(corte.getServicio()));

        if(corte.getEmpresa() != null)
            textview_empresa.setText(corte.getEmpresa().getNombre());

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = df.format(corte.getFechaInicio());
        textview_fecha.setText(fecha);

        textview_cantidad_reportes.setText(Integer.toString(corte.cantidadReportes()));

        this.cargarListView();
    }

    void cargarListView(){

        //Crea el adaptador de alarmas
        RespuestaAdapter adapter = new RespuestaAdapter(corte.getRespuestas(), this, this);

        //enlaza el list view del layout a la variable
        ListView mi_lista = (ListView)findViewById(R.id.lst_respuesta_perfil_corte);

        //Le setea el adaptador a la lista
        mi_lista.setAdapter(adapter);
    }

    public void agregarRespuesta(View view)
    {
        String respuesta = edittext_respuesta.getText().toString();
        Respuesta nueva_respuesta = new Respuesta(Global.usuarios.get(0), respuesta);

        corte.addRespuesta(nueva_respuesta);
        this.cargarListView();

    }
}
