package losmarinos.blackout.Objetos;

import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import losmarinos.blackout.Constantes;

/**
 * Created by garci on 15/8/2017.
 */

public class PuntoInteres {
    private Constantes.SERVICIO servicio;
    private Empresa empresa;
    private LatLng ubicacion;
    private double radio;
    private boolean activo;
    private List<Integer> id_cortes_avisados;


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

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public void addIdCorteAvisado(int id_corte)
    {
        this.id_cortes_avisados.add(id_corte);
    }

    public PuntoInteres(Constantes.SERVICIO servicio, Empresa empresa, LatLng ubicacion, double radio)
    {
        this.servicio = servicio;
        this.empresa = empresa;
        this.ubicacion = ubicacion;
        this.radio = radio;
        this.activo = true;
        this.id_cortes_avisados = new ArrayList<>();
    }

    public boolean avisoCorte(int id_corte)
    {
        for(int i = 0; i < this.id_cortes_avisados.size(); i++)
        {
            if(this.id_cortes_avisados.get(i) == id_corte)
            {
                return true;
            }
        }
        return false;
    }

    public String generarTexto()
    {
        String str_ubicacion = "(" + String.valueOf((int)this.ubicacion.latitude) + "," + String.valueOf((int)this.ubicacion.longitude) +")";
        String str_empresa = "";
        String str_servicio = "";

        if(this.empresa != null) {
            str_empresa = this.empresa.getNombre() + " - ";
        }
        if(this.servicio != null) {
            str_servicio = Constantes.servicioToString(this.servicio) + " - ";
        }

        String texto = str_servicio + str_empresa + str_ubicacion;

        return texto;
    }
}
