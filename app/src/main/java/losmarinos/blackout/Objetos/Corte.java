package losmarinos.blackout.Objetos;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import losmarinos.blackout.Aviso;
import losmarinos.blackout.Calculos;
import losmarinos.blackout.Constantes;
import losmarinos.blackout.ConsultorGETAPI;
import losmarinos.blackout.Global;
import losmarinos.blackout.ParserJSON;

import static losmarinos.blackout.Constantes.TAGAPI.OBTENER_RESPUESTAS_POR_CORTE;

/**
 * Created by garci on 23/7/2017.
 */

public class Corte implements Comparable<Corte>{
    public static int proxima_id_corte_global = 0;

    private int id;
    private Constantes.SERVICIO servicio;
    private int id_empresa;
    private LatLng ubicacion;
    private int radio;
    private int cantidad_reportes;
    private Date fecha_inicio;
    private Date fecha_fin;
    private int resuelto;
    private int programado;
    private List<Respuesta> respuestas;
    private List<Reporte> reportes;


    public int compareTo(Corte c) {
        if (this.resuelto > c.resuelto) {
            return 1;
        } else if (this.resuelto < c.resuelto) {
            return -1;
        }else if(this.resuelto == c.resuelto){
            if(this.fecha_inicio.before(c.fecha_inicio)){
                return 1;
            }else{
                return -1;
            }
        }
        return 0;
    }

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

    public void setRadio(int radio) {
        this.radio = radio;
    }

    public Date getFechaInicio() {
        return fecha_inicio;
    }

    public void setFechaInicio(Date fecha_inicio) {
        this.fecha_inicio = fecha_inicio;
    }

    public Date getFechaFin() {
        return fecha_fin;
    }

    public void setFechaFin(Date fecha_fin) {
        this.fecha_fin = fecha_fin;
    }

    public int getCantidadReportes() {
        return cantidad_reportes;
    }

    public void setCantidadReportes(int cantidad_reportes) {
        this.cantidad_reportes = cantidad_reportes;
    }

    public boolean isResuelto() {
        return (resuelto == 1);
    }

    public void setResuelto(int resuelto) {
        this.resuelto = resuelto;
    }

    public boolean isProgramado() {
        return (programado == 1);
    }

    public void setProgramado(int programado) {
        this.programado = programado;
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

    public Corte(int id, Constantes.SERVICIO servicio, int id_empresa, LatLng ubicacion, int radio, int cantidad_reportes, Date fecha_inicio, Date fecha_fin, int resuelto, int programado)
    {
        this.id = id;
        this.servicio = servicio;
        this.id_empresa = id_empresa;
        this.ubicacion = ubicacion;
        this.radio = radio;
        this.cantidad_reportes = cantidad_reportes;
        this.fecha_inicio = fecha_inicio;
        this.fecha_fin = fecha_fin;
        this.resuelto = resuelto;
        this.programado = programado;
        this.respuestas = new ArrayList<>();
        this.reportes = new ArrayList<>();
    }

    public Empresa getEmpresa()
    {
        return Global.encontrarEmpresaPorId(this.id_empresa);
    }

    public void actualizarRespuestas(Activity activity){
        try {
            String respuesta = new ConsultorGETAPI("cortes/"+String.valueOf(this.id)+"/respuestas",
                    Global.token_usuario_actual, OBTENER_RESPUESTAS_POR_CORTE, null).execute().get();
            StringBuilder msg_error = new StringBuilder();
            if(ParserJSON.esError(respuesta, msg_error)){
                Aviso.showToast(activity, "No es posible cargar respuestas");
                return;
            }else{
                this.respuestas = ParserJSON.obtenerRespuestas(respuesta);
            }
        }catch (Exception e){}
    }

    public Corte(Reporte reporte)
    {
        this.id = 0;
        this.servicio = reporte.getServicio();
        this.id_empresa = -1;
        this.ubicacion = reporte.getUbicacion();
        this.radio = 0;
        this.cantidad_reportes = 0;
        this.fecha_inicio = Calendar.getInstance().getTime();
        this.fecha_fin = null;
        this.resuelto = 0;
        this.programado = 0;
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
            double desplazarse_lat = (reporte.getUbicacion().latitude - this.ubicacion.latitude) / this.getCantidadReportes();
            double desplazarse_long = (reporte.getUbicacion().longitude - this.ubicacion.longitude) / this.getCantidadReportes();
            this.ubicacion = new LatLng(this.ubicacion.latitude + desplazarse_lat, this.ubicacion.longitude + desplazarse_long);

            // Modifico el radio del corte
            double distancia_mas_lejana = Calculos.calcularDistancia(reporte.getUbicacion(), this.ubicacion) + reporte.getRadio();
            if(distancia_mas_lejana > this.radio)
            {
                this.radio = (int)distancia_mas_lejana;
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
                    if (this.reportes.get(i).getEmpresa().getSubId() == this.reportes.get(j).getEmpresa().getSubId()) {
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
            this.id_empresa = this.reportes.get(indice_empresa).getEmpresa().getSubId();
        }
        else
        {
            this.id_empresa = -1;
        }

    }

    public String generarTexto()
    {
        String str_fecha_inicio = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(this.fecha_inicio);
        String str_fecha_fin = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(this.fecha_fin);

        String texto = "Inicio: " + str_fecha_inicio + "\nFin: " + str_fecha_fin;
        return texto;
    }

    public String generarSnippet()
    {
        String texto = "Corte n°: " + this.id + Constantes.servicioToString(this.servicio) + this.getEmpresa().getNombre();
        return texto;
    }
}
