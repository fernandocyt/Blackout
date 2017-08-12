package losmarinos.blackout.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import losmarinos.blackout.Actividades.MisReportes;
import losmarinos.blackout.Actividades.PerfilCorte;
import losmarinos.blackout.Objetos.Reporte;
import losmarinos.blackout.Objetos.Respuesta;
import losmarinos.blackout.R;

/**
 * Created by garci on 28/7/2017.
 */

public class ReportesAdapter extends BaseAdapter implements ListAdapter {
    private List<Reporte> list = new ArrayList<Reporte>();
    private Context context;
    static MisReportes actividad;
    TextView textview_texto;
    TextView textview_activo;
    Button button_resolver;

    public ReportesAdapter(List<Reporte> list, Context context, MisReportes actividad) {
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
            view = inflater.inflate(R.layout.reporte_mis_reportes, null);
        }

        //Handle TextView and display string from your list
        textview_texto = (TextView)view.findViewById(R.id.lbl_texto_reporte_mis_reportes);
        textview_texto.setText(list.get(position).generarTextoReporte());

        textview_activo = (TextView)view.findViewById(R.id.lbl_activo_reporte_mis_reportes);
        if(list.get(position).isResuelto()) {
            textview_activo.setText("Resuelto");
        }else{
            textview_activo.setText("Pendiente");
        }

        button_resolver = (Button)view.findViewById(R.id.btn_resolver_reporte_mis_reportes);
        if(list.get(position).isResuelto()) {
            button_resolver.setVisibility(View.GONE);
        }
        button_resolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_resolver.setVisibility(View.GONE);
                list.get(position).setResuelto(true);
                MisReportes mis_reportes = (MisReportes)context;
                mis_reportes.cargarListView();
            }
        });


        LinearLayout linea_reporte = (LinearLayout)view.findViewById(R.id.reporte_mis_reportes);
        linea_reporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReportesAdapter.actividad.centrarMapaEnPosicion(list.get(position).getUbicacion());
            }
        });

        return view;
    }


}
