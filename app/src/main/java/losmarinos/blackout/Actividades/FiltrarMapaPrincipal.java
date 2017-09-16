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
import losmarinos.blackout.ConsultorPOSTAPI;
import losmarinos.blackout.Global;
import losmarinos.blackout.Objetos.Empresa;
import losmarinos.blackout.R;

public class FiltrarMapaPrincipal extends AppCompatActivity
        implements CompoundButton.OnCheckedChangeListener,
        AdapterView.OnItemSelectedListener{

    public static boolean mostrar_cortes = true;
    public static boolean mostrar_reportes = true;
    public static int id_empresa = -1;
    public static Constantes.SERVICIO servicio = null;

    public static int indice_spn_servicios = 0;
    public static int indice_spn_empresas = 0;

    public List<Empresa> empresas_spinner = new ArrayList<>();

    Switch switch_cortes;
    Switch switch_reportes;
    Switch switch_sucursales;
    Spinner spinner_empresas;
    Spinner spinner_servicios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Filtrar mapa");
        setContentView(R.layout.activity_filtrar_mapa_principal);

        switch_cortes = (Switch) findViewById(R.id.switch_cortes_filtrar_mapa_principal);
        switch_cortes.setChecked(FiltrarMapaPrincipal.mostrar_cortes);
        switch_cortes.setOnCheckedChangeListener(this);

        switch_reportes = (Switch) findViewById(R.id.switch_reportes_filtrar_mapa_principal);
        switch_reportes.setChecked(FiltrarMapaPrincipal.mostrar_reportes);
        switch_reportes.setOnCheckedChangeListener(this);

        spinner_servicios = (Spinner) findViewById(R.id.spn_servicios_filtrar_mapa_principal);
        spinner_servicios.setOnItemSelectedListener(this);

        spinner_empresas = (Spinner) findViewById(R.id.spn_empresas_filtrar_mapa_principal);

        this.cargarSpinnerServicios();
        this.cargarSpinnerEmpresas(FiltrarMapaPrincipal.servicio);

        spinner_servicios.setSelection(indice_spn_servicios);
        spinner_empresas.setSelection(indice_spn_empresas);

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

        empresas_spinner = new ArrayList<>();

        for(int i = 0; i < Global.empresas.size(); i++)
        {
            if(Global.empresas.get(i).getTipoServicio() == servicio) {
                spinnerArray.add(Global.empresas.get(i).getNombre());
                empresas_spinner.add(Global.empresas.get(i));
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

        if(this.spinner_empresas.getSelectedItem().toString().equals("No")) {
            FiltrarMapaPrincipal.id_empresa = -1;
        }else{
            Empresa empresa_elegida = this.empresas_spinner.get(this.spinner_empresas.getSelectedItemPosition()-1);
            FiltrarMapaPrincipal.id_empresa = empresa_elegida.getSubId();
        }
        indice_spn_empresas = this.spinner_empresas.getSelectedItemPosition();

        if(this.spinner_servicios.getSelectedItem().toString().equals("No")){
            FiltrarMapaPrincipal.servicio = null;
        }else {
            FiltrarMapaPrincipal.servicio = Constantes.stringToServicio(this.spinner_servicios.getSelectedItem().toString());
        }
        indice_spn_servicios = this.spinner_servicios.getSelectedItemPosition();

        super.onBackPressed();
    }
}
