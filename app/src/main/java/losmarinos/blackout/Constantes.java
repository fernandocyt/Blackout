package losmarinos.blackout;

import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;

// En esta clase van los valores constantes que pueden ser usados en toda la app
public class Constantes {

    public static final LatLng BSAS = new LatLng(-34.6229419,-58.4491101);
    public static final long TIEMPOLOCALIZAR = 1000 * 30 * 1;   //30 segundos
    public static final long DISTANCIALOCALIZAR = 50;      // 50 metros

    public static final int STROKE_COLOR_CIRCLE = Color.TRANSPARENT;
    public static final int COLOR_CIRCLE = 0x220000FF;

    public enum SERVICIOS
    {
        AGUA, ELECTRICIDAD, GAS, INTERNET, TELEFONO
    }

    public enum TIPOSUSUARIO
    {
        PERSONA, EMPRESA, ADMINISTRADOR
    }
}
