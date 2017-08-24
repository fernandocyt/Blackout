package losmarinos.blackout.Actividades;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import losmarinos.blackout.Constantes;
import losmarinos.blackout.ConsultorGETAPI;
import losmarinos.blackout.ConsultorPOSTAPI;
import losmarinos.blackout.Global;
import losmarinos.blackout.LocalDB;
import losmarinos.blackout.Objetos.Usuario;
import losmarinos.blackout.ObservadorAPI;
import losmarinos.blackout.R;

import static losmarinos.blackout.Constantes.TAGAPI.OBTENER_USUARIO_POR_TOKEN;

public class Login extends AppCompatActivity implements ObservadorAPI {

    EditText usuario;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        usuario = (EditText)findViewById(R.id.txt_usuario_login);
        password = (EditText)findViewById(R.id.txt_password_login);

        ArrayList<String> info_user = LocalDB.leerXML(getApplicationContext());

        if(info_user.size() > 0)
        {
            usuario.setText(info_user.get(2));
            password.setText(info_user.get(1));
        }

    }


    public void loguearUsuario(View view){
        String str_usuario = usuario.getText().toString();
        String str_password = password.getText().toString();

        if (str_password.isEmpty() || str_usuario.isEmpty()){
            Toast.makeText(this, "Debe completar todos los campos", Toast.LENGTH_LONG).show();
            return;
        }

        JSONObject obj = new JSONObject();
        try {
            obj.put("username", str_usuario);
            obj.put("password", str_password);
        }catch (Exception e){}

        new ConsultorPOSTAPI("login", obj, Constantes.TAGAPI.LOGUEAR_USUARIO, this).execute();
    }

    @Override
    public void obtenerRespuestaAPI(String respuesta, Constantes.TAGAPI tag, boolean correcto)
    {

        if (!correcto)
            return;

        switch (tag) {
            case LOGUEAR_USUARIO:
                //Toast.makeText(this, "Logeado correctamente", Toast.LENGTH_LONG).show();

                try {
                    JSONObject obj_resp = new JSONObject(respuesta);
                    Global.token_usuario_actual = obj_resp.getString("access_token");
                } catch (Exception e) {
                }

                new ConsultorGETAPI("user", Global.token_usuario_actual, Constantes.TAGAPI.OBTENER_USUARIO_POR_TOKEN, this).execute();

                break;
            case OBTENER_USUARIO_POR_TOKEN:

                int usu_id = 0;
                String usu_nombre = "";
                String usu_email = "";
                try {
                    JSONObject obj_resp = new JSONObject(respuesta);
                    usu_id = Integer.parseInt(obj_resp.getString("id"));
                    usu_nombre = obj_resp.getString("nombre");
                    usu_email = obj_resp.getString("email");
                } catch (Exception e) {
                }

                Global.usuario_actual = new Usuario(usu_nombre, password.getText().toString(), usu_email, Constantes.TIPOSUSUARIO.PERSONA);

                LocalDB.crearXML(this.getApplicationContext(),
                        Global.usuario_actual.getNombre(),
                        Global.usuario_actual.getPass(),
                        Global.usuario_actual.getMail(),
                        Global.token_usuario_actual);

                Intent i = new Intent(getApplicationContext(), MapaPrincipal.class);
                startActivity(i);
                break;
        }
    }

    public void registrarUsuario(View view)
    {
        Intent i = new Intent(getApplicationContext(), RegistrarUsuario.class);
        startActivity(i);
    }
}
