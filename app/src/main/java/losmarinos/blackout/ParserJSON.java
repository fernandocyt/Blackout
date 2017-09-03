package losmarinos.blackout;

import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import losmarinos.blackout.Objetos.Comentario;
import losmarinos.blackout.Objetos.Corte;
import losmarinos.blackout.Objetos.Empresa;
import losmarinos.blackout.Objetos.Reporte;
import losmarinos.blackout.Objetos.Usuario;

import static losmarinos.blackout.Constantes.TAGAPI.REGISTRAR_REPORTE;

/**
 * Created by garci on 25/8/2017.
 */

public class ParserJSON {

    public static boolean esError(String json, StringBuilder mensaje_error)
    {
        try {
            JSONObject obj_resp = new JSONObject(json);

            if(obj_resp.has("error")) {
                mensaje_error.append(obj_resp.getString("message"));
                return true;
            }

            return false;
        } catch (JSONException e) {
        }

        try {
            JSONArray array_resp = new JSONArray(json);

            return false;
        } catch (JSONException e) {
        }

        mensaje_error.append("Error");
        return true;
    }

    public static JSONObject crearJSONUsuario(String nombre, String email, String pass1, String pass2) throws JSONException{
        JSONObject json_usu = new JSONObject();
        json_usu.put("name", nombre);
        json_usu.put("email", email);
        json_usu.put("password", pass1);
        json_usu.put("password_confirmation", pass1);

        return json_usu;
    }

    public static JSONObject crearJSONReporte(int persona_id, Constantes.SERVICIO servicio, int empresa_id, LatLng posicion, int radio) throws JSONException{
        JSONObject json_rep = new JSONObject();
        json_rep.put("persona_id", persona_id);
        json_rep.put("ubicacion", Double.toString(posicion.latitude) + ";" + Double.toString(posicion.longitude));
        json_rep.put("radio", radio);
        json_rep.put("servicio_id", Constantes.getIdServicio(servicio));
        if(empresa_id != -1) {
            json_rep.put("empresa_id", empresa_id);
        }
        return json_rep;
    }

    public static JSONObject crearJSONComentario(int persona_id, int empresa_id, String descripcion) throws JSONException{
        JSONObject json_rep = new JSONObject();
        json_rep.put("persona_id", persona_id);
        json_rep.put("empresa_id", empresa_id);
        json_rep.put("descripcion", descripcion);

        return json_rep;
    }

    public static JSONObject crearJSONPuntoDeInteres(int persona_id, Constantes.SERVICIO servicio, int empresa_id, LatLng posicion, int radio) throws JSONException{
        JSONObject json_pto_interes = new JSONObject();
        json_pto_interes.put("persona_id", persona_id);
        json_pto_interes.put("ubicacion", Double.toString(posicion.latitude) + ";" + Double.toString(posicion.longitude));
        json_pto_interes.put("radio", radio);
        if(empresa_id != -1) {
            json_pto_interes.put("empresa_id", empresa_id);
        }
        return json_pto_interes;
    }

    public static String obtenerAccessToken(String json)
    {
        try {
            JSONObject obj_resp = new JSONObject(json);
            return obj_resp.getString("access_token");
        } catch (JSONException e) {
            return null;
        }
    }

    public static Usuario obtenerUsuario(String json)
    {
        try {
            JSONObject obj_resp = new JSONObject(json);
            int usu_id = Integer.parseInt(obj_resp.getString("id"));
            String usu_nombre = obj_resp.getString("nombre");
            String usu_email = obj_resp.getString("email");
            int persona_id = Integer.parseInt(obj_resp.getString("persona_id"));
            //FALTA QUE TRAIGA EMPRESA_ID
            return new Usuario(persona_id, usu_nombre, "", usu_email, Constantes.TIPOSUSUARIO.PERSONA);
        } catch (JSONException e) {
            return null;
        }
    }

    public static Empresa obtenerEmpresa(String json)
    {
        try {
            JSONObject obj_resp = new JSONObject(json);
            int id = Integer.parseInt(obj_resp.getString("id"));
            String nombre = obj_resp.getString("nombre");
            String password = obj_resp.getString("password");
            String email = obj_resp.getString("email");
            String telefono = obj_resp.getString("telefono");
            String direccion = obj_resp.getString("direccion");
            String website = obj_resp.getString("website");
            return new Empresa(id, nombre, password, email, telefono, direccion, Constantes.SERVICIO.AGUA, website);
        } catch (JSONException e) {
            return null;
        }
    }

    public static List<Empresa> obtenerEmpresas(String json)
    {
        // OJO, lo que se retorna es un array cuyo primer elemento es el array "empresa"
        List<Empresa> empresas_retornar = new ArrayList<>();
        try {
            JSONArray obj_empresas = new JSONArray(json);
            //JSONArray obj_empresas = array_resp.getJSONArray(0);
            for(int i = 0; i < obj_empresas.length(); i++)
            {
                JSONObject obj_empresa = obj_empresas.getJSONObject(i);
                Empresa empresa = ParserJSON.obtenerEmpresa(obj_empresa.toString());

                if(empresa != null){
                    empresas_retornar.add(empresa);
                }
            }
            return empresas_retornar;
        } catch (JSONException e) {
            return null;
        }
    }

    public static Reporte obtenerReporte(String json)
    {
        try {
            JSONObject obj_resp = new JSONObject(json);
            int id = Integer.parseInt(obj_resp.getString("id"));
            int id_persona = Integer.parseInt(obj_resp.getString("persona_id"));
            int id_servicio = Integer.parseInt(obj_resp.getString("servicio_id"));
            int id_empresa = -1;
            if(!obj_resp.isNull("empresa_id")) {
                id_empresa = Integer.parseInt(obj_resp.getString("empresa_id"));
            }
            String ubicacion = obj_resp.getString("ubicacion");
            int radio = Integer.parseInt(obj_resp.getString("radio"));
            int resuelto = Integer.parseInt(obj_resp.getString("resuelto"));
            String fecha = obj_resp.getString("created_at");

            // SI NO ES HH es kk
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = format.parse(fecha);

            return new Reporte(id,
                    Constantes.getServicioById(id_servicio),
                    id_empresa,
                    id_persona,
                    Constantes.stringToLatLng(ubicacion),
                    radio,
                    date,
                    resuelto);

        } catch (JSONException e) {
            return null;
        } catch (ParseException e){
            return null;
        }
    }

    public static List<Reporte> obtenerReportes(String json)
    {
        // OJO, lo que se retorna es un array cuyo primer elemento es el array "empresa"
        List<Reporte> reportes_retornar = new ArrayList<>();
        try {
            JSONArray array_resp = new JSONArray(json);
            JSONArray obj_reportes = array_resp.getJSONArray(1);
            for(int i = 0; i < obj_reportes.length(); i++)
            {
                JSONObject obj_reporte = obj_reportes.getJSONObject(i);
                Reporte reporte = ParserJSON.obtenerReporte(obj_reporte.toString());

                if(reporte != null){
                    reportes_retornar.add(reporte);
                }
            }
            return reportes_retornar;
        } catch (JSONException e) {
            return null;
        }
    }

    public static Corte obtenerCorte(String json)
    {
        try {
            JSONObject obj_resp = new JSONObject(json);
            int id = Integer.parseInt(obj_resp.getString("id"));
            int id_servicio = -1;
            if(!obj_resp.isNull("servicio_id")) { // ESTO LO PONGO PA QUE NO EZPLOTE
                id_servicio = Integer.parseInt(obj_resp.getString("servicio_id"));
            }
            int id_empresa = -1;
            if(!obj_resp.isNull("empresa_id")) {
                id_empresa = Integer.parseInt(obj_resp.getString("empresa_id"));
            }
            String ubicacion = obj_resp.getString("ubicacion");
            int radio = Integer.parseInt(obj_resp.getString("radio"));
            //No traigo cantidad de reportes ya que lo calculo
            //No traigo duración
            int resuelto = Integer.parseInt(obj_resp.getString("resuelto"));
            String fecha_inicio = obj_resp.getString("fecha_inicio");

            // SI NO ES HH es kk
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = format.parse(fecha_inicio);

            return new Corte(id, Constantes.getServicioById(id_servicio), id_empresa, Constantes.stringToLatLng(ubicacion), radio, date, resuelto);

        } catch (JSONException e) {
            return null;
        } catch (ParseException e){
            return null;
        }
    }

    public static List<Corte> obtenerCortes(String json)
    {
        List<Corte> cortes_retornar = new ArrayList<>();
        try {
            JSONObject obj_resp = new JSONObject(json);
            JSONArray obj_cortes = obj_resp.getJSONArray("cortes");
            for(int i = 0; i < obj_cortes.length(); i++)
            {
                JSONObject obj_corte = obj_cortes.getJSONObject(i);
                Corte corte = ParserJSON.obtenerCorte(obj_corte.toString());

                if(corte != null){
                    cortes_retornar.add(corte);
                }
            }
            return cortes_retornar;
        } catch (JSONException e) {
            return null;
        }
    }

    public static Comentario obtenerComentario(String json)
    {
        try {
            JSONObject obj_resp = new JSONObject(json);
            int id = Integer.parseInt(obj_resp.getString("id"));
            int persona_id = Integer.parseInt(obj_resp.getString("persona_id"));
            int empresa_id = Integer.parseInt(obj_resp.getString("empresa_id"));
            String descripcion = obj_resp.getString("descripcion");
            return new Comentario(id, persona_id, empresa_id, descripcion);
        } catch (JSONException e) {
            return null;
        }
    }

    public static List<Comentario> obtenerComentarios(String json)
    {
        // OJO, lo que se retorna es un array cuyo primer elemento es el array "empresa"
        List<Comentario> comentarios_retornar = new ArrayList<>();
        try {
            JSONArray array_com = new JSONArray(json);
            for(int i = 0; i < array_com.length(); i++)
            {
                JSONObject obj_comentario = array_com.getJSONObject(i);
                Comentario comentario = ParserJSON.obtenerComentario(obj_comentario.toString());

                if(comentario != null){
                    comentarios_retornar.add(comentario);
                }
            }
            return comentarios_retornar;
        } catch (JSONException e) {
            return null;
        }
    }

}
