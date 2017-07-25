package losmarinos.blackout.Objetos;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import losmarinos.blackout.Constantes;

/**
 * Created by garci on 23/7/2017.
 */

public class Corte implements Serializable{
    private Constantes.SERVICIO servicio;
    private String empresa;
    private LatLng ubicacion;
    private double radio;
    private Date fecha_inicio;
    private boolean resuelto;
    private int cantidad_reportes;
    private List<Respuesta> respuestas;

    public Constantes.SERVICIO getServicio() {
        return servicio;
    }

    public void setServicio(Constantes.SERVICIO servicio) {
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

    public Date getFechaInicio() {
        return fecha_inicio;
    }

    public void setFechaInicio(Date fecha_inicio) {
        this.fecha_inicio = fecha_inicio;
    }

    public boolean isResuelto() {
        return resuelto;
    }

    public void setResuelto(boolean resuelto) {
        this.resuelto = resuelto;
    }

    public int getCantidadReportes() {
        return cantidad_reportes;
    }

    public void setCantidadReportes(int cantidad_reportes) {
        this.cantidad_reportes = cantidad_reportes;
    }

    public void addRespuesta(Respuesta respuesta)
    {
        this.respuestas.add(respuesta);
    }

    public List<Respuesta> getRespuestas()
    {
        return this.respuestas;
    }

    public Corte(Constantes.SERVICIO servicio, String empresa, LatLng ubicacion, double radio, Date fecha_inicio, int cantidad_reportes, boolean resuelto)
    {
        this.servicio = servicio;
        this.empresa = empresa;
        this.ubicacion = ubicacion;
        this.radio = radio;
        this.fecha_inicio = fecha_inicio;
        this.cantidad_reportes = cantidad_reportes;
        this.resuelto = resuelto;
        this.respuestas = new ArrayList<>();
    }

}
