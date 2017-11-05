package losmarinos.blackout.Objetos;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import losmarinos.blackout.Calculos;
import losmarinos.blackout.Constantes;
import losmarinos.blackout.ConsultorGETAPI;
import losmarinos.blackout.ConsultorPOSTAPI;
import losmarinos.blackout.Global;
import losmarinos.blackout.LocalDB;
import losmarinos.blackout.ParserJSON;

import static losmarinos.blackout.Constantes.TAGAPI.ACTUALIZAR_REPORTE_RESUELTO;
import static losmarinos.blackout.Constantes.TAGAPI.ACTUALIZAR_REPORTE_UPDATE_AT;

/**
 * Created by garci on 22/7/2017.
 */

public class Reporte implements Comparable<Reporte>{
    public static int proxima_id_reporte_global = 0;

    private int id;
    private Constantes.SERVICIO servicio;
    private int id_empresa;
    private int id_usuario;
    private LatLng ubicacion;
    private int radio;
    private Date fecha;
    private Date fecha_confirmacion;
    private int resuelto;
    private boolean asociado;

    @Override
    public int compareTo(Reporte r) {
        if (this.fecha.before(r.fecha)) {
            return 1;
        } else if (this.fecha.after(r.fecha)) {
            return -1;
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

    public int getIdUsuario() {
        return id_usuario;
    }

    public void setIdUsuario(int id_usuario) {
        this.id_usuario = id_usuario;
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

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Date getFechaConfirmacion() {
        return fecha_confirmacion;
    }

    public void setFechaConfirmacion(Date fecha_confirmacion) {
        this.fecha_confirmacion = fecha_confirmacion;
    }

    public boolean isResuelto() {
        return (resuelto == 1);
    }

    public void setResuelto(int resuelto) {
        this.resuelto = resuelto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isAsociado() {
        return asociado;
    }

    public void setAsociado(boolean asociado) {
        this.asociado = asociado;
    }

    public Reporte(int id, Constantes.SERVICIO servicio, int id_empresa, int id_usuario, LatLng ubicacion, int radio, Date fecha, Date fecha_confirmacion, int resuelto)
    {
        this.id = id;
        this.servicio = servicio;
        this.id_empresa = id_empresa;
        this.id_usuario = id_usuario;
        this.ubicacion = ubicacion;
        this.radio = radio;
        this.fecha = fecha;
        this.fecha_confirmacion = fecha_confirmacion;
        this.resuelto = resuelto;
        this.asociado = false;
    }

    public String generarTexto()
    {
        String texto = "";

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = df.format(this.fecha);

        if(this.servicio != null) {
            texto = Constantes.servicioToString(this.servicio);
        }

        Empresa emp = Global.encontrarEmpresaPorId(this.id_empresa);
        if(emp != null) {
            texto = texto + " - " + emp.getNombre() + " - " + fecha;
        }else{
            texto = texto + " - " + fecha;
        }

        return texto;
    }

    public String generarSnippet()
    {
        String texto = "Reporte n° " + this.id + " - " + Constantes.servicioToString(this.servicio);


        Empresa emp = Global.encontrarEmpresaPorId(this.id_empresa);
        if(emp != null) {
            texto += " - " + emp.getNombre();
        }
        return texto;
    }

    public Empresa getEmpresa()
    {
        return Global.encontrarEmpresaPorId(this.id_empresa);
    }

    public boolean resolver (){
        try {
            String respuesta = new ConsultorPOSTAPI( "reporte/" + String.valueOf(this.id) + "/resolver", Global.token_usuario_actual, null, ACTUALIZAR_REPORTE_RESUELTO, null).execute().get();
            StringBuilder mensaje_error = new StringBuilder();
            if(ParserJSON.esError(respuesta, mensaje_error)) {
                return false;
            }else{
                this.resuelto = 1;
                return true;
            }
        }catch (Exception e){
            return false;
        }
    }

    public boolean confirmar(){
        try {
            String respuesta = new ConsultorGETAPI( "reporte/" + String.valueOf(this.id) + "/actualizar-updated-at", Global.token_usuario_actual, ACTUALIZAR_REPORTE_UPDATE_AT, null).execute().get();
            StringBuilder mensaje_error = new StringBuilder();
            if(ParserJSON.esError(respuesta, mensaje_error)) {
                return false;
            }else{
                JSONObject reporte = new JSONObject(respuesta);
                String fecha_confirm = reporte.getString("updated_at");

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = format.parse(fecha_confirm);

                this.fecha_confirmacion = date;
                return true;
            }
        }catch (Exception e){
            return false;
        }
    }


}
