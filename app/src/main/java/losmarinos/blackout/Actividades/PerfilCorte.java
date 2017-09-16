package losmarinos.blackout.Actividades;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import losmarinos.blackout.Constantes;
import losmarinos.blackout.ConsultorGETAPI;
import losmarinos.blackout.ConsultorPOSTAPI;
import losmarinos.blackout.Global;
import losmarinos.blackout.Objetos.Comentario;
import losmarinos.blackout.Objetos.Corte;
import losmarinos.blackout.Objetos.Respuesta;
import losmarinos.blackout.ParserJSON;
import losmarinos.blackout.R;
import losmarinos.blackout.Adapters.RespuestaAdapter;

import static losmarinos.blackout.Constantes.TAGAPI.OBTENER_COMENTARIOS_POR_EMPRESA;
import static losmarinos.blackout.Constantes.TAGAPI.OBTENER_RESPUESTAS_POR_CORTE;
import static losmarinos.blackout.Constantes.TAGAPI.REGISTRAR_COMENTARIO;
import static losmarinos.blackout.Constantes.TAGAPI.REGISTRAR_CORTE_DE_INTERES;
import static losmarinos.blackout.Constantes.TAGAPI.REGISTRAR_RESPUESTA;

public class PerfilCorte extends AppCompatActivity {

    TextView textview_servicio;
    TextView textview_empresa;
    TextView textview_fecha;
    TextView textview_cantidad_reportes;
    EditText edittext_respuesta;
    Button button_hacer_de_interes;

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
        button_hacer_de_interes = (Button)findViewById(R.id.btn_corte_interes_perfil_corte);

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

        if(Global.usuario_actual.esCorteDeInteres(this.corte.getId())){
            button_hacer_de_interes.setVisibility(View.GONE);
        }

        this.cargarListView();
    }

    void cargarListView(){
        List<Respuesta> respuestas = new ArrayList<>();
        try {
            String respuesta = new ConsultorGETAPI("cortes/"+String.valueOf(this.corte.getId())+"/respuestas",
                    Global.token_usuario_actual, OBTENER_RESPUESTAS_POR_CORTE, null).execute().get();
            StringBuilder msg_error = new StringBuilder();
            if(ParserJSON.esError(respuesta, msg_error)){
                Toast.makeText(this, "No es posible cargar respuestas", Toast.LENGTH_LONG).show();
                return;
            }else{
                respuestas = ParserJSON.obtenerRespuestas(respuesta);
            }
        }catch (Exception e){}

        RespuestaAdapter adapter = new RespuestaAdapter(respuestas, this, this);
        ListView mi_lista = (ListView)findViewById(R.id.lst_respuesta_perfil_corte);
        mi_lista.setAdapter(adapter);

    }

    public void agregarRespuesta(View view)
    {
        String respuesta = edittext_respuesta.getText().toString();

        try{
            JSONObject nuevo_com = ParserJSON.crearJSONRespuesta(Global.usuario_actual.getId(), this.corte.getId(), respuesta);

            String resultado = new ConsultorPOSTAPI("respuestas", Global.token_usuario_actual, nuevo_com, REGISTRAR_RESPUESTA, null).execute().get();
            StringBuilder mensaje_error = new StringBuilder();
            if(ParserJSON.esError(resultado, mensaje_error)){
                Toast.makeText(this, mensaje_error, Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
        }


        this.cargarListView();
    }

    public void irAPerfilEmpresa(View view){
        if(corte.getEmpresa() != null) {
            Intent i = new Intent(getApplicationContext(), PerfilEmpresa.class);
            i.putExtra("idEmpresa", corte.getIdEmpresa());
            try {
                startActivity(i);
            } catch (Exception e) {
                throw e;
            }
        }
    }

    public void marcarCorteDeInteres(View view){
        try{
            JSONObject nuevo_corte_int = ParserJSON.crearJSONCorteDeInteres(Global.usuario_actual.getId(), this.corte.getId());

            String resultado = new ConsultorPOSTAPI("cortes-de-interes", Global.token_usuario_actual, nuevo_corte_int, REGISTRAR_CORTE_DE_INTERES, null).execute().get();
            StringBuilder mensaje_error = new StringBuilder();
            if(ParserJSON.esError(resultado, mensaje_error)){
                Toast.makeText(this, mensaje_error, Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this, "Corte de interes agregado", Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
        }
    }
}
