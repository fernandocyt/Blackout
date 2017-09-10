package losmarinos.blackout.Objetos;

import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import losmarinos.blackout.Constantes;
import losmarinos.blackout.Global;

/**
 * Created by garci on 15/8/2017.
 */

public class PuntoInteres {
    private Constantes.SERVICIO servicio;
    private int id_empresa;
    private LatLng ubicacion;
    private double radio;

    public Constantes.SERVICIO getServicio() {
        return servicio;
    }

    public void setServicio(Constantes.SERVICIO servicio) {
        this.servicio = servicio;
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

    public double getRadio() {
        return radio;
    }

    public void setRadio(double radio) {
        this.radio = radio;
    }

    public PuntoInteres(Constantes.SERVICIO servicio, int id_empresa, LatLng ubicacion, double radio)
    {
        this.servicio = servicio;
        this.id_empresa = id_empresa;
        this.ubicacion = ubicacion;
        this.radio = radio;
    }

    public Empresa getEmpresa(){
        return Global.encontrarEmpresaPorId(this.id_empresa);
    }

    public String generarTexto()
    {
        //String str_ubicacion = "(" + String.valueOf((int)this.ubicacion.latitude) + "," + String.valueOf((int)this.ubicacion.longitude) +")";
        String texto = "General";

        if(this.servicio != null) {
            texto = Constantes.servicioToString(this.servicio);
        }

        Empresa empresa = Global.encontrarEmpresaPorId(this.id_empresa);
        if(empresa != null) {
            texto = texto + " - " + empresa.getNombre();
        }

        return texto;
    }
}
