package losmarinos.blackout.Actividades;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.List;

import losmarinos.blackout.Constantes;
import losmarinos.blackout.ConsultorAPI;
import losmarinos.blackout.R;

public class FiltrarMapaPrincipal extends AppCompatActivity
        implements CompoundButton.OnCheckedChangeListener,
        AdapterView.OnItemSelectedListener{

    public static boolean mostrar_cortes = true;
    public static boolean mostrar_reportes = true;
    public static boolean mostrar_sucursales = true;
    public static String nombre_empresa = null;
    public static Constantes.SERVICIO servicio = null;

    Switch switch_cortes;
    Switch switch_reportes;
    Switch switch_sucursales;
    Spinner spinner_empresas;
    Spinner spinner_servicios;

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

        spinner_servicios = (Spinner) findViewById(R.id.spn_servicios_filtrar_mapa_principal);
        spinner_servicios.setOnItemSelectedListener(this);

        spinner_empresas = (Spinner) findViewById(R.id.spn_empresas_filtrar_mapa_principal);

        this.cargarSpinnerServicios();
        if(FiltrarMapaPrincipal.servicio != null) {
            switch (FiltrarMapaPrincipal.servicio) {
                case AGUA:
                    spinner_servicios.setSelection(1);
                    break;
                case LUZ:
                    spinner_servicios.setSelection(2);
                    break;
                case GAS:
                    spinner_servicios.setSelection(3);
                    break;
                case TELEFONO:
                    spinner_servicios.setSelection(4);
                    break;
                case INTERNET:
                    spinner_servicios.setSelection(5);
                    break;
                case CABLE:
                    spinner_servicios.setSelection(6);
                    break;
            }
        }

        this.cargarSpinnerEmpresas(FiltrarMapaPrincipal.servicio);
        if(FiltrarMapaPrincipal.nombre_empresa != null) {
            for (int i = 0; i < spinner_empresas.getCount(); i++) {
                String nombre_empresa_item = spinner_empresas.getItemAtPosition(i).toString();
                if (nombre_empresa_item.equals(FiltrarMapaPrincipal.nombre_empresa)) {
                    spinner_empresas.setSelection(i);
                }
            }
        }
    }

    private void cargarSpinnerServicios()
    {
        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("No");
        spinnerArray.add(Constantes.servicioToString(Constantes.SERVICIO.AGUA));
        spinnerArray.add(Constantes.servicioToString(Constantes.SERVICIO.LUZ));
        spinnerArray.add(Constantes.servicioToString(Constantes.SERVICIO.GAS));
        spinnerArray.add(Constantes.servicioToString(Constantes.SERVICIO.TELEFONO));
        spinnerArray.add(Constantes.servicioToString(Constantes.SERVICIO.INTERNET));
        spinnerArray.add(Constantes.servicioToString(Constantes.SERVICIO.CABLE));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinner_servicios.setAdapter(adapter);
    }

    private void cargarSpinnerEmpresas(Constantes.SERVICIO servicio)
    {
        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("No");

        for(int i = 0; i < ConsultorAPI.empresas.size(); i++)
        {
            if(ConsultorAPI.empresas.get(i).getTipoServicio() == servicio) {
                spinnerArray.add(ConsultorAPI.empresas.get(i).getNombre());
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinner_empresas.setAdapter(adapter);
    }


    @Override
    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
        String servicio = this.spinner_servicios.getSelectedItem().toString();

        this.cargarSpinnerEmpresas(Constantes.stringToServicio(servicio));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parentView) {
        // your code here
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }

    public void aplicarFiltros(View view)
    {
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

        if(this.spinner_empresas.getSelectedItem().toString().equals("No")) {
            FiltrarMapaPrincipal.nombre_empresa = null;
        }else{
            FiltrarMapaPrincipal.nombre_empresa = this.spinner_empresas.getSelectedItem().toString();
        }

        if(this.spinner_servicios.getSelectedItem().toString().equals("No")){
            FiltrarMapaPrincipal.servicio = null;
        }else {
            FiltrarMapaPrincipal.servicio = Constantes.stringToServicio(this.spinner_servicios.getSelectedItem().toString());
        }

        super.onBackPressed();
    }
}
