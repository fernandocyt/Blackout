package losmarinos.blackout.Actividades;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import losmarinos.blackout.Aviso;
import losmarinos.blackout.Constantes;
import losmarinos.blackout.ConsultorDELETEAPI;
import losmarinos.blackout.ConsultorPOSTAPI;
import losmarinos.blackout.Global;
import losmarinos.blackout.Objetos.Corte;
import losmarinos.blackout.Objetos.Respuesta;
import losmarinos.blackout.ParserJSON;
import losmarinos.blackout.R;
import losmarinos.blackout.Adapters.RespuestaAdapter;

import static losmarinos.blackout.Constantes.TAGAPI.BORRAR_CORTE_DE_INTERES;
import static losmarinos.blackout.Constantes.TAGAPI.BORRAR_RESPUESTA;
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
    Button button_borrar_respuesta;
    ImageView imageview_servicio;

    Corte corte;

    ProgressDialog progress_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Perfil de corte");
        setContentView(R.layout.activity_perfil_corte);

        progress_dialog = Aviso.showProgressDialog(this, "Cargando corte...");

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
        button_borrar_respuesta = (Button)findViewById(R.id.btn_borrar_respuesta_perfil_corte);
        imageview_servicio = (ImageView)findViewById(R.id.img_perfil_corte);

        new Thread(new Runnable() {
            @Override
            public void run() {
                corte.actualizarRespuestas(PerfilCorte.this);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cargarCorte();
                    }
                });
                Aviso.hideProgressDialog(PerfilCorte.this, progress_dialog);
            }
        }).start();
    }

    void cargarCorte()
    {
        switch(corte.getServicio())
        {
            case AGUA:
                imageview_servicio.setImageResource(R.drawable.icono_corte_agua);
                this.getWindow().getDecorView().setBackgroundColor(Color.parseColor("#CEE3F6"));
                break;
            case LUZ:
                imageview_servicio.setImageResource(R.drawable.icono_corte_luz);
                this.getWindow().getDecorView().setBackgroundColor(Color.parseColor("#F5F6CE"));
                break;
            case GAS:
                imageview_servicio.setImageResource(R.drawable.icono_corte_gas);
                this.getWindow().getDecorView().setBackgroundColor(Color.parseColor("#F8E0E0"));
                break;
            case CABLE:
                imageview_servicio.setImageResource(R.drawable.icono_corte_cable);
                this.getWindow().getDecorView().setBackgroundColor(Color.parseColor("#F5ECCE"));
                break;
            case TELEFONO:
                imageview_servicio.setImageResource(R.drawable.icono_corte_telefono);
                this.getWindow().getDecorView().setBackgroundColor(Color.parseColor("#CEF6CE"));
                break;
            case INTERNET:
                imageview_servicio.setImageResource(R.drawable.icono_corte_internet);
                this.getWindow().getDecorView().setBackgroundColor(Color.parseColor("#ECCEF5"));
                break;

        }

        textview_servicio.setText(Constantes.servicioToString(corte.getServicio()));

        if(corte.getEmpresa() != null)
            textview_empresa.setText(corte.getEmpresa().getNombre());

        if(corte.isProgramado()){
            textview_motivo.setText("Programado por empresa");
        }else{
            textview_motivo.setText("Generado por " + Integer.toString(corte.getCantidadReportes()) + " reportes");
        }

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
        textview_fecha_inicio.setText(df.format(corte.getFechaInicio()));
        if(corte.getFechaFin() != null) {
            textview_fecha_fin.setText(df.format(corte.getFechaFin()));
        }

        if(Global.usuario_actual.esCorteDeInteres(this.corte.getId())){
            button_de_interes.setText("Desmarcar de interés");
        }else{
            button_de_interes.setText("Marcar de interés");
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

        button_borrar_respuesta.setVisibility(View.GONE);
        List<Respuesta> respuestas = this.corte.getRespuestas();
        for(int i= 0; i < respuestas.size(); i++){
            if(respuestas.get(i).getUsuario().getIdUsuario() == Global.usuario_actual.getIdUsuario()){
                edittext_respuesta.setText(respuestas.get(i).getTexto());
                button_borrar_respuesta.setVisibility(View.VISIBLE);
            }
        }

        this.cargarListView();
    }

    public void cargarListView(){
        List<Respuesta> respuestas_usuarios = new ArrayList<>(this.corte.getRespuestas());
        List<Respuesta> respuesta_empresa = new ArrayList<>();

        for(int i = 0; i < respuestas_usuarios.size(); i++) {
            if(respuestas_usuarios.get(i).getUsuario().getTipo() == Constantes.TIPOSUSUARIO.EMPRESA)
            {
                respuesta_empresa.add(respuestas_usuarios.remove(i));
                break;
            }
        }

        RespuestaAdapter adapter_empresa = new RespuestaAdapter(respuesta_empresa, true, this, this);
        ListView mi_lista_empresa = (ListView)findViewById(R.id.lst_respuesta_empresa_perfil_corte);
        mi_lista_empresa.setAdapter(adapter_empresa);
        Global.setearLargoEnBaseAContenido(mi_lista_empresa);

        RespuestaAdapter adapter_usuarios = new RespuestaAdapter(respuestas_usuarios, false, this, this);
        ListView mi_lista_usuarios = (ListView)findViewById(R.id.lst_respuesta_perfil_corte);

        if(respuestas_usuarios.size() == 0){
            mi_lista_usuarios.setVisibility(View.GONE);
        }else{
            mi_lista_usuarios.setVisibility(View.VISIBLE);
        }
        mi_lista_usuarios.setAdapter(adapter_usuarios);
        Global.setearLargoEnBaseAContenido(mi_lista_usuarios);
    }

    public void agregarRespuesta(View view)
    {
        progress_dialog = Aviso.showProgressDialog(this, "Agregando respuesta...");

        new Thread(new Runnable() {
            @Override
            public void run() {

                String respuesta = edittext_respuesta.getText().toString();

                try{
                    JSONObject nuevo_com = ParserJSON.crearJSONRespuesta(Global.usuario_actual.getIdUsuario(), corte.getId(), respuesta);

                    String resultado = new ConsultorPOSTAPI("respuestas", Global.token_usuario_actual, nuevo_com, REGISTRAR_RESPUESTA, null).execute().get();
                    StringBuilder mensaje_error = new StringBuilder();
                    if(ParserJSON.esError(resultado, mensaje_error)){
                        Aviso.showToast(PerfilCorte.this, mensaje_error.toString());
                    }
                }catch (Exception e){
                    Aviso.showToast(PerfilCorte.this, "Error");
                }

                corte.actualizarRespuestas(PerfilCorte.this);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cargarCorte();
                    }
                });
                Aviso.hideProgressDialog(PerfilCorte.this, progress_dialog);
            }
        }).start();

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

        progress_dialog = Aviso.showProgressDialog(this, "Actualizando corte de interés...");

        new Thread(new Runnable() {
            @Override
            public void run() {

                if(!Global.usuario_actual.esCorteDeInteres(corte.getId())) {
                    // LO MARCO COMO CORTE DE INTERES
                    try {
                        JSONObject nuevo_corte_int = ParserJSON.crearJSONCorteDeInteres(Global.usuario_actual.getIdUsuario(), corte.getId());

                        String resultado = new ConsultorPOSTAPI("cortes-de-interes", Global.token_usuario_actual, nuevo_corte_int, REGISTRAR_CORTE_DE_INTERES, null).execute().get();
                        StringBuilder mensaje_error = new StringBuilder();
                        if (ParserJSON.esError(resultado, mensaje_error)) {
                            Aviso.showToast(PerfilCorte.this, mensaje_error.toString());
                        } else {
                            Aviso.showToast(PerfilCorte.this, "Corte de interés agregado");
                        }
                    } catch (Exception e) {
                        Aviso.showToast(PerfilCorte.this, "Error");
                    }
                }else{
                    // LO BORRO COMO CORTE DE INTERES
                    try{
                        int id_corte_interes = Global.usuario_actual.getIdCorteInteres(corte.getId());
                        if(id_corte_interes == -1){
                            Aviso.showToast(PerfilCorte.this, "No se pudo encontrar corte de interés");
                            return;
                        }

                        String resultado = new ConsultorDELETEAPI("cortes-de-interes/" + id_corte_interes, token_usuario_actual, BORRAR_CORTE_DE_INTERES, null).execute().get();
                        StringBuilder mensaje_error = new StringBuilder();
                        if(ParserJSON.esError(resultado, mensaje_error)){
                            Aviso.showToast(PerfilCorte.this, mensaje_error.toString());
                        } else {
                            Aviso.showToast(PerfilCorte.this, "Corte de interés borrado");
                        }
                    }catch (Exception e){
                        Aviso.showToast(PerfilCorte.this, "Error");
                    }
                }
                Global.usuario_actual.actualizarCortesInteres(PerfilCorte.this);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cargarCorte();
                    }
                });
                Aviso.hideProgressDialog(PerfilCorte.this, progress_dialog);
            }
        }).start();
    }

    public void borrarRespuesta(View view)
    {

        progress_dialog = Aviso.showProgressDialog(this, "Borrando respuesta...");

        new Thread(new Runnable() {
            @Override
            public void run() {

                List<Respuesta> respuestas = corte.getRespuestas();
                int id_respuesta = -1;
                for(int i = 0; i < respuestas.size(); i++){
                    if(respuestas.get(i).getUsuario().getIdUsuario() == Global.usuario_actual.getIdUsuario()){
                        id_respuesta = respuestas.get(i).getId();
                    }
                }

                if(id_respuesta == -1)
                    return;

                try{
                    String resultado = new ConsultorDELETEAPI("respuestas/" + String.valueOf(id_respuesta) + "/delete", token_usuario_actual, BORRAR_RESPUESTA, null).execute().get();
                    StringBuilder mensaje_error = new StringBuilder();
                    if(ParserJSON.esError(resultado, mensaje_error)){
                        Aviso.showToast(PerfilCorte.this, mensaje_error.toString());
                    }
                }catch (Exception e){
                    Aviso.showToast(PerfilCorte.this, "Error");
                }

                corte.actualizarRespuestas(PerfilCorte.this);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cargarCorte();
                    }
                });
                Aviso.hideProgressDialog(PerfilCorte.this, progress_dialog);
            }
        }).start();
    }

}
