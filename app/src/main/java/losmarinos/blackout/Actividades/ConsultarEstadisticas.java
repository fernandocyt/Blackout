package losmarinos.blackout.Actividades;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import losmarinos.blackout.Adapters.EmpresaAdapter;
import losmarinos.blackout.Adapters.EstadisticaAdapter;
import losmarinos.blackout.Calculos;
import losmarinos.blackout.Constantes;
import losmarinos.blackout.Global;
import losmarinos.blackout.Objetos.Empresa;
import losmarinos.blackout.R;

public class ConsultarEstadisticas extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner spnEstadistica;
    Spinner spnOrdenar;
    Spinner spnServicios;
    List<Calculos.valorEmpresa> estadisticas = new ArrayList<>();
    Constantes.SERVICIO servicio = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultar_estadisticas);

        spnServicios = (Spinner)findViewById(R.id.spn_servicios_consultar_estadisticas);
        spnServicios.setOnItemSelectedListener(this);

        spnEstadistica = (Spinner)findViewById(R.id.spn_estadisticas_consultar_estadisticas);
        spnEstadistica.setOnItemSelectedListener(this);

        spnOrdenar = (Spinner)findViewById(R.id.spn_ordenar_consultar_estadisticas);
        spnOrdenar.setOnItemSelectedListener(this);

        this.cargarSpinnerEstadisticas();
        this.cargarSpinnerOrdenar();
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

    private void cargarSpinnerEstadisticas()
    {
        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("Porcentaje de cortes total");
        spinnerArray.add("Cantidad de cortes programados");
        spinnerArray.add("Tiempo promedio de resolución de cortes");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spnEstadistica.setAdapter(adapter);
    }

    private void cargarSpinnerOrdenar()
    {
        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("Mayor a menor");
        spinnerArray.add("Menor a mayor");
        spinnerArray.add("Alfabéticamente");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spnOrdenar.setAdapter(adapter);
    }

    public void cargarListView(){
        EstadisticaAdapter adapter = new EstadisticaAdapter(this.estadisticas, this, this);
        ListView mi_lista = (ListView)findViewById(R.id.lst_empresa_consultar_estadisticas);
        mi_lista.setAdapter(adapter);
    }


    @Override
    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
        this.servicio = Constantes.stringToServicio(this.spnServicios.getSelectedItem().toString());
        this.actualizarEstadisticas();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parentView) {
    }

    public void actualizarEstadisticas(){
        switch(spnEstadistica.getSelectedItemPosition())
        {
            case 0:
                this.estadisticas = Calculos.porcentajeCortesTotal(this.servicio);
                break;
            case 1:
                this.estadisticas = Calculos.cantidadCortesProgramados(this.servicio);
                break;
            case 2:
                this.estadisticas = Calculos.tiempoPromedioDeResolucionPorEmpresa(this.servicio);
                break;
        }

        Calculos.valorEmpresa.ordenar = spnOrdenar.getSelectedItemPosition();
        Collections.sort(this.estadisticas);

        this.cargarListView();
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
}