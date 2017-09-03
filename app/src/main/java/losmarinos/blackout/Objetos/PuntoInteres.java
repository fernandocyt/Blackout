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
    private List<Integer> id_cortes_avisados;


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

    public void addIdCorteAvisado(int id_corte)
    {
        this.id_cortes_avisados.add(id_corte);
    }

    public PuntoInteres(Constantes.SERVICIO servicio, int id_empresa, LatLng ubicacion, double radio)
    {
        this.servicio = servicio;
        this.id_empresa = id_empresa;
        this.ubicacion = ubicacion;
        this.radio = radio;
        this.id_cortes_avisados = new ArrayList<>();
    }

    public Empresa getEmpresa(){
        return Global.encontrarEmpresaPorId(this.id_empresa);
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

        Empresa empresa = Global.encontrarEmpresaPorId(this.id_empresa);
        if(empresa != null) {
            str_empresa = empresa.getNombre() + " - ";
        }
        if(this.servicio != null) {
            str_servicio = Constantes.servicioToString(this.servicio) + " - ";
        }

        String texto = str_servicio + str_empresa + str_ubicacion;

        return texto;
    }
}
