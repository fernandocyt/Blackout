package losmarinos.blackout.Objetos;

import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import losmarinos.blackout.Constantes;

/**
 * Created by garci on 22/7/2017.
 */

public class Reporte {
    private Constantes.SERVICIO servicio;
    private Empresa empresa;
    private LatLng ubicacion;
    private double radio;
    private Date fecha;
    private boolean resuelto;

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

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public boolean isResuelto() {
        return resuelto;
    }

    public void setResuelto(boolean resuelto) {
        this.resuelto = resuelto;
    }

    public Reporte(Constantes.SERVICIO servicio, Empresa empresa, LatLng ubicacion, double radio)
    {
        this.setServicio(servicio);
        this.setEmpresa(empresa);
        this.setUbicacion(ubicacion);
        this.setRadio(radio);
        Calendar calenadrio = Calendar.getInstance();
        this.setFecha(calenadrio.getTime());
        this.resuelto = false;
    }

    public String generarTextoReporte()
    {
        String texto = "";

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = df.format(this.fecha);

        texto = this.servicio + " - " + this.empresa.getNombre() + " - " + fecha;

        return texto;
    }
}
