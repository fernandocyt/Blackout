package losmarinos.blackout;

import android.os.AsyncTask;
import android.util.Log;

import com.httprequest.HttpRequest;

/**
 * Created by Fernando on 11/7/2017.
 */

// Clase para obtener respuestas a las consultas realizadas a la API
public class ConsultorAPI extends AsyncTask<String, Long, String> {

    protected String doInBackground(String... urls) {
        try {
            return HttpRequest.get(urls[0]).accept("application/json").body();
        } catch (HttpRequest.HttpRequestException exception) {
            return null;
        }
    }

    protected void onPostExecute(String response) {
        Log.i("PRUEBA", response);
    }
}