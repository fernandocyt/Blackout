package losmarinos.blackout.Adapters;

import android.content.Context;
import android.content.Intent;
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
import losmarinos.blackout.Actividades.PerfilEmpresa;
import losmarinos.blackout.Objetos.Empresa;
import losmarinos.blackout.R;

/**
 * Created by Fernando on 2/9/2017.
 */

public class EmpresaAdapter extends BaseAdapter implements ListAdapter {

    private List<Empresa> list = new ArrayList<Empresa>();
    private Context context;
    static BuscarEmpresa actividad;
    TextView textview_texto;

    public EmpresaAdapter(List<Empresa> list, Context context, BuscarEmpresa actividad) {
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
            view = inflater.inflate(R.layout.item_empresa, null);
        }

        //Handle TextView and display string from your list
        textview_texto = (TextView)view.findViewById(R.id.lbl_texto_empresa_mis_objetos);
        textview_texto.setText(list.get(position).getNombre());

        LinearLayout linea_empresa = (LinearLayout)view.findViewById(R.id.empresa_mis_objetos);
        linea_empresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmpresaAdapter.actividad.irAPerfilEmpresa(list.get(position).getId());
            }
        });

        return view;
    }
}