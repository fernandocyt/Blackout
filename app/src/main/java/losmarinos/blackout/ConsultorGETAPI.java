package losmarinos.blackout;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by garci on 24/8/2017.
 */

public class ConsultorGETAPI extends AsyncTask<Void, Long, String> {

    String link = "";
    String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6ImI0Zjc4ZmRlN2ZiMTkwZTUzNWJjYWVkZDI5YWE5ZWE4M2Y5ZGMxZGYxMjJkOGI3ODYwMDEzMzJmMGNkM2U1MWYwOGMzZDY2NDAzMWE3Zjg1In0.eyJhdWQiOiIxIiwianRpIjoiYjRmNzhmZGU3ZmIxOTBlNTM1YmNhZWRkMjlhYTllYTgzZjlkYzFkZjEyMmQ4Yjc4NjAwMTMzMmYwY2QzZTUxZjA4YzNkNjY0MDMxYTdmODUiLCJpYXQiOjE1MDM1OTMyMzksIm5iZiI6MTUwMzU5MzIzOSwiZXhwIjoxNTM1MTI5MjM5LCJzdWIiOiIzIiwic2NvcGVzIjpbIioiXX0.V-WAjR-7oOFKW8aBVZuUucUxBkXyI9Qa3HobY26Ha6j7HAlAX0XM3kWTnnEbrXeUwcnkRNgEETMJU4xTu2ecYSD4AyMlYI5oe6N22dj_syVdjyFdIBL0XHinmb1yGnXgmk9_f6v5M9QMW-m0b8KuxS2mjG4Eakrd9XjjfXa6-HGe_4p-wB58eulLesImvXgRlfYfmgYyiK1lwI1NKy2d6eNuamzjXIAFU2hPigG5WKglBOkd0nOJvTg_SorA9jqclgy74jn3BGiLH0dtWzdB4I6YTGfDIki0q4QLX1vNZgDwQZDSYN8DWj8zw9ZRlDao68VckQDGh_LBvjN4Y7G2cql6JdgQ6Hs73bpoBbp2bKVTTaYKHoN9vTbwGjBgCi06KmF1UWnCAMtx6rrvgZPSFXzDqk3RRBqZvFxOy9r-N_fBUnr8SPyU0CRi6cBBuXj2VbTc8S-jlMj7GHx3GXivg_kH9VjKDR7Am1l6UXi9IWTaNNqkGNAmwT_ihGGaP9XcHIiuNQS-2DKWtu644mHF5Y5IKATYaxZvc7QUNoa5ReUWNrKklYFq8P2J48xxZUoNhcN4hRSMweIFzwyOqhhKdoO7bNgDyS8_W_IkOUy30YU3DEvzx7LpPdNEq8oVp1s0tydSHYK9zWo4RwjRp39_5qKG3NKW25-VnQmIq8CJFdM";
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
            connection.setRequestProperty("Authorization", "Bearer " + token);
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
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }

            rd.close();

            this.observador.obtenerRespuestaAPI(response.toString(), this.tag, true);

            return response.toString();
        } catch(Exception e){
            this.observador.obtenerRespuestaAPI("", this.tag, false);

            return new String("Exception: " + e.getMessage());
        }
    }

    protected void onPostExecute(String response) {
        JSONObject obj_resp= null;
        try {
            obj_resp = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
