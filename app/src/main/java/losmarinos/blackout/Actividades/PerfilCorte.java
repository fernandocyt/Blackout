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
import losmarinos.blackout.ConsultorDELETEAPI;
import losmarinos.blackout.ConsultorGETAPI;
import losmarinos.blackout.ConsultorPOSTAPI;
import losmarinos.blackout.Global;
import losmarinos.blackout.Objetos.Comentario;
import losmarinos.blackout.Objetos.Corte;
import losmarinos.blackout.Objetos.Respuesta;
import losmarinos.blackout.ParserJSON;
import losmarinos.blackout.R;
import losmarinos.blackout.Adapters.RespuestaAdapter;

import static losmarinos.blackout.Constantes.TAGAPI.BORRAR_CORTE_DE_INTERES;
import static losmarinos.blackout.Constantes.TAGAPI.BORRAR_PUNTO_DE_INTERES;
import static losmarinos.blackout.Constantes.TAGAPI.OBTENER_COMENTARIOS_POR_EMPRESA;
import static losmarinos.blackout.Constantes.TAGAPI.OBTENER_RESPUESTAS_POR_CORTE;
import static losmarinos.blackout.Constantes.TAGAPI.REGISTRAR_COMENTARIO;
import static losmarinos.blackout.Constantes.TAGAPI.REGISTRAR_CORTE_DE_INTERES;
import static losmarinos.blackout.Constantes.TAGAPI.REGISTRAR_RESPUESTA;
import static losmarinos.blackout.Global.token_usuario_actual;

public class PerfilCorte extends AppCompatActivity {

    TextView textview_servicio;
    TextView textview_empresa;
    TextView textview_motivo;
    TextView textview_fecha_inicio;
    TextView textview_fecha_fin;
    EditText edittext_respuesta;
    Button button_de_interes;
    Button button_agregar_respuesta;

    Corte corte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Perfil de corte");
        setContentView(R.layout.activity_perfil_corte);

        int id_corte = getIntent().getIntExtra("idCorte", 0);
        this.corte = Global.encontrarCortePorId(id_corte);

        textview_servicio = (TextView)findViewById(R.id.lbl_servicio_perfil_corte);
        textview_empresa = (TextView)findViewById(R.id.lbl_empresa_perfil_corte);
        textview_motivo = (TextView)findViewById(R.id.lbl_motivo_perfil_corte);
        textview_fecha_inicio = (TextView)findViewById(R.id.lbl_fecha_inicio_perfil_corte);
        textview_fecha_fin = (TextView)findViewById(R.id.lbl_fecha_fin_perfil_corte);
        edittext_respuesta = (EditText)findViewById(R.id.txt_respuesta_perfil_corte);
        button_de_interes = (Button)findViewById(R.id.btn_corte_interes_perfil_corte);
        button_agregar_respuesta = (Button)findViewById(R.id.btn_agregar_respuesta_perfil_corte);

        this.cargarCorte();
    }

    void cargarCorte()
    {
        textview_servicio.setText(Constantes.servicioToString(corte.getServicio()));

        if(corte.getEmpresa() != null)
            textview_empresa.setText(corte.getEmpresa().getNombre());

        if(corte.isProgramado()){
            textview_motivo.setText("Programado por empresa");
        }else{
            textview_motivo.setText("Generado por " + Integer.toString(corte.cantidadReportes()) + " reportes");
        }

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        textview_fecha_inicio.setText(df.format(corte.getFechaInicio()));
        if(corte.getFechaFin() != null) {
            textview_fecha_fin.setText(df.format(corte.getFechaFin()));
        }

        if(Global.usuario_actual.esCorteDeInteres(this.corte.getId())){
            button_de_interes.setText("Desmarcar de interes");
        }else{
            button_de_interes.setText("Marcar de interes");
        }

        if(Global.usuario_actual.getTipo() == Constantes.TIPOSUSUARIO.EMPRESA &&
                Global.usuario_actual.getSubId() != this.corte.getIdEmpresa()){
            button_agregar_respuesta.setVisibility(View.GONE);
            edittext_respuesta.setVisibility(View.GONE);
        }else{
            button_agregar_respuesta.setVisibility(View.VISIBLE);
            edittext_respuesta.setVisibility(View.VISIBLE);
        }

        if(Global.usuario_actual.getTipo() == Constantes.TIPOSUSUARIO.EMPRESA &&
                Global.usuario_actual.getSubId() == this.corte.getIdEmpresa()){
            edittext_respuesta.setHint("Informar a usuarios");
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

        for(int i = 0; i < respuestas.size(); i++) {
            if(respuestas.get(i).getUsuario().getTipo() == Constantes.TIPOSUSUARIO.EMPRESA)
            {

                List<Respuesta> resp_empresa = new ArrayList<>();
                resp_empresa.add(respuestas.remove(i));
                RespuestaAdapter adapter = new RespuestaAdapter(resp_empresa, true, this, this);
                ListView mi_lista = (ListView)findViewById(R.id.lst_respuesta_empresa_perfil_corte);
                mi_lista.setAdapter(adapter);
                break;
            }
        }

        RespuestaAdapter adapter = new RespuestaAdapter(respuestas, false, this, this);
        ListView mi_lista = (ListView)findViewById(R.id.lst_respuesta_perfil_corte);
        mi_lista.setAdapter(adapter);

    }

    public void agregarRespuesta(View view)
    {
        String respuesta = edittext_respuesta.getText().toString();

        try{
            JSONObject nuevo_com = ParserJSON.crearJSONRespuesta(Global.usuario_actual.getIdUsuario(), this.corte.getId(), respuesta);

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
        if(!Global.usuario_actual.esCorteDeInteres(this.corte.getId())) {
            // LO MARCO COMO CORTE DE INTERES
            try {
                JSONObject nuevo_corte_int = ParserJSON.crearJSONCorteDeInteres(Global.usuario_actual.getIdUsuario(), this.corte.getId());

                String resultado = new ConsultorPOSTAPI("cortes-de-interes", Global.token_usuario_actual, nuevo_corte_int, REGISTRAR_CORTE_DE_INTERES, null).execute().get();
                StringBuilder mensaje_error = new StringBuilder();
                if (ParserJSON.esError(resultado, mensaje_error)) {
                    Toast.makeText(this, mensaje_error, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Corte de interes agregado", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
            }
        }else{
            // LO BORRO COMO CORTE DE INTERES
            try{
                int id_corte_interes = Global.usuario_actual.getIdCorteInteres(this.corte.getId());
                if(id_corte_interes == -1){
                    Toast.makeText(this, "No se pudo encontrar corte de interes", Toast.LENGTH_LONG).show();
                    return;
                }

                String resultado = new ConsultorDELETEAPI("cortes-de-interes/" + id_corte_interes, token_usuario_actual, BORRAR_CORTE_DE_INTERES, null).execute().get();
                StringBuilder mensaje_error = new StringBuilder();
                if(ParserJSON.esError(resultado, mensaje_error)){
                    Toast.makeText(this, mensaje_error, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Corte de interes borrado", Toast.LENGTH_LONG).show();
                }
            }catch (Exception e){
                Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
            }
        }
        Global.usuario_actual.actualizarCortesInteres(this);
        this.cargarCorte();
    }
}
