package losmarinos.blackout.Actividades;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import losmarinos.blackout.Constantes;
import losmarinos.blackout.ConsultorPOSTAPI;
import losmarinos.blackout.ObservadorAPI;
import losmarinos.blackout.ParserJSON;
import losmarinos.blackout.R;

import static losmarinos.blackout.Constantes.TAGAPI.REGISTRAR_USUARIO;

public class RegistrarUsuario extends AppCompatActivity {
    EditText edittext_nombre;
    EditText edittext_email;
    EditText edittext_password;
    EditText edittext_password2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Registrar usuario");
        setContentView(R.layout.activity_registrar_usuario);

        edittext_nombre = (EditText)findViewById(R.id.txt_nombre_registrar_usuario);
        edittext_email = (EditText)findViewById(R.id.txt_email_registrar_usuario);
        edittext_password = (EditText)findViewById(R.id.txt_password_registrar_usuario);
        edittext_password2 = (EditText)findViewById(R.id.txt_password2_registrar_usuario);
    }

    public void registrarUsuario(View view)
    {
        String nombre = edittext_nombre.getText().toString();
        String email = edittext_email.getText().toString();
        String pass1 = edittext_password.getText().toString();
        String pass2 = edittext_password2.getText().toString();

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

        try {
            JSONObject obj = new JSONObject();
            obj.put("name", nombre);
            obj.put("email", email);
            obj.put("password", pass1);
            obj.put("password_confirmation", pass1);

            String respuesta = new ConsultorPOSTAPI("register", null, obj, Constantes.TAGAPI.REGISTRAR_USUARIO, null).execute().get();
            StringBuilder msj_error = new StringBuilder();
            if(ParserJSON.esError(respuesta, msj_error))
            {
                Toast.makeText(this, msj_error, Toast.LENGTH_LONG).show();
                return;
            }
            else
            {
                Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_LONG).show();
                super.onBackPressed();
            }

        }catch (Exception e){
            Toast.makeText(this, "Ha surgido un problema", Toast.LENGTH_LONG).show();
        }

    }
}
