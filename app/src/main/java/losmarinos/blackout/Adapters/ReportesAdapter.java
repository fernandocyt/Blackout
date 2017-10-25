package losmarinos.blackout.Adapters;

import android.app.ProgressDialog;
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
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import losmarinos.blackout.Actividades.MisReportes;
import losmarinos.blackout.Actividades.PerfilCorte;
import losmarinos.blackout.Aviso;
import losmarinos.blackout.Constantes;
import losmarinos.blackout.LocalDB;
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
    TextView textview_confirmacion;
    Button button_resolver;
    Button button_confirmar;

    ProgressDialog progress_dialog;

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
            view = inflater.inflate(R.layout.item_reporte, null);
        }

        //Handle TextView and display string from your list
        textview_texto = (TextView)view.findViewById(R.id.lbl_texto_reporte_mis_objetos);
        textview_texto.setText(list.get(position).generarTexto());

        textview_activo = (TextView)view.findViewById(R.id.lbl_activo_reporte_mis_objetos);
        textview_confirmacion = (TextView)view.findViewById(R.id.lbl_confirmacion_reporte_mis_objetos);
        button_resolver = (Button)view.findViewById(R.id.btn_resolver_reporte_mis_objetos);
        button_confirmar = (Button)view.findViewById(R.id.btn_confirmar_reporte_mis_objetos);

        if(list.get(position).isResuelto()) {
            textview_activo.setText("Resuelto");
            button_resolver.setVisibility(View.GONE);
            button_confirmar.setVisibility(View.GONE);
        }else{
            textview_activo.setText("Pendiente");
            button_resolver.setVisibility(View.VISIBLE);
            button_confirmar.setVisibility(View.VISIBLE);
        }

        Date fecha_confirm = list.get(position).getFechaConfirmacion();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyy HH:mm");
        String str_fecha = format.format(fecha_confirm);

        Date ahora = Calendar.getInstance().getTime();
        long dif_horas_conf_horas = (ahora.getTime() - fecha_confirm.getTime()) / (60 * 60 * 1000);

        textview_confirmacion.setText("Ultima confirmación:\n" + str_fecha + " (hace " + String.valueOf(dif_horas_conf_horas) + "hs)");


        button_resolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress_dialog = Aviso.showProgressDialog(ReportesAdapter.actividad, "Resolviendo reporte");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean correcto = list.get(position).resolver();
                        if(correcto) {
                            ReportesAdapter.actividad.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ReportesAdapter.actividad.cargarListView();
                                    ReportesAdapter.actividad.cargarMapa();
                                }
                            });
                            Aviso.showToast(ReportesAdapter.actividad, "Reporte de " + Constantes.servicioToString(list.get(position).getServicio()) + " resuelto");
                        }else{
                            Aviso.showToast(ReportesAdapter.actividad, "No se pudo resolver el reporte");
                        }
                        Aviso.hideProgressDialog(ReportesAdapter.actividad, progress_dialog);
                    }
                }).start();
            }
        });

        button_confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            progress_dialog = Aviso.showProgressDialog(ReportesAdapter.actividad, "Confirmando reporte");
            new Thread(new Runnable() {
                @Override
                public void run() {
                boolean correcto = list.get(position).confirmar();
                if(correcto) {
                    ReportesAdapter.actividad.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ReportesAdapter.actividad.cargarListView();
                            ReportesAdapter.actividad.cargarMapa();
                        }
                    });
                    Aviso.showToast(ReportesAdapter.actividad, "Reporte confirmado");
                }else{
                    Aviso.showToast(ReportesAdapter.actividad, "No se pudo confirmar el reporte");
                }
                Aviso.hideProgressDialog(ReportesAdapter.actividad, progress_dialog);
                }
            }).start();
            }
        });

        LinearLayout linea_reporte = (LinearLayout)view.findViewById(R.id.reporte_mis_objetos);
        linea_reporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReportesAdapter.actividad.centrarMapaEnPosicion(list.get(position).getUbicacion());
            }
        });

        return view;
    }


}
