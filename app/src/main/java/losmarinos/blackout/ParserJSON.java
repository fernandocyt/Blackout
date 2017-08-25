package losmarinos.blackout;

import org.json.JSONException;
import org.json.JSONObject;

import losmarinos.blackout.Objetos.Usuario;

/**
 * Created by garci on 25/8/2017.
 */

public class ParserJSON {

    public static boolean esError(String json, String mensaje_error)
    {
        try {
            JSONObject obj_resp = new JSONObject(json);
            return false;
        } catch (JSONException e) {
            mensaje_error = "Error";
            return true;
        }
    }

    public static String obtenerAccessToken(String json)
    {
        try {
            JSONObject obj_resp = new JSONObject(json);
            return obj_resp.getString("access_token");
        } catch (JSONException e) {

        }
        return null;
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

        }

        return null;
    }
}
