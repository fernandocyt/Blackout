package losmarinos.blackout.Actividades;

import android.opengl.Visibility;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import losmarinos.blackout.Constantes;
import losmarinos.blackout.ConsultorPOSTAPI;
import losmarinos.blackout.Global;
import losmarinos.blackout.Objetos.Empresa;
import losmarinos.blackout.ParserJSON;
import losmarinos.blackout.R;
import losmarinos.blackout.Validador;

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
    Spinner spinner_servicios;
    Spinner spinner_empresas;

    View layout_empresa;
    View layout_usuario;
    View layout_seleccionar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Gestionar empresas");
        setContentView(R.layout.activity_crear_empresa);


        layout_empresa = findViewById(R.id.lyt_empresa_crear_empresa);
        layout_usuario = findViewById(R.id.lyt_usuario_crear_empresa);
        layout_seleccionar = findViewById(R.id.lyt_seleccionar_crear_empresa);

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
        spinner_servicios = (Spinner)findViewById(R.id.spn_servicios_crear_empresa);
        spinner_servicios.setOnItemSelectedListener(this);
        spinner_empresas = (Spinner)findViewById(R.id.spn_empresa_crear_empresa);
        spinner_empresas.setOnItemSelectedListener(this);

        this.cargarSpinnerOpciones();
        this.cargarSpinnerServicios();
        this.cargarSpinnerEmpresas();
    }

    @Override
    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
        switch (spinner_opciones.getSelectedItemPosition()){
            case 0:
                layout_empresa.setVisibility(View.VISIBLE);
                layout_usuario.setVisibility(View.GONE);
                layout_seleccionar.setVisibility(View.GONE);
                break;
            case 1:
                layout_empresa.setVisibility(View.VISIBLE);
                layout_usuario.setVisibility(View.VISIBLE);
                layout_seleccionar.setVisibility(View.GONE);
                break;
            case 2:
                layout_empresa.setVisibility(View.GONE);
                layout_usuario.setVisibility(View.VISIBLE);
                layout_seleccionar.setVisibility(View.VISIBLE);
                break;
            case 3:
                layout_empresa.setVisibility(View.GONE);
                layout_usuario.setVisibility(View.GONE);
                layout_seleccionar.setVisibility(View.VISIBLE);
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
        spinnerArray.add("Borrar empresa");


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinner_opciones.setAdapter(adapter);
    }

    private void cargarSpinnerServicios()
    {
        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add(Constantes.servicioToString(Constantes.SERVICIO.AGUA));
        spinnerArray.add(Constantes.servicioToString(Constantes.SERVICIO.LUZ));
        spinnerArray.add(Constantes.servicioToString(Constantes.SERVICIO.GAS));
        spinnerArray.add(Constantes.servicioToString(Constantes.SERVICIO.TELEFONO));
        spinnerArray.add(Constantes.servicioToString(Constantes.SERVICIO.INTERNET));
        spinnerArray.add(Constantes.servicioToString(Constantes.SERVICIO.CABLE));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinner_servicios.setAdapter(adapter);
    }

    private void cargarSpinnerEmpresas()
    {
        List<String> spinnerArray =  new ArrayList<String>();

        for(int i = 0; i < Global.empresas.size(); i++)
        {
            spinnerArray.add(Global.empresas.get(i).getNombre());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinner_empresas.setAdapter(adapter);
    }

    public void realizarOperacion(View view)
    {
        if(!this.validar())
            return;

        String nombre = edittext_nombre.getText().toString();
        String direccion = edittext_direccion.getText().toString();
        String website = edittext_website.getText().toString();
        String telefono = edittext_telefono.getText().toString();
        String email_contacto = edittext_email_contacto.getText().toString();
        String email = edittext_email.getText().toString();
        String pass1 = edittext_password.getText().toString();
        String pass2 = edittext_password2.getText().toString();

        String str_servicio = this.spinner_servicios.getSelectedItem().toString();
        int id_servicio =  Constantes.getIdServicio(Constantes.stringToServicio(str_servicio));

        try {
            // CREAR EMPRESA SIN USUARIO
            if(spinner_opciones.getSelectedItemPosition() == 0) {

                JSONObject nuevo_emp = ParserJSON.crearJSONEmpresa(nombre, email_contacto, telefono, website, direccion);
                nuevo_emp.put("servicio_id", id_servicio);

                String respuesta = new ConsultorPOSTAPI("empresa", Global.token_usuario_actual, nuevo_emp, Constantes.TAGAPI.REGISTRAR_EMPRESA, null).execute().get();

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

            // CREAR EMPRESA CON USUARIO
            } else if(spinner_opciones.getSelectedItemPosition() == 1) {

                JSONObject nuevo_emp_user = ParserJSON.crearJSONEmpresaUsuario(nombre, direccion, website, telefono, email_contacto, email, pass1, pass2);
                nuevo_emp_user.put("servicio_id", id_servicio);

                String respuesta = new ConsultorPOSTAPI("register-empresa", Global.token_usuario_actual, nuevo_emp_user, Constantes.TAGAPI.REGISTRAR_EMPRESA_USUARIO, null).execute().get();

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

            // CREAR USUARIO PARA EMPRESA
            }else if(spinner_opciones.getSelectedItemPosition() == 2) {
                String str_empresa = this.spinner_empresas.getSelectedItem().toString();
                Empresa empresa = Global.encontrarEmpresaPorNombre(str_empresa);

                JSONObject nuevo_user = ParserJSON.crearJSONUsuario(str_empresa, email, pass1, pass2);

                String respuesta = new ConsultorPOSTAPI("empresa/"+String.valueOf(empresa.getSubId())+"/asociar-usuario", Global.token_usuario_actual, nuevo_user, Constantes.TAGAPI.REGISTRAR_USUARIO_EMPRESA_EXISTENTE, null).execute().get();

                StringBuilder msj_error = new StringBuilder();
                if(ParserJSON.esError(respuesta, msj_error))
                {
                    Toast.makeText(this, msj_error, Toast.LENGTH_LONG).show();
                    return;
                }
                else
                {
                    Toast.makeText(this, "Usuario para empresa creado correctamente", Toast.LENGTH_LONG).show();
                    super.onBackPressed();
                }



                // BORRAR EMPRESA
            }else if(spinner_opciones.getSelectedItemPosition() == 3) {


            }



        }catch (Exception e){
            Toast.makeText(this, "Ha surgido un problema", Toast.LENGTH_LONG).show();
        }

    }

    public boolean validar(){
        String nombre = edittext_nombre.getText().toString();
        String direccion = edittext_direccion.getText().toString();
        String website = edittext_website.getText().toString();
        String telefono = edittext_telefono.getText().toString();
        String email_contacto = edittext_email_contacto.getText().toString();
        String email = edittext_email.getText().toString();
        String pass1 = edittext_password.getText().toString();
        String pass2 = edittext_password2.getText().toString();

        switch (spinner_opciones.getSelectedItemPosition()){
            case 0:
                if(!Validador.validarCamposVacios(this, (LinearLayout)findViewById(R.id.lyt_empresa_crear_empresa)))
                    return false;
                if(!Validador.validarMail(this, email_contacto))
                    return false;
                break;

            case 1:
                if(!Validador.validarCamposVacios(this, (LinearLayout)findViewById(R.id.lyt_empresa_crear_empresa)))
                    return false;
                if(!Validador.validarCamposVacios(this, (LinearLayout)findViewById(R.id.lyt_usuario_crear_empresa)))
                    return false;
                if(!Validador.validarMail(this, email_contacto))
                    return false;
                if(!Validador.validarMail(this, email))
                    return false;
                if(!Validador.validarPasswords(this, pass1, pass2))
                    return false;
                break;

            case 2:
                if(!Validador.validarCamposVacios(this, (LinearLayout)findViewById(R.id.lyt_usuario_crear_empresa)))
                    return false;
                if(!Validador.validarMail(this, email))
                    return false;
                if(!Validador.validarPasswords(this, pass1, pass2))
                    return false;
                break;
        }
        return true;
    }
}
