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

import java.util.ArrayList;
import java.util.List;

import losmarinos.blackout.Actividades.MisReportes;
import losmarinos.blackout.Actividades.VerSucursales;
import losmarinos.blackout.Objetos.Reporte;
import losmarinos.blackout.Objetos.Sucursal;
import losmarinos.blackout.R;

/**
 * Created by Fernando on 23/8/2017.
 */

public class SucursalAdapter extends BaseAdapter implements ListAdapter {

    private List<Sucursal> list = new ArrayList<Sucursal>();
    private Context context;
    static VerSucursales actividad;
    TextView textview_texto;

    public SucursalAdapter(List<Sucursal> list, Context context, VerSucursales actividad) {
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
            view = inflater.inflate(R.layout.item_sucursal, null);
        }

        //Handle TextView and display string from your list
        textview_texto = (TextView)view.findViewById(R.id.lbl_texto_sucursal_mis_objetos);
        textview_texto.setText(list.get(position).generarTexto());

        LinearLayout linea_reporte = (LinearLayout)view.findViewById(R.id.sucursal_mis_objetos);
        linea_reporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SucursalAdapter.actividad.centrarMapaEnPosicion(list.get(position).getUbicacion());
            }
        });

        return view;
    }
}
