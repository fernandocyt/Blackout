package losmarinos.blackout.Actividades;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONObject;

import losmarinos.blackout.Constantes;
import losmarinos.blackout.ConsultorPOSTAPI;
import losmarinos.blackout.ObservadorAPI;
import losmarinos.blackout.ParserJSON;
import losmarinos.blackout.R;
import losmarinos.blackout.Validador;

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

        if(!Validador.validarCamposVacios(this, (LinearLayout)findViewById(R.id.lyt_registrar_usuario)))
            return;

        if(!Validador.validarMail(this, email))
            return;

        if(!Validador.validarPasswords(this, pass1, pass2))
            return;

        try {
            JSONObject nuevo_usu = ParserJSON.crearJSONUsuario(nombre, email, pass1, pass2);

            String respuesta = new ConsultorPOSTAPI("register", null, nuevo_usu, Constantes.TAGAPI.REGISTRAR_USUARIO, null).execute().get();
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
