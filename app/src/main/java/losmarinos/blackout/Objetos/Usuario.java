package losmarinos.blackout.Objetos;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import losmarinos.blackout.Constantes;
import losmarinos.blackout.ConsultorGETAPI;
import losmarinos.blackout.Global;
import losmarinos.blackout.ParserJSON;

import static losmarinos.blackout.Constantes.TAGAPI.OBTENER_PUNTOSINTERES_POR_USUARIO;

/**
 * Created by garci on 23/7/2017.
 */

public class Usuario {
    public static int proxima_id_usuario_global = 0;

    private int id;
    private String nombre;
    private String pass;
    private String mail;
    private Constantes.TIPOSUSUARIO tipo;
    private List<PuntoInteres> puntos_interes;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public Constantes.TIPOSUSUARIO getTipo() {
        return tipo;
    }

    public void setTipo(Constantes.TIPOSUSUARIO tipo) {
        this.tipo = tipo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<PuntoInteres> getPuntosInteres() {
        return puntos_interes;
    }

    public void setPuntosInteres(List<PuntoInteres> puntos_interes) {
        this.puntos_interes = puntos_interes;
    }


    public void removePuntoInteres(int pos)
    {
        this.puntos_interes.remove(pos);
    }

    public Usuario(int id, String nombre, String pass, String mail, Constantes.TIPOSUSUARIO tipo)
    {
        this.id = id;
        this.nombre = nombre;
        this.pass = pass;
        this.mail = mail;
        this.tipo = tipo;
        this.puntos_interes = new ArrayList<>();
    }


    public List<Reporte> getReportes() {
        return Global.encontrarReportesPorUsuario(this.id);
    }

    public void actualizarPuntosInteres(Context context){
        try {
            String respuesta = new ConsultorGETAPI("personas/" + String.valueOf(this.id) + "/puntos-de-interes", Global.token_usuario_actual, OBTENER_PUNTOSINTERES_POR_USUARIO, null).execute().get();
            StringBuilder msg_error = new StringBuilder();
            if(ParserJSON.esError(respuesta, msg_error)){
                if(context != null) {
                    Toast.makeText(context, "No es posible obtener los puntos de interes", Toast.LENGTH_LONG).show();
                }
                return;
            }else{
                this.puntos_interes = ParserJSON.obtenerPuntosInteres(respuesta);
            }
        }catch (Exception e){}
    }

}
