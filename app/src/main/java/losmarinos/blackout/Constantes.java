package losmarinos.blackout;

import com.google.android.gms.maps.model.LatLng;

// En esta clase van los valores constantes que pueden ser usados en toda la app
public class Constantes {

    public static final LatLng BSAS = new LatLng(-34.6229419,-58.4491101);
    public static final long TIEMPOLOCALIZAR = 1000 * 30 * 1;   //30 segundos
    public static final long DISTANCIALOCALIZAR = 50;      // 50 metros

    public enum SERVICIOS
    {
        AGUA, ELECTRICIDAD, GAS, INTERNET, TELEFONO
    }
}
