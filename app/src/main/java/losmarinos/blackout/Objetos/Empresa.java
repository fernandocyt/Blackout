package losmarinos.blackout.Objetos;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import losmarinos.blackout.Constantes;

/**
 * Created by garci on 29/7/2017.
 */

public class Empresa extends Usuario {
    private Constantes.SERVICIO tipo_servicio;
    private List<Sucursal> sucursales;

    public Constantes.SERVICIO getTipoServicio() {
        return tipo_servicio;
    }

    public void setTipoServicio(Constantes.SERVICIO tipo_servicio) {
        this.tipo_servicio = tipo_servicio;
    }

    public List<Sucursal> getSucursales() {
        return sucursales;
    }

    public void addSucursal(Sucursal sucursal)
    {
        this.sucursales.add(sucursal);
    }

    public Empresa(String nombre, String pass, String mail, Constantes.SERVICIO tipo_servicio)
    {
        super(nombre, pass, mail, Constantes.TIPOSUSUARIO.EMPRESA);
        this.tipo_servicio = tipo_servicio;
        this.sucursales = new ArrayList<>();
    }

}
