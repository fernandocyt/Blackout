package losmarinos.blackout;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by garci on 24/8/2017.
 */

public class ConsultorGETAPI extends AsyncTask<Void, Long, String> {

    String link = null;
    String token = null;
    ObservadorAPI observador = null;
    Constantes.TAGAPI tag = null;

    public ConsultorGETAPI(String link, String token, Constantes.TAGAPI tag, ObservadorAPI observador)
    {
        this.link = link;
        this.token = token;
        this.observador = observador;
        this.tag = tag;
    }

    protected String doInBackground(Void... params) {
        try{
            URL url = new URL(Constantes.LINK_API + link);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            if(token != null) {
                connection.setRequestProperty("Authorization", "Bearer " + token);
            }
            connection.setRequestMethod("GET");

            //int httpStatus = connection.getResponseCode();

            //connection.setUseCaches (false);
            //connection.setDoInput(true);
            //connection.setDoOutput(true);
            connection.setConnectTimeout(30000);

            //Send request
            //DataOutputStream wr = new DataOutputStream (connection.getOutputStream ());
            //wr.flush ();
            //wr.close ();

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
    }
}
