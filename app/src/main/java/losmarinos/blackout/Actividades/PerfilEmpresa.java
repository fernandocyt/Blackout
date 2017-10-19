package losmarinos.blackout.Actividades;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import losmarinos.blackout.Adapters.ComentarioAdapter;
import losmarinos.blackout.Aviso;
import losmarinos.blackout.Calculos;
import losmarinos.blackout.Constantes;
import losmarinos.blackout.ConsultorDELETEAPI;
import losmarinos.blackout.ConsultorPOSTAPI;
import losmarinos.blackout.Global;
import losmarinos.blackout.Objetos.Comentario;
import losmarinos.blackout.Objetos.Empresa;
import losmarinos.blackout.ParserJSON;
import losmarinos.blackout.R;

import static losmarinos.blackout.Constantes.TAGAPI.BORRAR_COMENTARIO;
import static losmarinos.blackout.Constantes.TAGAPI.REGISTRAR_COMENTARIO;
import static losmarinos.blackout.Global.token_usuario_actual;

public class PerfilEmpresa extends AppCompatActivity {

    TextView textview_empresaNombre;
    TextView textview_telefono;
    TextView textview_direccion;
    TextView textview_servicio;
    TextView textview_email;
    TextView textview_pagina;
    EditText edittext_comentario;
    RatingBar rtb_calificacion;
    Button button_agregar_comentario;
    Button button_borrar_comentario;
    Button button_modificar_perfil;

    Empresa empresa = null;

    ProgressDialog progress_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Perfil de empresa");
        setContentView(R.layout.activity_perfil_empresa);

        progress_dialog = Aviso.showProgressDialog(this, "Cargando empresa...");

        int id_empresa = getIntent().getIntExtra("idEmpresa", 0);
        this.empresa = Global.encontrarEmpresaPorId(id_empresa);

        textview_empresaNombre = (TextView)findViewById(R.id.lbl_empresaNombre_perfil_empresa);
        textview_telefono = (TextView)findViewById(R.id.lbl_telefono_perfil_empresa);
        textview_direccion = (TextView)findViewById(R.id.lbl_direccion_perfil_empresa);
        textview_servicio = (TextView)findViewById(R.id.lbl_servicio_perfil_empresa);
        textview_email = (TextView)findViewById(R.id.lbl_email_perfil_empresa);
        textview_pagina = (TextView)findViewById(R.id.lbl_pagina_perfil_empresa);
        edittext_comentario = (EditText)findViewById(R.id.txt_comentario_perfil_empresa);
        rtb_calificacion = (RatingBar)findViewById(R.id.rtb_calificacion_perfil_empresa);
        rtb_calificacion.setMax(5);
        rtb_calificacion.setStepSize(0.1f);
        button_agregar_comentario = (Button)findViewById(R.id.btn_agregar_comentario_perfil_empresa);
        button_borrar_comentario = (Button)findViewById(R.id.btn_borrar_comentario_perfil_empresa);
        button_modificar_perfil = (Button)findViewById(R.id.btn_modificar_perfil_empresa);


        new Thread(new Runnable() {
            @Override
            public void run() {
                empresa.actualizarComentarios(PerfilEmpresa.this);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cargarEmpresa();
                    }
                });
                Aviso.hideProgressDialog(PerfilEmpresa.this, progress_dialog);
            }
        }).start();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(empresa != null)
            this.cargarEmpresa();
    }

    void cargarEmpresa()
    {
        textview_empresaNombre.setText(empresa.getNombre());
        textview_telefono.setText(empresa.getTelefono());
        textview_direccion.setText(empresa.getDireccion());
        textview_servicio.setText(Constantes.servicioToString(empresa.getTipoServicio()));
        textview_email.setText(empresa.getMail());
        textview_pagina.setText(empresa.getPagina());
        rtb_calificacion.setRating((float) Calculos.calificacionEmpresa(empresa));

        if(Global.usuario_actual.getTipo() == Constantes.TIPOSUSUARIO.EMPRESA){
            button_agregar_comentario.setVisibility(View.GONE);
            edittext_comentario.setVisibility(View.GONE);
        }else{
            button_agregar_comentario.setVisibility(View.VISIBLE);
            edittext_comentario.setVisibility(View.VISIBLE);
        }

        if(Global.usuario_actual.getSubId() == empresa.getSubId()){
            button_modificar_perfil.setVisibility(View.VISIBLE);
        }else{
            button_modificar_perfil.setVisibility(View.GONE);
        }

        button_borrar_comentario.setVisibility(View.GONE);
        List<Comentario> comentarios = this.empresa.getComentarios();
        for(int i= 0; i < comentarios.size(); i++){
            if(comentarios.get(i).getUsuario().getIdUsuario() == Global.usuario_actual.getIdUsuario()){
                edittext_comentario.setText(comentarios.get(i).getTexto());
                button_borrar_comentario.setVisibility(View.VISIBLE);
            }
        }

        cargarListView();
    }

    public void cargarListView(){
        List<Comentario> comentarios_usuarios = new ArrayList<>(this.empresa.getComentarios());

        ComentarioAdapter adapter = new ComentarioAdapter(comentarios_usuarios, this, this);
        ListView mi_lista_comentarios = (ListView)findViewById(R.id.lst_comentario_perfil_empresa);
        mi_lista_comentarios.setAdapter(adapter);
        Global.setearLargoEnBaseAContenido(mi_lista_comentarios);
    }

    public void agregarComentario(View view)
    {
        progress_dialog = Aviso.showProgressDialog(this, "Agregando comentario...");

        new Thread(new Runnable() {
            @Override
            public void run() {
                String comentario = edittext_comentario.getText().toString();

                //Comentario nuevo_comentario = new Comentario(Global.usuarios.get(0), comentario);
                //empresa.addComentario(nuevo_comentario);

                try{
                    JSONObject nuevo_com = ParserJSON.crearJSONComentario(Global.usuario_actual.getIdUsuario(), empresa.getSubId(), comentario);

                    String resultado = new ConsultorPOSTAPI("comentario", Global.token_usuario_actual, nuevo_com, REGISTRAR_COMENTARIO, null).execute().get();
                    StringBuilder mensaje_error = new StringBuilder();
                    if(ParserJSON.esError(resultado, mensaje_error)){
                        Aviso.showToast(PerfilEmpresa.this, mensaje_error.toString());
                    }
                }catch (Exception e){
                    Aviso.showToast(PerfilEmpresa.this, "Error");
                }

                empresa.actualizarComentarios(PerfilEmpresa.this);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cargarEmpresa();
                    }
                });
                Aviso.hideProgressDialog(PerfilEmpresa.this, progress_dialog);
            }
        }).start();
    }

    public void verSucursales(View view)
    {
        Intent i = new Intent(this, VerSucursales.class);
        i.putExtra("idEmpresa", empresa.getSubId());
        startActivity(i);
    }

    public void modificarPerfil(View view)
    {
        Intent i = new Intent(this, ModificarPerfilEmpresa.class);
        i.putExtra("idEmpresa", empresa.getSubId());
        startActivity(i);
    }

    public void borrarComentario(View view)
    {
        progress_dialog = Aviso.showProgressDialog(this, "Borrando comentario...");

        new Thread(new Runnable() {
            @Override
            public void run() {

                List<Comentario> comentarios = empresa.getComentarios();
                int id_comentario = -1;
                for(int i = 0; i < comentarios.size(); i++){
                    if(comentarios.get(i).getUsuario().getIdUsuario() == Global.usuario_actual.getIdUsuario()){
                        id_comentario = comentarios.get(i).getId();
                    }
                }

                if(id_comentario == -1)
                    return;

                try{
                    String resultado = new ConsultorDELETEAPI("comentario/" + String.valueOf(id_comentario) + "/delete", token_usuario_actual, BORRAR_COMENTARIO, null).execute().get();
                    StringBuilder mensaje_error = new StringBuilder();
                    if(ParserJSON.esError(resultado, mensaje_error)){
                        Aviso.showToast(PerfilEmpresa.this, mensaje_error.toString());
                    }
                }catch (Exception e){
                    Aviso.showToast(PerfilEmpresa.this, "Error");
                }

                empresa.actualizarComentarios(PerfilEmpresa.this);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cargarEmpresa();
                    }
                });
                Aviso.hideProgressDialog(PerfilEmpresa.this, progress_dialog);
            }
        }).start();
    }

}
