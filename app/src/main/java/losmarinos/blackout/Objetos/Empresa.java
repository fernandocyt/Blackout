package losmarinos.blackout.Objetos;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import losmarinos.blackout.Aviso;
import losmarinos.blackout.Constantes;
import losmarinos.blackout.ConsultorGETAPI;
import losmarinos.blackout.Global;
import losmarinos.blackout.ParserJSON;

import static losmarinos.blackout.Constantes.TAGAPI.OBTENER_COMENTARIOS_POR_EMPRESA;
import static losmarinos.blackout.Constantes.TAGAPI.OBTENER_SUCURSALES_POR_EMPRESA;

/**
 * Created by garci on 29/7/2017.
 */

public class Empresa extends Usuario {

    private String telefono;
    private String direccion;
    private Constantes.SERVICIO tipo_servicio;
    private float calificacion;
    private List<Sucursal> sucursales;
    private String pagina;
    private List<Comentario> comentarios;
    private int habilitada;

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Constantes.SERVICIO getTipoServicio() {
        return tipo_servicio;
    }

    public void setTipoServicio(Constantes.SERVICIO tipo_servicio) {
        this.tipo_servicio = tipo_servicio;
    }

    public float getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(float calificacion) {
        this.calificacion = calificacion;
    }

    public List<Sucursal> getSucursales() {
        return sucursales;
    }

    public void setSucursales(List<Sucursal> sucursales) {
        this.sucursales = sucursales;
    }

    public void addSucursal(Sucursal sucursal)
    {
        this.sucursales.add(sucursal);
    }

    public String getPagina() {
        return pagina;
    }

    public void setPagina(String pagina) {
        this.pagina = pagina;
    }

    public void addComentario(Comentario comentario)
    {
        this.comentarios.add(comentario);
    }

    public List<Comentario> getComentarios()
    {
        return this.comentarios;
    }

    public void setHabilitada(int habilitada) {
        this.habilitada = habilitada;
    }

    public boolean isHabilitada() {
        return (habilitada == 1);
    }

    public Empresa(int empresa_id, String nombre, String pass, String mail,String telefono, String direccion, Constantes.SERVICIO tipo_servicio, String pagina, int habilitada)
    {
        super(-1, empresa_id, nombre, pass, mail, Constantes.TIPOSUSUARIO.EMPRESA);
        this.telefono = telefono;
        this.direccion = direccion;
        this.tipo_servicio = tipo_servicio;
        this.calificacion = 0;
        this.sucursales = new ArrayList<>();
        this.pagina = pagina;
        this.comentarios = new ArrayList<>();
        this.habilitada = habilitada;
    }

    public List<Corte> obtenerCortesProgramados(){
        List<Corte> cortes = new ArrayList<>();
        for(int i = 0; i < Global.cortes.size(); i++)
        {
            if(Global.cortes.get(i).getIdEmpresa() == this.getSubId() && Global.cortes.get(i).isProgramado())
            {
                cortes.add(Global.cortes.get(i));
            }
        }
        return cortes;
    }

    public void actualizarSucursales(Activity activity){
        try {
            String respuesta = new ConsultorGETAPI("empresa/"+String.valueOf(this.getSubId())+"/sucursales",
                    Global.token_usuario_actual, OBTENER_SUCURSALES_POR_EMPRESA, null).execute().get();
            StringBuilder msg_error = new StringBuilder();
            if(ParserJSON.esError(respuesta, msg_error)){
                Aviso.showToast(activity, "No es posible cargar sucursales");
                return;
            }else{
                this.sucursales = ParserJSON.obtenerSucursales(respuesta);
            }
        }catch (Exception e){}
    }

    public void actualizarComentarios(Activity activity){
        try {
            String respuesta = new ConsultorGETAPI("empresa/"+String.valueOf(this.getSubId())+"/comentarios",
                    Global.token_usuario_actual, OBTENER_COMENTARIOS_POR_EMPRESA, null).execute().get();
            StringBuilder msg_error = new StringBuilder();
            if(ParserJSON.esError(respuesta, msg_error)){
                Aviso.showToast(activity, "No es posible cargar comentarios");
                return;
            }else{
                this.comentarios = ParserJSON.obtenerComentarios(respuesta);
            }
        }catch (Exception e){}
    }

}
