package losmarinos.blackout.Actividades;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import losmarinos.blackout.LocalDB;
import losmarinos.blackout.R;

public class Preferencias extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{

    public static boolean notificaciones = true;
    public static boolean vibrar = true;
    public static boolean sonar = true;
    public static boolean gps = true;

    Switch switch_notificaciones;
    Switch switch_vibrar;
    Switch switch_sonar;
    Switch switch_gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferencias);

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
}
