package losmarinos.blackout;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by garci on 15/7/2017.
 */

public interface ObservadorGPS {

    void actualizarPosicionActual(LatLng posicion);
}
