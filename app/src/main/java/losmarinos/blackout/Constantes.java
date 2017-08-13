package losmarinos.blackout;

import android.graphics.Color;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

// En esta clase van los valores constantes que pueden ser usados en toda la app
public class Constantes {

    public static final LatLng BSAS = new LatLng(-34.6229419,-58.4491101);
    public static final long TIEMPOLOCALIZAR = 1000 * 30 * 1;   //30 segundos
    public static final long DISTANCIALOCALIZAR = 50;      // 50 metros

    public static final int STROKE_COLOR_CIRCLE = Color.TRANSPARENT;
    public static final int COLOR_CIRCLE = 0x220000FF;

    public static final String LINK_API = "http://45.79.78.110/api/";

    public static final int RADIO_SECU = 1000;

    public enum SERVICIO
    {
        AGUA,
        LUZ,
        GAS,
        INTERNET,
        TELEFONO,
        CABLE
    }

    public static String servicioToString(SERVICIO servicio)
    {
        switch (servicio)
        {
            case AGUA: return "Agua";
            case LUZ: return "Luz";
            case GAS: return "Gas";
            case INTERNET: return "Internet";
            case TELEFONO: return "Telefono";
            case CABLE: return "Cable";
        }
        return "";
    }

    public static SERVICIO stringToServicio(String servicio)
    {
        if(servicio.equals("Agua")){
            return SERVICIO.AGUA;
        }else if(servicio.equals("Luz")){
            return SERVICIO.LUZ;
        }else if(servicio.equals("Gas")){
            return SERVICIO.GAS;
        }else if(servicio.equals("Internet")){
            return SERVICIO.INTERNET;
        }else if(servicio.equals("Telefono")){
            return SERVICIO.TELEFONO;
        }else if(servicio.equals("Cable")){
            return SERVICIO.CABLE;
        }
        return null;
    }

    public static BitmapDescriptor getIconoCorte(SERVICIO servicio)
    {
        BitmapDescriptor icono;
        switch (servicio)
        {
            case AGUA: default: icono = BitmapDescriptorFactory.fromResource(R.drawable.icono_corte_agua); break;
            case LUZ: icono = BitmapDescriptorFactory.fromResource(R.drawable.icono_corte_luz); break;
            case GAS: icono = BitmapDescriptorFactory.fromResource(R.drawable.icono_corte_gas); break;
            case TELEFONO: icono = BitmapDescriptorFactory.fromResource(R.drawable.icono_corte_telefono); break;
            case INTERNET: icono = BitmapDescriptorFactory.fromResource(R.drawable.icono_corte_internet); break;
            case CABLE: icono = BitmapDescriptorFactory.fromResource(R.drawable.icono_corte_cable); break;
        }
        return icono;
    }

    public static BitmapDescriptor getIconoReporte(SERVICIO servicio)
    {
        BitmapDescriptor icono;
        switch (servicio)
        {
            case AGUA: default: icono = BitmapDescriptorFactory.fromResource(R.drawable.icono_reporte_agua); break;
            case LUZ: icono = BitmapDescriptorFactory.fromResource(R.drawable.icono_reporte_luz); break;
            case GAS: icono = BitmapDescriptorFactory.fromResource(R.drawable.icono_reporte_gas); break;
            case TELEFONO: icono = BitmapDescriptorFactory.fromResource(R.drawable.icono_reporte_telefono); break;
            case INTERNET: icono = BitmapDescriptorFactory.fromResource(R.drawable.icono_reporte_internet); break;
            case CABLE: icono = BitmapDescriptorFactory.fromResource(R.drawable.icono_reporte_cable); break;
        }
        return icono;
    }

    public enum TIPOSUSUARIO
    {
        PERSONA, EMPRESA, ADMINISTRADOR
    }

}
