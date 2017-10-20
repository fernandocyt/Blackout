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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import losmarinos.blackout.Actividades.MisPuntosInteres;
import losmarinos.blackout.Actividades.MisReportes;
import losmarinos.blackout.Aviso;
import losmarinos.blackout.Global;
import losmarinos.blackout.Objetos.PuntoInteres;
import losmarinos.blackout.Objetos.Reporte;
import losmarinos.blackout.R;

/**
 * Created by garci on 19/8/2017.
 */

public class PuntoInteresAdapter extends BaseAdapter implements ListAdapter {
    private List<PuntoInteres> list = new ArrayList<PuntoInteres>();
    private Context context;
    static MisPuntosInteres actividad;
    TextView textview_texto;
    //TextView textview_activo;
    //Button button_activar;
    Button button_borrar;

    ProgressDialog progress_dialog;

    public PuntoInteresAdapter(List<PuntoInteres> list, Context context, MisPuntosInteres actividad) {
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
            view = inflater.inflate(R.layout.item_punto_interes, null);
        }

        //Handle TextView and display string from your list
        textview_texto = (TextView)view.findViewById(R.id.lbl_texto_punto_interes_mis_objetos);
        textview_texto.setText(list.get(position).generarTexto());

        /*textview_activo = (TextView)view.findViewById(R.id.lbl_activo_punto_interes_mis_objetos);
        if(list.get(position).isActivo()) {
            textview_activo.setText("Activo");
        }else{
            textview_activo.setText("Inactivo");
        }

        button_activar = (Button)view.findViewById(R.id.btn_activar_punto_interes_mis_objetos);
        if(list.get(position).isActivo()) {
            button_activar.setText("Desactivar");
        }else{
            button_activar.setText("Activar");
        }

        button_activar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list.get(position).isActivo()){
                    list.get(position).setActivo(false);
                }else{
                    list.get(position).setActivo(true);
                }

                MisPuntosInteres mis_puntos_interes = (MisPuntosInteres)context;
                mis_puntos_interes.cargarListView();
            }
        });*/

        button_borrar = (Button)view.findViewById(R.id.btn_borrar_punto_interes_mis_objetos);
        button_borrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress_dialog = Aviso.showProgressDialog(PuntoInteresAdapter.actividad, "Borrando punto de interes");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean correcto = PuntoInteresAdapter.actividad.borrarPuntoDeInteres(list.get(position).getId());
                        if(correcto) {
                            Global.usuario_actual.actualizarPuntosInteres(PuntoInteresAdapter.actividad);
                            PuntoInteresAdapter.actividad.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    PuntoInteresAdapter.actividad.cargarListView();
                                    PuntoInteresAdapter.actividad.cargarMapa();
                                }
                            });
                            Aviso.showToast(PuntoInteresAdapter.actividad, "Punto de interes borrado");
                        }else{
                            Aviso.showToast(PuntoInteresAdapter.actividad, "No se pudo borrar el punto de interes");
                        }
                        Aviso.hideProgressDialog(PuntoInteresAdapter.actividad, progress_dialog);
                    }
                }).start();
            }
        });


        LinearLayout linea_punto_interes = (LinearLayout)view.findViewById(R.id.punto_interes_mis_objetos);
        linea_punto_interes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PuntoInteresAdapter.actividad.centrarMapaEnPosicion(list.get(position).getUbicacion());
            }
        });

        return view;
    }
}
