package losmarinos.blackout.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import losmarinos.blackout.Actividades.MisCortesProgramados;
import losmarinos.blackout.Actividades.MisPuntosInteres;
import losmarinos.blackout.Constantes;
import losmarinos.blackout.Global;
import losmarinos.blackout.Objetos.Corte;
import losmarinos.blackout.Objetos.PuntoInteres;
import losmarinos.blackout.R;

/**
 * Created by Fernando on 16/9/2017.
 */

public class CorteProgramadoAdapter extends BaseAdapter implements ListAdapter {
    private List<Corte> list = new ArrayList<Corte>();
    private Context context;
    static MisCortesProgramados actividad;
    TextView textview_texto;
    //TextView textview_activo;
    //Button button_activar;
    Button button_resolver;

    public CorteProgramadoAdapter(List<Corte> list, Context context, MisCortesProgramados actividad) {
        this.list = list;
        this.context = context;
        this.actividad = actividad;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        //return list.get(pos).getId();
        //just return 0 if your list items do not have an Id variable.
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_corte_programado, null);
        }

        //Handle TextView and display string from your list
        textview_texto = (TextView)view.findViewById(R.id.lbl_texto_corte_programado_mis_objetos);
        textview_texto.setText(list.get(position).generarTexto());

        button_resolver = (Button)view.findViewById(R.id.btn_resolver_corte_programado_mis_objetos);
        if(list.get(position).isResuelto()){
            button_resolver.setVisibility(View.GONE);
        }else{
            button_resolver.setVisibility(View.VISIBLE);
        }

        button_resolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MisCortesProgramados mis_cortes_programados = (MisCortesProgramados)context;
                boolean correcto = mis_cortes_programados.resolverCorteProgramado(list.get(position));
                if(correcto){
                    Toast.makeText(context, "Corte resuelto", Toast.LENGTH_LONG).show();
                    mis_cortes_programados.cargarListView();
                    mis_cortes_programados.cargarMapa();
                }else{
                    Toast.makeText(context, "No se pudo resolver el corte", Toast.LENGTH_LONG).show();
                }
            }
        });


        LinearLayout linea_corte_programado = (LinearLayout)view.findViewById(R.id.corte_programado_mis_objetos);
        linea_corte_programado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CorteProgramadoAdapter.actividad.centrarMapaEnPosicion(list.get(position).getUbicacion());
            }
        });

        return view;
    }
}
