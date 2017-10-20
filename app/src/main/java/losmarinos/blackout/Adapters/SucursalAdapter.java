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

import losmarinos.blackout.Actividades.MisReportes;
import losmarinos.blackout.Actividades.VerSucursales;
import losmarinos.blackout.Aviso;
import losmarinos.blackout.Constantes;
import losmarinos.blackout.Global;
import losmarinos.blackout.Objetos.Empresa;
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
    Button button_borrar;
    Empresa empresa_actual;

    ProgressDialog progress_dialog;

    public SucursalAdapter(Empresa empresa, Context context, VerSucursales actividad) {
        this.empresa_actual = empresa;
        this.list = empresa.getSucursales();
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

        button_borrar = (Button)view.findViewById(R.id.btn_borrar_sucursal_mis_objetos);
        if(Global.usuario_actual.getTipo() == Constantes.TIPOSUSUARIO.EMPRESA && Global.usuario_actual.getSubId() == empresa_actual.getSubId()) {
            button_borrar.setVisibility(View.VISIBLE);
        }else{
            button_borrar.setVisibility(View.GONE);
        }
        button_borrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress_dialog = Aviso.showProgressDialog(SucursalAdapter.actividad, "Borrando sucursal");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean correcto = SucursalAdapter.actividad.borrarSucursal(list.get(position).getId());
                        if(correcto){
                            empresa_actual.actualizarSucursales(SucursalAdapter.actividad);
                            SucursalAdapter.actividad.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    SucursalAdapter.actividad.cargarListView();
                                    SucursalAdapter.actividad.cargarMapa();
                                }
                            });
                            Aviso.showToast(SucursalAdapter.actividad, "Sucursal borrada");
                        }else{
                            Aviso.showToast(SucursalAdapter.actividad, "No se pudo borrar sucursal");
                        }
                        Aviso.hideProgressDialog(SucursalAdapter.actividad, progress_dialog);
                    }
                }).start();
            }
        });



        LinearLayout linea_sucursal = (LinearLayout)view.findViewById(R.id.sucursal_mis_objetos);
        linea_sucursal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SucursalAdapter.actividad.centrarMapaEnPosicion(list.get(position).getUbicacion());
            }
        });

        return view;
    }
}
