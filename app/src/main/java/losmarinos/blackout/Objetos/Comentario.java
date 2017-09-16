package losmarinos.blackout.Objetos;

import losmarinos.blackout.Global;

/**
 * Created by garci on 24/7/2017.
 */

public class Comentario {
    private int id;
    private int id_empresa;
    private int id_usuario;
    private String texto;

    public int getIdUsuario() {
        return id_usuario;
    }

    public void setIdUsuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdEmpresa() {
        return id_empresa;
    }

    public void setIdEmpresa(int id_empresa) {
        this.id_empresa = id_empresa;
    }

    public Comentario(int id, int id_usuario, int id_empresa, String texto)
    {
        this.id_usuario = id_usuario;
        this.texto = texto;
    }

    public Usuario getUsuario()
    {
        return Global.encontrarUsuarioPorId(this.id_usuario);
    }
}
