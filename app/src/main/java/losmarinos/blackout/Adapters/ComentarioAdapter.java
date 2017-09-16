package losmarinos.blackout.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import losmarinos.blackout.Actividades.PerfilEmpresa;
import losmarinos.blackout.Global;
import losmarinos.blackout.Objetos.Comentario;
import losmarinos.blackout.Objetos.Usuario;
import losmarinos.blackout.R;

/**
 * Created by Fernando on 17/8/2017.
 */

public class ComentarioAdapter extends BaseAdapter implements ListAdapter {
    private List<Comentario> list = new ArrayList<Comentario>();
    private Context context;
    PerfilEmpresa actividad;
    TextView textview_texto;
    TextView textview_usuario;

    public ComentarioAdapter(List<Comentario> list, Context context, PerfilEmpresa actividad) {
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
            view = inflater.inflate(R.layout.item_comentario, null);
        }

        //Handle TextView and display string from your list
        textview_texto = (TextView)view.findViewById(R.id.lbl_comentario_perfil_empresa);
        textview_texto.setText(list.get(position).getTexto());

        Usuario usuario = list.get(position).getUsuario();
        textview_usuario = (TextView)view.findViewById(R.id.lbl_usuario_comentario_perfil_empresa);
        if (usuario != null) {
            textview_usuario.setText(usuario.getNombre());
        }else{
            textview_usuario.setText("");
        }

        RelativeLayout linea = (RelativeLayout)view.findViewById(R.id.relative);

        return view;
    }

}
