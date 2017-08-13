package losmarinos.blackout.Objetos;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import losmarinos.blackout.Calculos;
import losmarinos.blackout.Constantes;

/**
 * Created by garci on 23/7/2017.
 */

public class Corte{
    public static int proxima_id_corte_global = 0;

    private int id;
    private Constantes.SERVICIO servicio;
    private Empresa empresa;
    private LatLng ubicacion;
    private double radio;
    private Date fecha_inicio;
    private boolean resuelto;
    private List<Respuesta> respuestas;
    private List<Reporte> reportes;

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

    public void addRespuesta(Respuesta respuesta)
    {
        this.respuestas.add(respuesta);
    }

    public List<Respuesta> getRespuestas()
    {
        return this.respuestas;
    }

    public void addReporte(Reporte reporte)
    {
        this.reportes.add(reporte);
    }

    public List<Reporte> getReportes()
    {
        return this.reportes;
    }

    public void setReportes(List<Reporte> reportes) { this.reportes = reportes; }

    public int getId() {
        return id;
    }

    public Corte(Constantes.SERVICIO servicio, Empresa empresa, LatLng ubicacion, double radio, Date fecha_inicio, boolean resuelto)
    {
        this.id = this.proxima_id_corte_global;
        this.proxima_id_corte_global++;

        this.servicio = servicio;
        this.empresa = empresa;
        this.ubicacion = ubicacion;
        this.radio = radio;
        this.fecha_inicio = fecha_inicio;
        this.resuelto = resuelto;
        this.respuestas = new ArrayList<>();
        this.reportes = new ArrayList<>();
    }

    public Corte(Reporte reporte)
    {
        this.id = this.proxima_id_corte_global;
        this.proxima_id_corte_global++;

        this.servicio = reporte.getServicio();
        this.empresa = null;
        this.ubicacion = reporte.getUbicacion();
        this.radio = 0;
        this.fecha_inicio = Calendar.getInstance().getTime();
        this.resuelto = false;
        this.respuestas = new ArrayList<>();
        this.reportes = new ArrayList<>();
        this.reportes.add(reporte);
        reporte.setAsociado(true);
    }

    public boolean contieneReporte(Reporte reporte)
    {
        for(int i = 0; i < this.reportes.size(); i++)
        {
            if(reporte.getId() == this.reportes.get(i).getId())
            {
                return true;
            }
        }
        return false;
    }

    public int cantidadReportes()
    {
        return this.reportes.size();
    }

    public boolean asociar(Reporte reporte)
    {
        if (Calculos.hayInterseccion(
                reporte.getUbicacion(), Constantes.RADIO_SECU,
                this.ubicacion, Constantes.RADIO_SECU)
                && reporte.getServicio() == this.servicio) {

            // Asocio al corte con el reporte
            this.reportes.add(reporte);
            reporte.setAsociado(true);

            // Modifico la posicion del corte
            double desplazarse_lat = (reporte.getUbicacion().latitude - this.ubicacion.latitude) / this.cantidadReportes();
            double desplazarse_long = (reporte.getUbicacion().longitude - this.ubicacion.longitude) / this.cantidadReportes();
            this.ubicacion = new LatLng(this.ubicacion.latitude + desplazarse_lat, this.ubicacion.longitude + desplazarse_long);

            // Modifico el radio del corte
            double distancia_mas_lejana = Calculos.calcularDistancia(reporte.getUbicacion(), this.ubicacion) + reporte.getRadio();
            if(distancia_mas_lejana > this.radio)
            {
                this.radio = distancia_mas_lejana;
            }

            // Modifico empresa responsable
            //this.recalcularEmpresaResponsable();

            return true;
        }
        else
        {
            return false;
        }
    }

    public void recalcularEmpresaResponsable()
    {
        int indice_empresa = -1;
        int mayor_cantidad_apariciones = 0;

        // Primero me fijo cantidad de reportes sin empresa
        for(int i = 0; i < this.reportes.size(); i++)
        {
            if(this.reportes.get(i).getEmpresa() == null)
            {
                mayor_cantidad_apariciones++;
            }
        }

        // Luego busco empresa por empresa
        for(int i = 0; i < this.reportes.size(); i++)
        {
            if(this.reportes.get(i).getEmpresa() != null) {

                int cant_apariciones = 1;
                for (int j = 0; j < this.reportes.size(); j++) {
                    if (this.reportes.get(i).getEmpresa().getId() == this.reportes.get(j).getEmpresa().getId()) {
                        if (i == j) continue;
                        cant_apariciones++;
                    }
                }
                if (cant_apariciones > mayor_cantidad_apariciones) {
                    mayor_cantidad_apariciones = cant_apariciones;
                    indice_empresa = i;
                }
            }
        }

        // Guardo empresa
        if(indice_empresa != -1)
        {
            this.empresa = this.reportes.get(indice_empresa).getEmpresa();
        }
        else
        {
            this.empresa = null;
        }
    }
}
