package losmarinos.blackout.Objetos;

import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by garci on 22/7/2017.
 */

public class Reporte {
    private String servicio;
    private String empresa;
    private LatLng ubicacion;
    private double radio;
    private Date fecha;

    public String getServicio() {
        return servicio;
    }

    public void setServicio(String servicio) {
        this.servicio = servicio;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public LatLng getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(LatLng ubicacion) {
        this.ubicacion = ubicacion;
    }

    public double getRadio() {
        return radio;
    }

    public void setRadio(double radio) {
        this.radio = radio;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Reporte(String servicio, String empresa, LatLng ubicacion, double radio)
    {
        this.setServicio(servicio);
        this.setEmpresa(empresa);
        this.setUbicacion(ubicacion);
        this.setRadio(radio);
        Calendar calenadrio = Calendar.getInstance();
        this.setFecha(calenadrio.getTime());
    }
}
