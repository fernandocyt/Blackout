package losmarinos.blackout.Objetos;

import java.util.Date;

import losmarinos.blackout.Global;

/**
 * Created by garci on 24/7/2017.
 */

public class Respuesta {
    private int id;
    private int id_corte;
    private int id_usuario;
    private String texto;
    private Date fecha;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_corte() {
        return id_corte;
    }

    public void setId_corte(int id_corte) {
        this.id_corte = id_corte;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Respuesta(int id, int id_usuario, int id_corte, String texto, Date fecha)
    {
        this.id = id;
        this.id_corte = id_corte;
        this.id_usuario = id_usuario;
        this.texto = texto;
        this.fecha = fecha;
    }

    public Usuario getUsuario()
    {
        return Global.encontrarUsuarioPorId(this.id_usuario);
    }
}
