package losmarinos.blackout.Objetos;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.Date;

import losmarinos.blackout.Constantes;

/**
 * Created by garci on 23/7/2017.
 */

public class Corte implements Serializable{
    private String servicio;
    private String empresa;
    private LatLng ubicacion;
    private double radio;
    private Date fecha_inicio;
    private boolean resuelto;
    private int cantidad_reportes;

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

    public Date getFecha_inicio() {
        return fecha_inicio;
    }

    public void setFecha_inicio(Date fecha_inicio) {
        this.fecha_inicio = fecha_inicio;
    }

    public boolean isResuelto() {
        return resuelto;
    }

    public void setResuelto(boolean resuelto) {
        this.resuelto = resuelto;
    }

    public int getCantidad_reportes() {
        return cantidad_reportes;
    }

    public void setCantidad_reportes(int cantidad_reportes) {
        this.cantidad_reportes = cantidad_reportes;
    }

    public Corte(String servicio, String empresa, LatLng ubicacion, double radio, Date fecha_inicio, int cantidad_reportes, boolean resuelto)
    {
        this.servicio = servicio;
        this.empresa = empresa;
        this.ubicacion = ubicacion;
        this.radio = radio;
        this.fecha_inicio = fecha_inicio;
        this.cantidad_reportes = cantidad_reportes;
        this.resuelto = resuelto;
    }
}
