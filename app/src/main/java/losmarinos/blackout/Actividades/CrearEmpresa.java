package losmarinos.blackout.Actividades;

import android.opengl.Visibility;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import losmarinos.blackout.Constantes;
import losmarinos.blackout.ConsultorPOSTAPI;
import losmarinos.blackout.Global;
import losmarinos.blackout.ParserJSON;
import losmarinos.blackout.R;

public class CrearEmpresa extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    EditText edittext_nombre;
    EditText edittext_direccion;
    EditText edittext_website;
    EditText edittext_telefono;
    EditText edittext_email_contacto;
    EditText edittext_email;
    EditText edittext_password;
    EditText edittext_password2;
    Spinner spinner_opciones;

    TextView textview_email;
    TextView textview_password;
    TextView textview_password2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Crear empresa");
        setContentView(R.layout.activity_crear_empresa);

        edittext_nombre = (EditText)findViewById(R.id.txt_nombre_crear_empresa);
        edittext_direccion = (EditText)findViewById(R.id.txt_direccion_crear_empresa);
        edittext_website = (EditText)findViewById(R.id.txt_pagina_crear_empresa);
        edittext_telefono = (EditText)findViewById(R.id.txt_telefono_crear_empresa);
        edittext_email_contacto = (EditText)findViewById(R.id.txt_email_contacto_crear_empresa);
        edittext_email = (EditText)findViewById(R.id.txt_email_crear_empresa);
        edittext_password = (EditText)findViewById(R.id.txt_password_crear_empresa);
        edittext_password2 = (EditText)findViewById(R.id.txt_password2_crear_empresa);

        spinner_opciones = (Spinner) findViewById(R.id.spn_opciones_crear_empresa);
        spinner_opciones.setOnItemSelectedListener(this);

        textview_email = (TextView) findViewById(R.id.lbl_email_crear_empresa);
        textview_password = (TextView)findViewById(R.id.lbl_password_crear_empresa);
        textview_password2 = (TextView)findViewById(R.id.lbl_password2_crear_empresa);

        this.cargarSpinnerOpciones();
    }

    @Override
    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
        switch (spinner_opciones.getSelectedItemPosition()){
            case 0:
                edittext_email.setVisibility(View.GONE);
                edittext_password.setVisibility(View.GONE);
                edittext_password2.setVisibility(View.GONE);
                textview_email.setVisibility(View.GONE);
                textview_password.setVisibility(View.GONE);
                textview_password2.setVisibility(View.GONE);
                break;
            case 1:
                edittext_email.setVisibility(View.VISIBLE);
                edittext_password.setVisibility(View.VISIBLE);
                edittext_password2.setVisibility(View.VISIBLE);
                textview_email.setVisibility(View.VISIBLE);
                textview_password.setVisibility(View.VISIBLE);
                textview_password2.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parentView) {
    }

    private void cargarSpinnerOpciones()
    {
        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("Crear empresa");
        spinnerArray.add("Crear usuario y empresa");
        spinnerArray.add("Asignar usuario a empresa");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinner_opciones.setAdapter(adapter);
    }

    public void registrarUsuario(View view)
    {
        String nombre = edittext_nombre.getText().toString();
        String direccion = edittext_direccion.getText().toString();
        String website = edittext_website.getText().toString();
        String telefono = edittext_telefono.getText().toString();
        String email_contacto = edittext_email_contacto.getText().toString();
        String email = edittext_email.getText().toString();
        String pass1 = edittext_password.getText().toString();
        String pass2 = edittext_password2.getText().toString();

        if(direccion.isEmpty() || website.isEmpty() || telefono.isEmpty() || email_contacto.isEmpty())
        {
            Toast.makeText(this, "Debe completar todos los campos", Toast.LENGTH_LONG).show();
            return;
        }


        if(spinner_opciones.getSelectedItemPosition() == 1){
            if(nombre.isEmpty() || email.isEmpty() || pass1.isEmpty() || pass2.isEmpty())
            {
                Toast.makeText(this, "Debe completar todos los campos", Toast.LENGTH_LONG).show();
                return;
            }

            if(!pass1.equals(pass2))
            {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show();
                return;
            }
        }

        try {
            String respuesta = "";
            if(spinner_opciones.getSelectedItemPosition() == 0) {
                JSONObject nuevo_emp = ParserJSON.crearJSONEmpresa(nombre, email_contacto, telefono, website, direccion);

                respuesta = new ConsultorPOSTAPI("empresa", Global.token_usuario_actual, nuevo_emp, Constantes.TAGAPI.REGISTRAR_EMPRESA, null).execute().get();

            } else if(spinner_opciones.getSelectedItemPosition() == 1) {
                JSONObject nuevo_emp_user = ParserJSON.crearJSONEmpresaUsuario(nombre, direccion, website, telefono, email_contacto, email, pass1, pass2);

                respuesta = new ConsultorPOSTAPI("register-empresa", Global.token_usuario_actual, nuevo_emp_user, Constantes.TAGAPI.REGISTRAR_EMPRESA_USUARIO, null).execute().get();
            }

            StringBuilder msj_error = new StringBuilder();
            if(ParserJSON.esError(respuesta, msj_error))
            {
                Toast.makeText(this, msj_error, Toast.LENGTH_LONG).show();
                return;
            }
            else
            {
                Toast.makeText(this, "Empresa creada correctamente", Toast.LENGTH_LONG).show();
                super.onBackPressed();
            }

        }catch (Exception e){
            Toast.makeText(this, "Ha surgido un problema", Toast.LENGTH_LONG).show();
        }

    }
}
