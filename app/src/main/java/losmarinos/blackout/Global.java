package losmarinos.blackout;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import losmarinos.blackout.Objetos.Corte;
import losmarinos.blackout.Objetos.Empresa;
import losmarinos.blackout.Objetos.PuntoInteres;
import losmarinos.blackout.Objetos.Reporte;
import losmarinos.blackout.Objetos.Respuesta;
import losmarinos.blackout.Objetos.Sucursal;
import losmarinos.blackout.Objetos.Usuario;

/**
 * Created by garci on 13/8/2017.
 */

public class Global {
    public static Usuario usuario_actual = null;

    public static List<Reporte> reportes = new ArrayList<>();
    public static List<Corte> cortes = new ArrayList<>();
    public static List<Usuario> usuarios = new ArrayList<>();
    public static List<Empresa> empresas = new ArrayList<>();

    public static boolean cargo_datos = false;


    public static void cargarDatosPruebas()
    {
        if(cargo_datos)
            return;

        /*if(usuario_actual != null)
        {
            PuntoInteres punto = new PuntoInteres(Constantes.SERVICIO.LUZ, null, new LatLng(-34.627954, -58.499451), 1000);
            usuario_actual.addPuntoInteres(punto);
        }*/

        Usuario usuario1 = new Usuario("joelkalt", "1234", "joelkaltman@gmail.com", Constantes.TIPOSUSUARIO.PERSONA);
        Usuario usuario2 = new Usuario("fernandocyt", "1234", "fernandocyt@gmail.com", Constantes.TIPOSUSUARIO.PERSONA);
        usuarios.add(usuario1);
        usuarios.add(usuario2);

        Empresa empresa1 = new Empresa("Metrogas", "1234", "a", Constantes.SERVICIO.GAS);
        empresa1.addSucursal(new Sucursal(new LatLng(-34.660718, -58.570862)));
        Empresa empresa2 = new Empresa("Edenor", "1234", "a", Constantes.SERVICIO.LUZ);
        empresa2.addSucursal(new Sucursal(new LatLng(-34.583871, -58.539276)));
        Empresa empresa3 = new Empresa("Edesur", "1234", "a", Constantes.SERVICIO.LUZ);
        Empresa empresa4 = new Empresa("Telecentro", "1234", "a", Constantes.SERVICIO.CABLE);
        Empresa empresa5 = new Empresa("Cablevision", "1234", "a", Constantes.SERVICIO.CABLE);
        Empresa empresa6 = new Empresa("Aysa", "1234", "a", Constantes.SERVICIO.AGUA);
        Empresa empresa7 = new Empresa("Telefonica", "1234", "a", Constantes.SERVICIO.TELEFONO);
        Empresa empresa8 = new Empresa("Fibertel", "1234", "a", Constantes.SERVICIO.INTERNET);

        Global.empresas.add(empresa1);
        Global.empresas.add(empresa2);
        Global.empresas.add(empresa3);
        Global.empresas.add(empresa4);
        Global.empresas.add(empresa5);
        Global.empresas.add(empresa6);
        Global.empresas.add(empresa7);
        Global.empresas.add(empresa8);

        Corte corte_agua = new Corte(Constantes.SERVICIO.AGUA, empresa6, Constantes.BSAS, 500, Calendar.getInstance().getTime(), false);
        Respuesta respuesta1_corte_agua = new Respuesta(usuario1, "Todo mal viejo");
        Respuesta respuesta2_corte_agua = new Respuesta(usuario2, "Sigo esperando son todos putos");
        corte_agua.addRespuesta(respuesta1_corte_agua);
        corte_agua.addRespuesta(respuesta2_corte_agua);
        Corte corte_luz = new Corte(Constantes.SERVICIO.LUZ, empresa2, new LatLng(-34.627954, -58.499451), 1000, Calendar.getInstance().getTime(), false);
        Corte corte_gas = new Corte(Constantes.SERVICIO.GAS, empresa1, new LatLng(-34.565213, -58.482971), 700, Calendar.getInstance().getTime(), false);
        Corte corte_internet = new Corte(Constantes.SERVICIO.INTERNET, empresa8, new LatLng(-34.668060, -58.421173), 1500, Calendar.getInstance().getTime(), false);
        Corte corte_cable = new Corte(Constantes.SERVICIO.CABLE, empresa4, new LatLng(-34.633038, -58.372421), 1000, Calendar.getInstance().getTime(), false);
        Corte corte_telefono = new Corte(Constantes.SERVICIO.TELEFONO, empresa7, new LatLng(-34.595742, -58.420486), 1500, Calendar.getInstance().getTime(), false);

        Global.cortes.add(corte_agua);
        Global.cortes.add(corte_luz);
        Global.cortes.add(corte_gas);
        Global.cortes.add(corte_internet);
        Global.cortes.add(corte_cable);
        Global.cortes.add(corte_telefono);

        cargo_datos = true;
    }

    public static Empresa encontrarEmpresaPorNombre(String nombre)
    {
        for(int i = 0; i < Global.empresas.size(); i++)
        {
            if(Global.empresas.get(i).getNombre().equals(nombre))
            {
                return Global.empresas.get(i);
            }
        }
        return null;
    }

    public static boolean asociarReporteACortes(Reporte reporte)
    {
        for (int k = 0; k < Global.cortes.size(); k++) {
            if(Global.cortes.get(k).asociar(reporte))
            {
                return true;
            }
        }
        return false;
    }

    public static void calcularNuevosCortes()
    {
        List<Corte> cortes = new ArrayList<>();
        List<Reporte> reportes = new ArrayList<>(Global.reportes);

        for(int i = 0; i < reportes.size(); i++){
            List<Reporte> reportes_fundadores = new ArrayList<>();
            Reporte rep_referencia = reportes.get(i);

            if(rep_referencia.isAsociado()) continue;

            // FILTRO: Recorro todos y me fijo con cuantos del mismo servicio se superpone
            for(int j = 0; j < reportes.size(); j++){

                if(i == j) continue;
                if(reportes.get(j).isAsociado()) continue;

                if(Calculos.hayInterseccion(
                        rep_referencia.getUbicacion(), rep_referencia.getRadio(),
                        reportes.get(j).getUbicacion(), reportes.get(j).getRadio())
                        && rep_referencia.getServicio() == reportes.get(j).getServicio())
                {
                    reportes_fundadores.add(reportes.get(j));
                }
            }

            // Si se superpuso con al menos 2 VOY A GENERAR NUEVO CORTE
            if(reportes_fundadores.size() >= 2){
                Corte nuevo_corte = new Corte(rep_referencia);

                for(int j = 0; j < reportes_fundadores.size(); j++){
                    nuevo_corte.asociar(reportes_fundadores.get(j));
                }

                Global.cortes.add(nuevo_corte);
            }
        }
    }
}
