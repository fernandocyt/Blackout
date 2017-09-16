package losmarinos.blackout;

import android.graphics.Color;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

// En esta clase van los valores constantes que pueden ser usados en toda la app
public class Constantes {

    // GPS
    public static final LatLng BSAS = new LatLng(-34.6229419,-58.4491101);
    public static final long TIEMPOLOCALIZAR = 1000 * 30 * 1;   //30 segundos
    public static final long DISTANCIALOCALIZAR = 50;      // 50 metros

    // SERVICIO PERIODICO
    public static final int TIEMPO_CHECKEO_SERVICIO = 1000 * 30 * 1; //30 segundos

    // MAPA
    public static final int STROKE_COLOR_CIRCLE = Color.TRANSPARENT;
    public static final int COLOR_CIRCLE = 0x220000FF;

    // API
    public static final String LINK_API = "http://45.79.78.110/api/";

    // CALCULOS
    public static final int RADIO_SECU = 1000;

    // CONSTANTES BASE
    public static final int ID_EMPRESA_NO_ESPECIFICA = 3;

    public enum TAGAPI
    {
        LOGUEAR_USUARIO,
        REGISTRAR_USUARIO,
        REGISTRAR_REPORTE,
        REGISTRAR_PUNTO_DE_INTERES,
        REGISTRAR_CORTE_DE_INTERES,
        REGISTRAR_COMENTARIO,
        REGISTRAR_RESPUESTA,
        REGISTRAR_CORTE_PROGRAMADO,
        OBTENER_USUARIO_POR_TOKEN,
        OBTENER_EMPRESAS,
        OBTENER_REPORTES,
        OBTENER_CORTES,
        OBTENER_USUARIOS,
        OBTENER_RESPUESTAS_POR_CORTE,
        OBTENER_COMENTARIOS_POR_EMPRESA,
        OBTENER_SUCURSALES_POR_EMPRESA,
        OBTENER_PUNTOSINTERES_POR_USUARIO,
        OBTENER_REPORTES_POR_USUARIO,
        OBTENER_CORTESINTERES_POR_USUARIO,
        ACTUALIZAR_REPORTE_RESUELTO,
        BORRAR_PUNTO_DE_INTERES,
        BORRAR_CORTE_DE_INTERES,
        BORRAR_CORTE_PROGRAMADO,

    }

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
        return null;
    }

    public static int getIdServicio(SERVICIO servicio)
    {
        switch (servicio)
        {
            case AGUA: return 1;
            case LUZ: return 2;
            case GAS: return 3;
            case CABLE: return 4;
            case TELEFONO: return 5;
            case INTERNET: return 6;
        }
        return 0;
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
        }else {
            return null;
        }
    }

    public static SERVICIO getServicioById(int id_servicio)
    {
        if(id_servicio == 1){
            return SERVICIO.AGUA;
        }else if(id_servicio == 2){
            return SERVICIO.LUZ;
        }else if(id_servicio == 3){
            return SERVICIO.GAS;
        }else if(id_servicio == 6){
            return SERVICIO.INTERNET;
        }else if(id_servicio == 5){
            return SERVICIO.TELEFONO;
        }else if(id_servicio == 4){
            return SERVICIO.CABLE;
        }
        return null;
    }

    public static LatLng stringToLatLng(String str_latlng)
    {
        String str_lat = str_latlng.substring(0, str_latlng.indexOf(";"));
        String str_lng = str_latlng.substring(str_latlng.indexOf(";") + 1);

        double lat = Double.parseDouble(str_lat);
        double lng = Double.parseDouble(str_lng);

        return new LatLng(lat, lng);
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
