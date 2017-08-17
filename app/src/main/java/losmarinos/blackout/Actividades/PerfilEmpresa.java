package losmarinos.blackout.Actividades;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import losmarinos.blackout.Adapters.ComentarioAdapter;
import losmarinos.blackout.Constantes;
import losmarinos.blackout.Global;
import losmarinos.blackout.Objetos.Comentario;
import losmarinos.blackout.Objetos.Empresa;
import losmarinos.blackout.R;

public class PerfilEmpresa extends AppCompatActivity {

    TextView textview_empresaNombre;
    TextView textview_telefono;
    TextView textview_direccion;
    TextView textview_servicio;
    TextView textview_calificacion;
    TextView textview_pagina;
    EditText edittext_comentario;

    Empresa empresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Perfil de empresa");
        setContentView(R.layout.activity_perfil_empresa);

        int id_empresa = getIntent().getIntExtra("idEmpresa", 0);
        for(int i = 0; i < Global.empresas.size(); i++)
        {
            if(id_empresa == Global.empresas.get(i).getId())
            {
                this.empresa = Global.empresas.get(i);
            }
        }

        textview_empresaNombre = (TextView)findViewById(R.id.lbl_empresaNombre_perfil_empresa);
        textview_telefono = (TextView)findViewById(R.id.lbl_telefono_perfil_empresa);
        textview_direccion = (TextView)findViewById(R.id.lbl_direccion_perfil_empresa);
        textview_servicio = (TextView)findViewById(R.id.lbl_servicio_perfil_empresa);
        textview_calificacion = (TextView)findViewById(R.id.lbl_calificacion_perfil_empresa);
        textview_pagina = (TextView)findViewById(R.id.lbl_pagina_perfil_empresa);
        edittext_comentario = (EditText)findViewById(R.id.txt_comentario_perfil_empresa);

        this.cargarEmpresa();

    }

    void cargarEmpresa()
    {
        textview_empresaNombre.setText(empresa.getNombre());
        textview_telefono.setText(empresa.getTelefono());
        textview_direccion.setText(empresa.getDireccion());
        textview_servicio.setText(Constantes.servicioToString(empresa.getTipoServicio()));
        textview_calificacion.setText(Double.toString(empresa.getCalificacion()));
        textview_pagina.setText(empresa.getPagina());


        this.cargarListView();
    }

    void cargarListView(){

        //Crea el adaptador de alarmas
        ComentarioAdapter adapter = new ComentarioAdapter(empresa.getComentarios(), this, this);

        //enlaza el list view del layout a la variable
        ListView mi_lista = (ListView)findViewById(R.id.lst_comentario_perfil_empresa);

        //Le setea el adaptador a la lista
        mi_lista.setAdapter(adapter);
    }

    public void agregarComentario(View view)
    {
        String comentario = edittext_comentario.getText().toString();
        Comentario nuevo_comentario = new Comentario(Global.usuarios.get(0), comentario);

        empresa.addComentario(nuevo_comentario);
        this.cargarListView();

    }

}
