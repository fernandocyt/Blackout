package losmarinos.blackout;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import losmarinos.blackout.Objetos.Corte;
import losmarinos.blackout.Objetos.Reporte;

/**
 * Created by garci on 12/8/2017.
 */

public class Calculos {



    public static boolean hayInterseccion(LatLng p1, double r1, LatLng p2, double r2)
    {
        double distancia_centros = Calculos.calcularDistancia(p1, p2);

        double suma_radios = r1 + r2;

        if(distancia_centros < suma_radios)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static double calcularDistancia(LatLng p1, LatLng p2)
    {
        Location loc1 = new Location("loc1");
        loc1.setLatitude(p1.latitude);
        loc1.setLongitude(p1.longitude);

        Location loc2 = new Location("loc2");
        loc2.setLatitude(p2.latitude);
        loc2.setLongitude(p2.longitude);

        return loc1.distanceTo(loc2);
    }
}
