package losmarinos.blackout.Objetos;

import java.util.ArrayList;
import java.util.List;

import losmarinos.blackout.Constantes;

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
    private List<Reporte> reportes;

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

    public void addPuntoInteres(PuntoInteres punto_interes) {
        this.puntos_interes.add(punto_interes);
    }

    public List<Reporte> getReportes() {
        return reportes;
    }

    public void addReporte(Reporte reporte) {
        this.reportes.add(reporte);
    }


    public void removePuntoInteres(int pos)
    {
        this.puntos_interes.remove(pos);
    }

    public Usuario(String nombre, String pass, String mail, Constantes.TIPOSUSUARIO tipo)
    {
        this.id = this.proxima_id_usuario_global;
        this.proxima_id_usuario_global++;

        this.nombre = nombre;
        this.pass = pass;
        this.mail = mail;
        this.tipo = tipo;
        this.puntos_interes = new ArrayList<>();
        this.reportes = new ArrayList<>();
    }

}
