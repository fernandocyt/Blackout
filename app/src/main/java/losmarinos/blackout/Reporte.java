package losmarinos.blackout;

import com.google.android.gms.maps.model.LatLng;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by garci on 22/7/2017.
 */

public class Reporte {
    String servicio;
    String empresa;
    LatLng ubicacion;
    double radio;
    Date fecha;


    public Reporte(String servicio, String empresa, LatLng ubicacion, double radio)
    {
        this.servicio = servicio;
        this.empresa = empresa;
        this.ubicacion = ubicacion;
        this.radio = radio;
        Calendar calenadrio = Calendar.getInstance();
        this.fecha = calenadrio.getTime();
    }
}
