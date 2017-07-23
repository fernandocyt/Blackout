package losmarinos.blackout.Objetos;

import losmarinos.blackout.Constantes;

/**
 * Created by garci on 23/7/2017.
 */

public class Usuario {
    private String nombre;
    private String pass;
    private String mail;
    private Constantes.TIPOSUSUARIO tipo;

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
}
