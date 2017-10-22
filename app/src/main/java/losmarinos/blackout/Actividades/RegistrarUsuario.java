package losmarinos.blackout.Actividades;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONObject;

import losmarinos.blackout.Aviso;
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

    ProgressDialog progress_dialog;

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
        progress_dialog = Aviso.showProgressDialog(this, "Registrando usuario");

        final String nombre = edittext_nombre.getText().toString();
        final String email = edittext_email.getText().toString();
        final String pass1 = edittext_password.getText().toString();
        final String pass2 = edittext_password2.getText().toString();

        if (!Validador.validarCamposVacios(this, (LinearLayout) findViewById(R.id.lyt_registrar_usuario)))
            return;

        if (!Validador.validarMail(this, email))
            return;

        if (!Validador.validarPasswords(this, pass1, pass2))
            return;

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    JSONObject nuevo_usu = ParserJSON.crearJSONUsuario(nombre, email, pass1, pass2);

                    String respuesta = new ConsultorPOSTAPI("register", null, nuevo_usu, Constantes.TAGAPI.REGISTRAR_USUARIO, null).execute().get();
                    StringBuilder msj_error = new StringBuilder();
                    if (ParserJSON.esError(respuesta, msj_error)) {
                        Aviso.hideProgressDialog(RegistrarUsuario.this, progress_dialog);
                        Aviso.showToast(RegistrarUsuario.this, msj_error.toString());
                        return;
                    } else {
                        Aviso.hideProgressDialog(RegistrarUsuario.this, progress_dialog);
                        Aviso.showToast(RegistrarUsuario.this, "Usuario registrado correctamente");
                        RegistrarUsuario.this.finish();
                    }

                } catch (Exception e) {
                    Aviso.hideProgressDialog(RegistrarUsuario.this, progress_dialog);
                    Aviso.showToast(RegistrarUsuario.this, "Error");
                }
            }
        }).start();

    }
}
