package losmarinos.blackout.Actividades;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import losmarinos.blackout.Adapters.EmpresaAdapter;
import losmarinos.blackout.Adapters.SucursalAdapter;
import losmarinos.blackout.Constantes;
import losmarinos.blackout.Global;
import losmarinos.blackout.Objetos.Empresa;
import losmarinos.blackout.R;

public class BuscarEmpresa extends AppCompatActivity implements TextWatcher, AdapterView.OnItemSelectedListener {

    EditText textoBusqueda;
    Spinner spnServicios;
    List<Empresa> empresas_filtradas = new ArrayList<>();
    Constantes.SERVICIO servicio = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Buscar Empresa");
        setContentView(R.layout.activity_buscar_empresa);

        textoBusqueda = (EditText)findViewById(R.id.txt_nombre_buscar_empresa);
        textoBusqueda.addTextChangedListener(this);

        spnServicios = (Spinner)findViewById(R.id.spn_servicios_buscar_empresa);
        spnServicios.setOnItemSelectedListener(this);

        empresas_filtradas = Global.empresas;

        this.cargarSpinnerServicios();
        this.cargarListView();
    }

    private void cargarSpinnerServicios()
    {
        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("No especificar");
        spinnerArray.add(Constantes.servicioToString(Constantes.SERVICIO.AGUA));
        spinnerArray.add(Constantes.servicioToString(Constantes.SERVICIO.LUZ));
        spinnerArray.add(Constantes.servicioToString(Constantes.SERVICIO.GAS));
        spinnerArray.add(Constantes.servicioToString(Constantes.SERVICIO.TELEFONO));
        spinnerArray.add(Constantes.servicioToString(Constantes.SERVICIO.INTERNET));
        spinnerArray.add(Constantes.servicioToString(Constantes.SERVICIO.CABLE));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spnServicios.setAdapter(adapter);
    }

    public void cargarListView(){
        EmpresaAdapter adapter = new EmpresaAdapter(this.empresas_filtradas, this, this);
        ListView mi_lista = (ListView)findViewById(R.id.lst_empresa_buscar_empresa);
        mi_lista.setAdapter(adapter);
    }



    public void irAPerfilEmpresa(int id_empresa){
        Intent i = new Intent(getApplicationContext(), PerfilEmpresa.class);
        i.putExtra("idEmpresa", id_empresa);
        try {
            startActivity(i);
        }
        catch (Exception e)
        {
            throw e;
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count){
        filtrarEmpresas();
    }

    @Override
    public void afterTextChanged(Editable s){}

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after){}

    @Override
    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
        this.servicio = Constantes.stringToServicio(this.spnServicios.getSelectedItem().toString());
        filtrarEmpresas();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parentView) {
        // your code here
    }

    public void filtrarEmpresas() {
        this.empresas_filtradas = new ArrayList<>();

        for(int i = 0; i < Global.empresas.size(); i++)
        {
            if (this.servicio == null || this.servicio == Global.empresas.get(i).getTipoServicio())
            {
                String str1 = Global.empresas.get(i).getNombre().toLowerCase();
                String str2 = textoBusqueda.getText().toString().toLowerCase();
                if(str1.contains(str2)){
                    this.empresas_filtradas.add(Global.empresas.get(i));
                }
            }
        }

        this.cargarListView();


    }
}
