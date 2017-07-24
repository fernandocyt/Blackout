package losmarinos.blackout.Objetos;

/**
 * Created by garci on 24/7/2017.
 */

public class Respuesta {
    private Usuario usuario;
    private String texto;

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Respuesta(Usuario usuario, String texto)
    {
        this.usuario = usuario;
        this.texto = texto;
    }
}
