package losmarinos.blackout.Actividades;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import losmarinos.blackout.Aviso;
import losmarinos.blackout.ConsultorGETAPI;
import losmarinos.blackout.ConsultorPOSTAPI;
import losmarinos.blackout.Global;
import losmarinos.blackout.LocalDB;
import losmarinos.blackout.ParserJSON;
import losmarinos.blackout.R;
import losmarinos.blackout.Validador;

import static losmarinos.blackout.Constantes.TAGAPI.DESHABILITAR_USUARIO;

public class Preferencias extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{

    public static boolean notificaciones = true;
    public static boolean vibrar = true;
    public static boolean sonar = true;
    public static boolean gps = true;

    Switch switch_notificaciones;
    Switch switch_vibrar;
    Switch switch_sonar;
    Switch switch_gps;

    EditText edittext_nombre_usuario;
    EditText edittext_mail_usuario;
    EditText edittext_pass_usuario;
    EditText edittext_pass2_usuario;

    ProgressDialog progress_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferencias);

        int[] notificar = new int[1];
        int[] vibrar = new int[1];
        int[] sonar = new int[1];
        int[] gps = new int[1];
        boolean correcto = LocalDB.leerArchivoJSONPreferencias(this, notificar, vibrar, sonar, gps);

        if(correcto) {
            Preferencias.notificaciones = (notificar[0] == 1);
            Preferencias.vibrar = (vibrar[0] == 1);
            Preferencias.sonar = (sonar[0] == 1);
            Preferencias.gps = (gps[0] == 1);
        }

        switch_notificaciones = (Switch) findViewById(R.id.switch_notificaciones_preferencias);
        switch_notificaciones.setChecked(Preferencias.notificaciones);
        switch_notificaciones.setOnCheckedChangeListener(this);

        switch_vibrar = (Switch) findViewById(R.id.switch_vibrar_preferencias);
        switch_vibrar.setChecked(Preferencias.vibrar);
        switch_vibrar.setOnCheckedChangeListener(this);

        switch_sonar = (Switch) findViewById(R.id.switch_sonar_preferencias);
        switch_sonar.setChecked(Preferencias.notificaciones);
        switch_sonar.setOnCheckedChangeListener(this);

        switch_gps = (Switch) findViewById(R.id.switch_gps_preferencias);
        switch_gps.setChecked(Preferencias.notificaciones);
        switch_gps.setOnCheckedChangeListener(this);

        edittext_nombre_usuario = (EditText)findViewById(R.id.txt_nombre_preferencias);
        edittext_mail_usuario = (EditText)findViewById(R.id.txt_email_preferencias);
        edittext_pass_usuario = (EditText)findViewById(R.id.txt_password_preferencias);
        edittext_pass2_usuario = (EditText)findViewById(R.id.txt_password2_preferencias);

        edittext_nombre_usuario.setText(Global.usuario_actual.getNombre());
        edittext_mail_usuario.setText(Global.usuario_actual.getMail());
        edittext_pass_usuario.setText(Global.usuario_actual.getPass());
        edittext_pass2_usuario.setText(Global.usuario_actual.getPass());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(!switch_notificaciones.isChecked()){
            switch_vibrar.setChecked(false);
            switch_sonar.setChecked(false);
        }

        Preferencias.notificaciones = switch_notificaciones.isChecked();
        Preferencias.vibrar = switch_vibrar.isChecked();
        Preferencias.sonar = switch_sonar.isChecked();
        Preferencias.gps = switch_gps.isChecked();
    }

    public void guardarPreferencias(View view){
        LocalDB.crearArchivoJSONPreferencias(this,
                Preferencias.notificaciones,
                Preferencias.vibrar,
                Preferencias.sonar,
                Preferencias.gps);

        Toast.makeText(this, "Preferencias guardadas", Toast.LENGTH_LONG).show();
    }

    public void modificarUsuario(View view){
        String nombre = edittext_nombre_usuario.getText().toString();
        String email = edittext_mail_usuario.getText().toString();
        String pass = edittext_pass_usuario.getText().toString();
        String pass2 = edittext_pass2_usuario.getText().toString();

        if(!Validador.validarCamposVacios(this, (LinearLayout)findViewById(R.id.lyt_preferencias)))
            return;

        if(!Validador.validarMail(this, email))
            return;

        if(!Validador.validarPasswords(this, pass, pass2))
            return;

    }

    public void bajaUsuario(View view){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        realizarBajaUsuario();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Seguro que quieres dar de baja tu usuario?")
                .setPositiveButton("Si", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show();
    }

    public void realizarBajaUsuario(){
        progress_dialog = Aviso.showProgressDialog(this, "Dando de baja usuario");
        new Thread(new Runnable() {
            @Override
            public void run() {
                int id = Global.usuario_actual.getIdUsuario();
                try {
                    String respuesta = new ConsultorGETAPI("user/deshabilitar", Global.token_usuario_actual, DESHABILITAR_USUARIO, null).execute().get();
                    StringBuilder mensaje_error = new StringBuilder();
                    if(ParserJSON.esError(respuesta, mensaje_error)){
                        Aviso.hideProgressDialog(Preferencias.this, progress_dialog);
                        Aviso.showToast(Preferencias.this, mensaje_error.toString());
                    }else{
                        Aviso.hideProgressDialog(Preferencias.this, progress_dialog);
                        Aviso.showToast(Preferencias.this, "El usuario fue dado de baja exitosamente");
                        MapaPrincipal.flag_cerrar_sesion = true;
                        Preferencias.this.finish();
                    }
                }catch (Exception e){
                    Aviso.hideProgressDialog(Preferencias.this, progress_dialog);
                    Aviso.showToast(Preferencias.this, "Error");
                }
            }
        }).start();
    }
}
