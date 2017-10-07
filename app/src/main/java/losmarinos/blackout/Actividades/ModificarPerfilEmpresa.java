package losmarinos.blackout.Actividades;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import losmarinos.blackout.ConsultorPOSTAPI;
import losmarinos.blackout.Global;
import losmarinos.blackout.Objetos.Empresa;
import losmarinos.blackout.ParserJSON;
import losmarinos.blackout.R;
import losmarinos.blackout.Validador;

import static losmarinos.blackout.Constantes.TAGAPI.ACTUALIZAR_EMPRESA;
import static losmarinos.blackout.Constantes.TAGAPI.REGISTRAR_REPORTE;

public class ModificarPerfilEmpresa extends AppCompatActivity {

    TextView textview_nombre;
    TextView textview_telefono;
    TextView textview_direccion;
    TextView textview_pagina;
    TextView textview_email;
    Button button_modificar;

    Empresa empresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_perfil_empresa);

        int id_empresa = getIntent().getIntExtra("idEmpresa", 0);
        this.empresa = Global.encontrarEmpresaPorId(id_empresa);

        textview_nombre = (TextView)findViewById(R.id.txt_nombre_modificar_empresa);
        textview_telefono = (TextView)findViewById(R.id.txt_telefono_modificar_empresa);
        textview_direccion = (TextView)findViewById(R.id.txt_direccion_modificar_empresa);
        textview_pagina = (TextView)findViewById(R.id.txt_pagina_modificar_empresa);
        textview_email = (TextView)findViewById(R.id.txt_email_modificar_empresa);
        button_modificar = (Button)findViewById(R.id.btn_modificar_modificar_perfil_empresa);

        this.cargarEmpresa();
    }

    public void cargarEmpresa(){
        textview_nombre.setText(this.empresa.getNombre());
        textview_telefono.setText(this.empresa.getTelefono());
        textview_direccion.setText(this.empresa.getDireccion());
        textview_pagina.setText(this.empresa.getPagina());
        textview_email.setText(this.empresa.getMail());
    }

    public void modificarEmpresa(View view){
        String nombre = textview_nombre.getText().toString();
        String telefono = textview_telefono.getText().toString();
        String direccion = textview_direccion.getText().toString();
        String pagina = textview_pagina.getText().toString();
        String email = textview_email.getText().toString();

        if(!Validador.validarCamposVacios(this, (LinearLayout)findViewById(R.id.lyt_modificar_perfil_empresa)))
            return;

        if(!Validador.validarMail(this, email))
            return;

        try{
            JSONObject nuevo_rep = ParserJSON.crearJSONEmpresa(nombre, email, telefono, pagina, direccion);

            String resultado = new ConsultorPOSTAPI("empresa/" + String.valueOf(this.empresa.getSubId()) + "/actualizar", Global.token_usuario_actual, nuevo_rep, ACTUALIZAR_EMPRESA, null).execute().get();
            StringBuilder mensaje_error = new StringBuilder();
            if(ParserJSON.esError(resultado, mensaje_error)){
                Toast.makeText(this, mensaje_error, Toast.LENGTH_LONG).show();
            }else{
                this.empresa.setNombre(nombre);
                this.empresa.setTelefono(telefono);
                this.empresa.setDireccion(direccion);
                this.empresa.setPagina(pagina);
                this.empresa.setMail(email);
            }
        }catch (Exception e){
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
        }

        this.finish();
    }
}
