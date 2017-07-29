package losmarinos.blackout;

import losmarinos.blackout.Objetos.Corte;
import losmarinos.blackout.Objetos.Empresa;
import losmarinos.blackout.Objetos.Reporte;
import losmarinos.blackout.Objetos.Respuesta;
import losmarinos.blackout.Objetos.Sucursal;
import losmarinos.blackout.Objetos.Usuario;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.httprequest.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Fernando on 11/7/2017.
 */

// Clase para obtener respuestas a las consultas realizadas a la API
public class ConsultorAPI extends AsyncTask<String, Long, String> {

    //region
    public static List<Reporte> reportes = new ArrayList<>();
    public static List<Corte> cortes = new ArrayList<>();
    public static List<Usuario> usuarios = new ArrayList<>();
    public static List<Empresa> empresas = new ArrayList<>();

    public static void cargarDatosPruebas()
    {
        Usuario usuario1 = new Usuario("joelkalt", "1234", "joelkaltman@gmail.com", Constantes.TIPOSUSUARIO.PERSONA);
        Usuario usuario2 = new Usuario("fernandocyt", "1234", "fernandocyt@gmail.com", Constantes.TIPOSUSUARIO.PERSONA);
        usuarios.add(usuario1);
        usuarios.add(usuario2);

        Corte corte_agua = new Corte(1, Constantes.SERVICIO.AGUA, "hola", Constantes.BSAS, 500, Calendar.getInstance().getTime(), 40, false);
        Respuesta respuesta1_corte_agua = new Respuesta(usuario1, "Todo mal viejo");
        Respuesta respuesta2_corte_agua = new Respuesta(usuario2, "Sigo esperando son todos putos");
        corte_agua.addRespuesta(respuesta1_corte_agua);
        corte_agua.addRespuesta(respuesta2_corte_agua);

        Corte corte_luz = new Corte(2, Constantes.SERVICIO.LUZ, "hola", new LatLng(-34.627954, -58.499451), 1000, Calendar.getInstance().getTime(), 35, false);
        Corte corte_gas = new Corte(3, Constantes.SERVICIO.GAS, "hola", new LatLng(-34.565213, -58.482971), 700, Calendar.getInstance().getTime(), 22, false);
        Corte corte_internet = new Corte(4, Constantes.SERVICIO.INTERNET, "hola", new LatLng(-34.668060, -58.421173), 1500, Calendar.getInstance().getTime(), 152, false);
        Corte corte_cable = new Corte(5, Constantes.SERVICIO.CABLE, "hola", new LatLng(-34.633038, -58.372421), 1000, Calendar.getInstance().getTime(), 53, false);
        Corte corte_telefono = new Corte(6, Constantes.SERVICIO.TELEFONO, "hola", new LatLng(-34.595742, -58.420486), 1500, Calendar.getInstance().getTime(), 66, false);


        ConsultorAPI.cortes.add(corte_agua);
        ConsultorAPI.cortes.add(corte_luz);
        ConsultorAPI.cortes.add(corte_gas);
        ConsultorAPI.cortes.add(corte_internet);
        ConsultorAPI.cortes.add(corte_cable);
        ConsultorAPI.cortes.add(corte_telefono);

        Empresa empresa1 = new Empresa("Metrogas", "1234", "a", Constantes.SERVICIO.GAS);
        empresa1.addSucursal(new Sucursal(new LatLng(-34.660718, -58.570862)));
        Empresa empresa2 = new Empresa("Edenor", "1234", "a", Constantes.SERVICIO.LUZ);
        empresa2.addSucursal(new Sucursal(new LatLng(-34.583871, -58.539276)));
        Empresa empresa3 = new Empresa("Edesur", "1234", "a", Constantes.SERVICIO.LUZ);
        Empresa empresa4 = new Empresa("Telecentro", "1234", "a", Constantes.SERVICIO.CABLE);
        Empresa empresa5 = new Empresa("Cablevision", "1234", "a", Constantes.SERVICIO.CABLE);
        Empresa empresa6 = new Empresa("Aysa", "1234", "a", Constantes.SERVICIO.AGUA);
        Empresa empresa7 = new Empresa("Telefonica", "1234", "a", Constantes.SERVICIO.TELEFONO);
        Empresa empresa8 = new Empresa("Fibertel", "1234", "a", Constantes.SERVICIO.INTERNET);

        ConsultorAPI.empresas.add(empresa1);
        ConsultorAPI.empresas.add(empresa2);
        ConsultorAPI.empresas.add(empresa3);
        ConsultorAPI.empresas.add(empresa4);
        ConsultorAPI.empresas.add(empresa5);
        ConsultorAPI.empresas.add(empresa6);
        ConsultorAPI.empresas.add(empresa7);
        ConsultorAPI.empresas.add(empresa8);
    }

    //endregion

    protected String doInBackground(String... urls) {
        /*try {
            return HttpRequest.get(urls[0]).accept("application/json").body();
        } catch (HttpRequest.HttpRequestException exception) {
            return null;
        }*/
        try{
            String username = "joel";
            String password = "1234";

            String link=urls[0];//"http://myphpmysqlweb.hostei.com/loginpost.php";
            String data  = URLEncoder.encode("username", "UTF-8") + "=" +
                    URLEncoder.encode(username, "UTF-8");
            data += "&" + URLEncoder.encode("password", "UTF-8") + "=" +
                    URLEncoder.encode(password, "UTF-8");

            URL url = new URL(link);
            URLConnection conn = url.openConnection();

            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write( data );
            wr.flush();

            BufferedReader reader = new BufferedReader(new
                    InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while((line = reader.readLine()) != null) {
                sb.append(line);
                break;
            }

            return sb.toString();
        } catch(Exception e){
            return new String("Exception: " + e.getMessage());
        }
    }

    protected void onPostExecute(String response) {
        JSONObject obj1= null;
        try {
            obj1 = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Getting String values  inside JSONObject obj1 :

        //String value with key - schema
        //String schema=obj1.getString("schema");
        //String value with key - title

        /*String errorJ= null;
        try {
            errorJ = obj1.getString("Error");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*
        //String value with key - description
        String description=obj1.getString("description");
        //String value with key - type
        String type=obj1.getString("type");

        System.out.println("Schema : "+schema+" Title : "+title+" Description : "+description+" Type : "+type);

        // JSONObject values inside obj1 with key - properties
        JSONObject obj2=obj1.getJSONObject("properties");

        // JSONObject values inside obj2 with key - id
        JSONObject obj3=obj2.getJSONObject("id");
        //String value inside obj3 with key - description
        String description2=obj3.getString("description");
        //String value inside obj3 with key - type
        String type2=obj3.getString("type");

        System.out.println("Description of JSONObject with key- id : "+description2+" Type of JSONObject with key- id : "+type2);

        // JSONObject values inside obj2 with key - name
        JSONObject obj4=obj2.getJSONObject("name");
        //String value inside obj4 with key - description
        String description3=obj4.getString("description");
        //String value inside obj4 with key - type
        String type3=obj4.getString("type");

        System.out.println("Description of JSONObject with key- name : "+description3+" Type of JSONObject with key- name : "+type3);


        //JSONArray value inside obj1 with key - required
        JSONArray array=obj1.getJSONArray("required");
        //String values inside JSONArray array
        for(int i=0;i<array.length();i++){
            System.out.println(array.getString(i));
        }
*/

        //Log.i("PRUEBA", errorJ);
    }
}