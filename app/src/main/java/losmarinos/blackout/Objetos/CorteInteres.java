package losmarinos.blackout.Objetos;

/**
 * Created by garci on 16/9/2017.
 */

public class CorteInteres {
    private int id;
    private int corte_id;
    private int usuario_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCorteId() {
        return corte_id;
    }

    public void setCorteId(int corte_id) {
        this.corte_id = corte_id;
    }

    public int getUsuarioId() {
        return usuario_id;
    }

    public void setUsuarioId(int usuario_id) {
        this.usuario_id = usuario_id;
    }

    public CorteInteres(int id, int corte_id, int usuario_id)
    {
        this.id = id;
        this.corte_id = corte_id;
        this.usuario_id = usuario_id;
    }
}
