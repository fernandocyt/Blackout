package losmarinos.blackout.Objetos;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import losmarinos.blackout.Constantes;
import losmarinos.blackout.ConsultorPOSTAPI;
import losmarinos.blackout.Global;
import losmarinos.blackout.LocalDB;
import losmarinos.blackout.ParserJSON;

import static losmarinos.blackout.Constantes.TAGAPI.ACTUALIZAR_REPORTE_RESUELTO;

/**
 * Created by garci on 22/7/2017.
 */

public class Reporte {
    public static int proxima_id_reporte_global = 0;

    private int id;
    private Constantes.SERVICIO servicio;
    private int id_empresa;
    private int id_usuario;
    private LatLng ubicacion;
    private int radio;
    private Date fecha;
    private int resuelto;
    private boolean asociado;

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

    public Reporte(int id, Constantes.SERVICIO servicio, int id_empresa, int id_usuario, LatLng ubicacion, int radio, Date fecha, int resuelto)
    {
        this.id = id;
        this.servicio = servicio;
        this.id_empresa = id_empresa;
        this.id_usuario = id_usuario;
        this.ubicacion = ubicacion;
        this.radio = radio;
        this.fecha = fecha;
        this.resuelto = resuelto;
        this.asociado = false;
    }

    public String generarTexto()
    {
        String texto = "";

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String fecha = df.format(this.fecha);

        Empresa emp = Global.encontrarEmpresaPorId(this.id_empresa);
        if(emp != null) {
            texto = this.servicio + " - " + emp.getNombre() + " - " + fecha;
        }else{
            texto = this.servicio + " - " + fecha;
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

    public void confirmar(Context contexto){
        Date ahora = Calendar.getInstance().getTime();
        LocalDB.agregarArchivoJSONReportesConfirmados(contexto, this.id, ahora);
    }

}
