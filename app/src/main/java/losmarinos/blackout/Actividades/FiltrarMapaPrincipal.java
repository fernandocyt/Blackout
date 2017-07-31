package losmarinos.blackout.Actividades;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import losmarinos.blackout.R;

public class FiltrarMapaPrincipal extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    public static boolean mostrar_cortes = true;
    public static boolean mostrar_reportes = true;
    public static boolean mostrar_sucursales = true;

    Switch switch_cortes;
    Switch switch_reportes;
    Switch switch_sucursales;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtrar_mapa_principal);

        switch_cortes = (Switch) findViewById(R.id.switch_cortes_filtrar_mapa_principal);
        switch_cortes.setChecked(FiltrarMapaPrincipal.mostrar_cortes);
        switch_cortes.setOnCheckedChangeListener(this);

        switch_reportes = (Switch) findViewById(R.id.switch_reportes_filtrar_mapa_principal);
        switch_reportes.setChecked(FiltrarMapaPrincipal.mostrar_reportes);
        switch_reportes.setOnCheckedChangeListener(this);

        switch_sucursales = (Switch) findViewById(R.id.switch_sucursales_filtrar_mapa_principal);
        switch_sucursales.setChecked(FiltrarMapaPrincipal.mostrar_sucursales);
        switch_sucursales.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(switch_cortes.isChecked()){
            FiltrarMapaPrincipal.mostrar_cortes = true;
        }else{
            FiltrarMapaPrincipal.mostrar_cortes = false;
        }
        if(switch_reportes.isChecked()){
            FiltrarMapaPrincipal.mostrar_reportes = true;
        }else{
            FiltrarMapaPrincipal.mostrar_reportes = false;
        }
        if(switch_sucursales.isChecked()){
            FiltrarMapaPrincipal.mostrar_sucursales = true;
        }else{
            FiltrarMapaPrincipal.mostrar_sucursales = false;
        }
    }
}
