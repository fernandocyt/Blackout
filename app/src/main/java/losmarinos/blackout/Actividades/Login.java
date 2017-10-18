package losmarinos.blackout.Actividades;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import losmarinos.blackout.Constantes;
import losmarinos.blackout.ConsultorGETAPI;
import losmarinos.blackout.ConsultorPOSTAPI;
import losmarinos.blackout.Global;
import losmarinos.blackout.LocalDB;
import losmarinos.blackout.Objetos.Empresa;
import losmarinos.blackout.Objetos.Usuario;
import losmarinos.blackout.ObservadorAPI;
import losmarinos.blackout.ParserJSON;
import losmarinos.blackout.R;
import losmarinos.blackout.Validador;

import static losmarinos.blackout.Constantes.TAGAPI.OBTENER_EMPRESA_POR_ID;
import static losmarinos.blackout.Constantes.TAGAPI.OBTENER_USUARIO_POR_TOKEN;
import static losmarinos.blackout.Constantes.TAGAPI.LOGUEAR_USUARIO;

public class Login extends AppCompatActivity implements Runnable {

    EditText usuario;
    EditText password;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        dialog = new ProgressDialog(this);

        usuario = (EditText)findViewById(R.id.txt_usuario_login);
        password = (EditText)findViewById(R.id.txt_password_login);

        StringBuilder nom = new StringBuilder();
        StringBuilder pass = new StringBuilder();
        StringBuilder mail = new StringBuilder();
        StringBuilder token = new StringBuilder();
        int[] id = new int[1];
        boolean correcto = LocalDB.leerArchivoJSONUsuario(getApplicationContext(), id, nom, pass, mail, token);

        if(correcto)
        {
            usuario.setText(mail);
            password.setText(pass);
            this.loguearUsuario(findViewById(R.id.btn_ingresar_login));
        }
    }


    public void loguearUsuario(View view){
        dialog.setIndeterminate(true);
        dialog.setMessage("Logeando usuario...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        new Thread(this).start();
    }

    public void run(){

        if(!Validador.validarCamposVacios(this, (LinearLayout)findViewById(R.id.lyt_login))){
            if(dialog.isShowing()){
                dialog.dismiss();
            }
            return;
        }

        String str_usuario = usuario.getText().toString();
        String str_password = password.getText().toString();

        try {
            // Logeo usuario y obtengo token
            JSONObject obj = new JSONObject();
            obj.put("username", str_usuario);
            obj.put("password", str_password);

            String respuesta = new ConsultorPOSTAPI("login", null, obj, LOGUEAR_USUARIO, null).execute().get();
            StringBuilder msg_error = new StringBuilder();
            if(ParserJSON.esError(respuesta, msg_error)){
                this.showToast(msg_error.toString());
            }else{
                String access_token = ParserJSON.obtenerAccessToken(respuesta);
                if(access_token == null){
                    this.showToast("Error: usuario inexistente");
                }
                Global.token_usuario_actual = access_token;

                // Obtengo datos de usuario
                respuesta = new ConsultorGETAPI("user", Global.token_usuario_actual, OBTENER_USUARIO_POR_TOKEN, null).execute().get();

                if(ParserJSON.esError(respuesta, msg_error)){
                    this.showToast(msg_error.toString());
                    return;
                } else {
                    Usuario nuevo_usuario = ParserJSON.obtenerUsuario(respuesta);

                    nuevo_usuario.setPass(str_password);

                    if (nuevo_usuario == null) {
                        this.showToast("Error: usuario inexistente");
                        return;
                    }


                    // Si es una empresa me fijo que este habilitada
                    if(nuevo_usuario.getTipo() == Constantes.TIPOSUSUARIO.EMPRESA){
                        String resp_empresa = new ConsultorGETAPI("empresa/" + nuevo_usuario.getSubId(), Global.token_usuario_actual, OBTENER_EMPRESA_POR_ID, null).execute().get();

                        if(ParserJSON.esError(resp_empresa, msg_error)) {
                            this.showToast(msg_error.toString());
                            return;
                        }else{
                            JSONObject emp = new JSONObject(resp_empresa);
                            JSONObject json_emp = emp.getJSONObject("empresa");

                            int habilitada = json_emp.getInt("habilitada");
                            if(habilitada == 0){
                                this.showToast("La empresa del usuario no se encuentra habilitada");
                                return;
                            }
                        }
                    }


                    Global.usuario_actual = nuevo_usuario;

                    LocalDB.crearArchivoJSONUsuario(this.getApplicationContext(),
                            Global.usuario_actual.getIdUsuario(),
                            Global.usuario_actual.getNombre(),
                            password.getText().toString(),
                            Global.usuario_actual.getMail(),
                            Global.token_usuario_actual);

                    if(!LocalDB.leerArchivoJSONPreferencias(this, null, null, null, null)) {
                        LocalDB.crearArchivoJSONPreferencias(this, true, true, true, true);
                    }

                    //Intent i = new Intent(getApplicationContext(), MapaPrincipal.class);
                    //startActivity(i);
                    //finish();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            Intent i = new Intent(getApplicationContext(), MapaPrincipal.class);
                            startActivity(i);
                            finish();
                        }
                    });
                }
            }

        }
        catch (Exception e)
        {
            this.showToast("Se ah producido un error logeando");
            return;
        }


    }

    public void showToast(final String toast)
    {
        if(dialog.isShowing()){
            dialog.dismiss();
        }
        runOnUiThread(new Runnable() {
            public void run()
            {
                Toast.makeText(getApplicationContext(), toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void registrarUsuario(View view)
    {
        Intent i = new Intent(getApplicationContext(), RegistrarUsuario.class);
        startActivity(i);
    }
}
