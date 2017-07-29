package losmarinos.blackout.Objetos;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by garci on 29/7/2017.
 */

public class Sucursal {
    private LatLng ubicacion;


    public LatLng getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(LatLng ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Sucursal(LatLng ubicacion)
    {
        this.ubicacion = ubicacion;
    }
}
