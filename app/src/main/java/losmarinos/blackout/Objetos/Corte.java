package losmarinos.blackout.Objetos;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import losmarinos.blackout.Constantes;

/**
 * Created by garci on 23/7/2017.
 */

public class Corte{
    private int id;
    private Constantes.SERVICIO servicio;
    private Empresa empresa;
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

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
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

    public int getId() {
        return id;
    }

    public Corte(int id, Constantes.SERVICIO servicio, Empresa empresa, LatLng ubicacion, double radio, Date fecha_inicio, int cantidad_reportes, boolean resuelto)
    {
        this.id = id;
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
