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

import losmarinos.blackout.API.APIObtenerUsuarioPorToken;
import losmarinos.blackout.Constantes;
import losmarinos.blackout.ConsultorGETAPI;
import losmarinos.blackout.ConsultorPOSTAPI;
import losmarinos.blackout.Global;
import losmarinos.blackout.LocalDB;
import losmarinos.blackout.Objetos.Usuario;
import losmarinos.blackout.ObservadorAPI;
import losmarinos.blackout.ParserJSON;
import losmarinos.blackout.R;

import static losmarinos.blackout.Constantes.TAGAPI.OBTENER_USUARIO_POR_TOKEN;
import static losmarinos.blackout.Constantes.TAGAPI.LOGUEAR_USUARIO;

public class Login extends AppCompatActivity {

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

        try {
            // Logeo usuario y obtengo token
            JSONObject obj = new JSONObject();
            obj.put("username", str_usuario);
            obj.put("password", str_password);

            String respuesta = new ConsultorPOSTAPI("login", obj, LOGUEAR_USUARIO, null).execute().get();
            String access_token = ParserJSON.obtenerAccessToken(respuesta);
            if(access_token == null){
                Toast.makeText(this, "Error: usuario inexistente", Toast.LENGTH_LONG).show();
                return;
            }

            Global.token_usuario_actual = access_token;

            // Obtengo datos de usuario
            respuesta = new APIObtenerUsuarioPorToken("user", Global.token_usuario_actual, OBTENER_USUARIO_POR_TOKEN, null).execute().get();
            Usuario nuevo_usuario = ParserJSON.obtenerUsuario(respuesta);
            if(access_token == null){
                Toast.makeText(this, "Error: usuario inexistente", Toast.LENGTH_LONG).show();
                return;
            }

            Global.usuario_actual = nuevo_usuario;

            LocalDB.crearXML(this.getApplicationContext(),
                    Global.usuario_actual.getNombre(),
                    password.getText().toString(),
                    Global.usuario_actual.getMail(),
                    Global.token_usuario_actual);
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Se ah producido un error", Toast.LENGTH_LONG).show();
            return;
        }

        Intent i = new Intent(getApplicationContext(), MapaPrincipal.class);
        startActivity(i);
    }

    public void registrarUsuario(View view)
    {
        Intent i = new Intent(getApplicationContext(), RegistrarUsuario.class);
        startActivity(i);
    }
}
