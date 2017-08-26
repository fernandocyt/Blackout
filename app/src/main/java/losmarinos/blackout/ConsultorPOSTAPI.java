package losmarinos.blackout;

import losmarinos.blackout.Objetos.Corte;
import losmarinos.blackout.Objetos.Empresa;
import losmarinos.blackout.Objetos.Reporte;
import losmarinos.blackout.Objetos.Respuesta;
import losmarinos.blackout.Objetos.Sucursal;
import losmarinos.blackout.Objetos.Usuario;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.httprequest.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Fernando on 11/7/2017.
 */

// Clase para obtener respuestas a las consultas realizadas a la API
public class ConsultorPOSTAPI extends AsyncTask<Void, Long, String> {

    JSONObject obj = null;
    String link = null;
    String token = null;
    ObservadorAPI observador = null;
    Constantes.TAGAPI tag = null;

    public ConsultorPOSTAPI(String link, String token, JSONObject obj, Constantes.TAGAPI tag, ObservadorAPI observador)
    {
        super();
        this.link = link;
        this.token = token;
        this.obj = obj;
        this.observador = observador;
        this.tag = tag;
    }

    protected String doInBackground(Void... params) {
        try{

            URL url = new URL(Constantes.LINK_API + link);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            if(token != null){
                connection.setRequestProperty("Authorization",  "Bearer " + token);
            }

            connection.setUseCaches (false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setConnectTimeout(30000);

            //Send request
            DataOutputStream wr = new DataOutputStream (connection.getOutputStream ());
            wr.write(obj.toString().getBytes("UTF-8"));
            wr.flush ();
            wr.close ();

            //Get Response
            InputStream is = null;
            try {
                is = connection.getInputStream();
            }catch (IOException exception){
                is = connection.getErrorStream();
            }
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }

            rd.close();

            if(this.observador != null) {
                this.observador.obtenerRespuestaAPI(response.toString(), this.tag, true);
            }

            return response.toString();
        } catch(Exception e){

            if(this.observador != null) {
                this.observador.obtenerRespuestaAPI("", this.tag, false);
            }

            return new String(e.getMessage());
        }
    }

    protected void onPostExecute(String response) {
        JSONObject obj_resp= null;
        try {
            obj_resp = new JSONObject(response);
            //this.observador.obtenerRespuestaAPI(obj_resp, true);
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