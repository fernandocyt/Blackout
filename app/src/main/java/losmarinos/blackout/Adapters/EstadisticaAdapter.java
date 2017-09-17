package losmarinos.blackout.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import losmarinos.blackout.Actividades.BuscarEmpresa;
import losmarinos.blackout.Actividades.ConsultarEstadisticas;
import losmarinos.blackout.Calculos;
import losmarinos.blackout.Global;
import losmarinos.blackout.Objetos.Empresa;
import losmarinos.blackout.R;

/**
 * Created by garci on 17/9/2017.
 */

public class EstadisticaAdapter extends BaseAdapter implements ListAdapter {

    private List<Calculos.valorEmpresa> list = new ArrayList<>();
    private Context context;
    static ConsultarEstadisticas actividad;
    TextView textview_empresa;
    TextView textview_valor;

    public EstadisticaAdapter(List<Calculos.valorEmpresa> list, Context context, ConsultarEstadisticas actividad) {
        this.list = list;
        this.actividad = actividad;
        this.context = context;
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
            view = inflater.inflate(R.layout.item_estadistica, null);
        }

        Empresa empresa = Global.encontrarEmpresaPorId(list.get(position).id_empresa);

        //Handle TextView and display string from your list
        textview_empresa = (TextView)view.findViewById(R.id.lbl_empresa_estadistica_mis_objetos);
        textview_empresa.setText(empresa.getNombre());

        textview_valor = (TextView)view.findViewById(R.id.lbl_valor_estadistica_mis_objetos);
        textview_valor.setText((int)list.get(position).valor + " " + list.get(position).unidad);

        LinearLayout linea_empresa = (LinearLayout)view.findViewById(R.id.estadistica_mis_objetos);
        linea_empresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EstadisticaAdapter.actividad.irAPerfilEmpresa(list.get(position).id_empresa);
            }
        });

        return view;
    }
}
