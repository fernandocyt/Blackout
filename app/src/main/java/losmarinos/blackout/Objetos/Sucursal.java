package losmarinos.blackout.Objetos;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by garci on 29/7/2017.
 */

public class Sucursal {
    private int id;
    private int id_empresa;
    private LatLng ubicacion;
    private String telefono;
    private String direccion;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdEmpresa() {
        return id_empresa;
    }

    public void setIdEmpresa(int id_empresa) {
        this.id_empresa = id_empresa;
    }

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

    public Sucursal(int id, int id_empresa, LatLng ubicacion, String telefono, String direccion)
    {
        this.id = id;
        this.id_empresa = id_empresa;
        this.ubicacion = ubicacion;
        this.telefono = telefono;
        this.direccion = direccion;
    }

    public String generarTexto()
    {
        String texto = "Dir.: " + this.direccion + " - Tel.: " + this.telefono;
        return texto;
    }

}
