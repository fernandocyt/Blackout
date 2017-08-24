package losmarinos.blackout;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

/**
 * Created by Fernando on 12/8/2017.
 */

public interface ObservadorAPI {
    void obtenerRespuestaAPI(String respuesta, Constantes.TAGAPI tag, boolean correcto);
}
