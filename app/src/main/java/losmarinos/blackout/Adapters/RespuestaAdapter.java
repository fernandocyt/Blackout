package losmarinos.blackout.Adapters;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import losmarinos.blackout.Objetos.Usuario;
import losmarinos.blackout.R;
import losmarinos.blackout.Actividades.PerfilCorte;
import losmarinos.blackout.Objetos.Corte;
import losmarinos.blackout.Objetos.Reporte;
import losmarinos.blackout.Objetos.Respuesta;

public class RespuestaAdapter extends BaseAdapter implements ListAdapter {
    private List<Respuesta> list = new ArrayList<Respuesta>();
    private Context context;
    PerfilCorte actividad;
    TextView textview_texto;
    TextView textview_usuario;


    public RespuestaAdapter(List<Respuesta> list, Context context, PerfilCorte actividad) {
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
            view = inflater.inflate(R.layout.item_respuesta, null);
        }

        //Handle TextView and display string from your list
        textview_texto = (TextView)view.findViewById(R.id.lbl_respuesta_perfil_corte);
        textview_texto.setText(list.get(position).getTexto());

        Usuario usuario = list.get(position).getUsuario();
        textview_usuario = (TextView)view.findViewById(R.id.lbl_usuario_respuesta_perfil_corte);
        if (usuario != null) {
            textview_usuario.setText(usuario.getNombre());
        }else{
            textview_usuario.setText("");
        }

        return view;
    }
}
