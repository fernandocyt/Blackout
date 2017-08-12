package losmarinos.blackout.Actividades;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import losmarinos.blackout.Constantes;
import losmarinos.blackout.ConsultorAPI;
import losmarinos.blackout.ObservadorAPI;
import losmarinos.blackout.R;

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

        usuario.setText("jajaja\u0040gmail.com");
        password.setText("jajaja");
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

        ConsultorAPI.link = Constantes.LINK_API + "login";
        ConsultorAPI.obj = obj;
        ConsultorAPI.observador = this;

        new ConsultorAPI().execute();
    }

    @Override
    public void obtenerRespuestaAPI(JSONObject respuesta, boolean correcto)
    {
        if(correcto) {
            Toast.makeText(this, "Logeado correctamente", Toast.LENGTH_LONG).show();

            Intent i = new Intent(getApplicationContext(), MapaPrincipal.class);
            startActivity(i);
        }
    }

    public void registrarUsuario(View view)
    {
        Intent i = new Intent(getApplicationContext(), RegistrarUsuario.class);
        startActivity(i);
    }
}
