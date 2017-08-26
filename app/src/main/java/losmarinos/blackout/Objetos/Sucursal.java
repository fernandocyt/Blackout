package losmarinos.blackout.Objetos;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by garci on 29/7/2017.
 */

public class Sucursal {
    private LatLng ubicacion;
    private String telefono;
    private String direccion;



    public LatLng getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(LatLng ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Sucursal(LatLng ubicacion, String telefono, String direccion)
    {
        this.ubicacion = ubicacion;
        this.telefono = telefono;
        this.direccion = direccion;
    }

    public String generarTexto()
    {
        String texto = this.direccion + " (" + this.telefono + ")";
        return texto;
    }
}
