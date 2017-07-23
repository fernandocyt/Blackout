package losmarinos.blackout.Actividades;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;

import losmarinos.blackout.ConsultorAPI;
import losmarinos.blackout.Objetos.Corte;
import losmarinos.blackout.R;

public class PerfilCorte extends AppCompatActivity {

    TextView textview_servicio;
    TextView textview_empresa;
    TextView textview_fecha;
    TextView textview_cantidad_reportes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_corte);

        //Corte corte = (Corte)getIntent().getSerializableExtra("Corte");

        textview_servicio = (TextView)findViewById(R.id.lbl_servicio_perfil_corte);
        textview_empresa = (TextView)findViewById(R.id.lbl_empresa_perfil_corte);
        textview_fecha = (TextView)findViewById(R.id.lbl_fecha_inicio_perfil_corte);
        textview_cantidad_reportes = (TextView)findViewById(R.id.lbl_cant_reportes_perfil_corte);

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        Corte corte = ConsultorAPI.cortes.get(0);
        textview_servicio.setText(corte.getServicio());
        textview_empresa.setText(corte.getEmpresa());
        String fecha = df.format(corte.getFecha_inicio());
        textview_fecha.setText(fecha);
        textview_cantidad_reportes.setText(Integer.toString(corte.getCantidad_reportes()));

    }
}
