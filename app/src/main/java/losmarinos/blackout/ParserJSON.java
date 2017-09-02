package losmarinos.blackout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import losmarinos.blackout.Objetos.Corte;
import losmarinos.blackout.Objetos.Empresa;
import losmarinos.blackout.Objetos.Reporte;
import losmarinos.blackout.Objetos.Usuario;

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
        int usu_id = 0;
        String usu_nombre = "";
        String usu_email = "";
        try {
            JSONObject obj_resp = new JSONObject(json);
            usu_id = Integer.parseInt(obj_resp.getString("id"));
            usu_nombre = obj_resp.getString("nombre");
            usu_email = obj_resp.getString("email");
            return new Usuario(usu_id, usu_nombre, "", usu_email, Constantes.TIPOSUSUARIO.PERSONA);
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
            JSONArray array_resp = new JSONArray(json);
            JSONArray obj_empresas = array_resp.getJSONArray(1);
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
            int id_empresa = Integer.parseInt(obj_resp.getString("empresa_id"));
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
            int id_servicio = 1; //Ver que lo traiga de la base
            int id_empresa = Integer.parseInt(obj_resp.getString("empresa_id"));
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
}
