package losmarinos.blackout;

import losmarinos.blackout.Objetos.Reporte;

import android.os.AsyncTask;
import android.util.Log;

import com.httprequest.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fernando on 11/7/2017.
 */

// Clase para obtener respuestas a las consultas realizadas a la API
public class ConsultorAPI extends AsyncTask<String, Long, String> {

    public static List<Reporte> reportes = new ArrayList<>();

    protected String doInBackground(String... urls) {
        try {
            return HttpRequest.get(urls[0]).accept("application/json").body();
        } catch (HttpRequest.HttpRequestException exception) {
            return null;
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