package losmarinos.blackout;

import com.google.android.gms.maps.model.LatLng;

// Interfaz para la implementacion del patron observer
public interface ObservadorGPS {

    void actualizarPosicionActual(LatLng posicion);
}
