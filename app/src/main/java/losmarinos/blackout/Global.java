package losmarinos.blackout;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.text.ParseException;
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

import static losmarinos.blackout.Constantes.TAGAPI.OBTENER_CORTES;
import static losmarinos.blackout.Constantes.TAGAPI.OBTENER_EMPRESAS;
import static losmarinos.blackout.Constantes.TAGAPI.OBTENER_REPORTES;
import static losmarinos.blackout.Constantes.TAGAPI.OBTENER_USUARIOS;

/**
 * Created by garci on 13/8/2017.
 */

public class Global {
    public static Usuario usuario_actual = null;
    public static String token_usuario_actual = null;

    public static List<Corte> cortes = new ArrayList<>();
    public static List<Usuario> usuarios = new ArrayList<>();
    public static List<Empresa> empresas = new ArrayList<>();
    public static List<Reporte> reportes = new ArrayList<>();

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

        /*Usuario usuario1 = new Usuario(55,"joelkalt", "1234", "joelkaltman@gmail.com", Constantes.TIPOSUSUARIO.PERSONA);
        Usuario usuario2 = new Usuario(54,"fernandocyt", "1234", "fernandocyt@gmail.com", Constantes.TIPOSUSUARIO.PERSONA);
        usuarios.add(usuario1);
        usuarios.add(usuario2);*/

        //Empresa empresa1 = new Empresa("Metrogas", "1234", "a", Constantes.SERVICIO.GAS);
        /*Empresa empresa1 = new Empresa(4, "Metrogas", "1234", "a", "1532323287", "Loyola 20", Constantes.SERVICIO.GAS, "www.metrogas.com.ar");
        empresa1.addSucursal(new Sucursal(new LatLng(-34.660718, -58.570862), "4983-9271", "Av. Medrano 900"));
        Empresa empresa2 = new Empresa(5, "Edenor", "1234", "a", "1532323288", "Loyola 21", Constantes.SERVICIO.LUZ, "www.edenor.com.ar");
        empresa2.addSucursal(new Sucursal(new LatLng(-34.583871, -58.539276), "48547559", "Corrientes 5225"));
        Empresa empresa3 = new Empresa(6, "Edesur", "1234", "a", "1532323289", "Loyola 22", Constantes.SERVICIO.LUZ, "www.edesur.com.ar");
        Empresa empresa4 = new Empresa(7, "Telecentro", "1234", "a", "1532323290", "Loyola 23", Constantes.SERVICIO.CABLE, "www.telecentro.com.ar");
        Empresa empresa5 = new Empresa(8,"Cablevision", "1234", "a", "1532323291", "Loyola 24", Constantes.SERVICIO.CABLE, "www.cablevisionfibertel.com.ar");
        Empresa empresa6 = new Empresa(9,"Aysa", "1234", "a", "1532323292", "Loyola 25", Constantes.SERVICIO.AGUA, "www.aysa.com.ar");
        Empresa empresa7 = new Empresa(10,"Telefonica", "1234", "a", "1532323293", "Loyola 26", Constantes.SERVICIO.TELEFONO, "www.telefonica.com.ar");
        Empresa empresa8 = new Empresa(11,"Fibertel", "1234", "a", "1532323294", "Loyola 27", Constantes.SERVICIO.INTERNET, "www.cablevisionfibertel.com.ar");

        Global.empresas.add(empresa1);
        Global.empresas.add(empresa2);
        Global.empresas.add(empresa3);
        Global.empresas.add(empresa4);
        Global.empresas.add(empresa5);
        Global.empresas.add(empresa6);
        Global.empresas.add(empresa7);
        Global.empresas.add(empresa8);*/

        /*Corte corte_agua = new Corte(Constantes.SERVICIO.AGUA, empresa6, Constantes.BSAS, 500, Calendar.getInstance().getTime(), false);
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
        Global.cortes.add(corte_telefono);*/

        cargo_datos = true;
    }

    public static void actualizarEmpresas(Context context)
    {
        try {
            String respuesta = new ConsultorGETAPI("empresa", Global.token_usuario_actual, OBTENER_EMPRESAS, null).execute().get();
            StringBuilder msg_error = new StringBuilder();
            if(ParserJSON.esError(respuesta, msg_error)){
                if(context != null) {
                    Toast.makeText(context, "No es posible actualizar empresas", Toast.LENGTH_LONG).show();
                }
                return;
            }else{
                Global.empresas = ParserJSON.obtenerEmpresas(respuesta);
            }
        }catch (Exception e){}
    }

    public static void actualizarReportes(Context context)
    {
        try {
            String respuesta = new ConsultorGETAPI("reporte", Global.token_usuario_actual, OBTENER_REPORTES, null).execute().get();
            StringBuilder msg_error = new StringBuilder();
            if(ParserJSON.esError(respuesta, msg_error)){
                if(context != null) {
                    Toast.makeText(context, "No es posible actualizar reportes", Toast.LENGTH_LONG).show();
                }
                return;
            }else{
                Global.reportes = ParserJSON.obtenerReportes(respuesta);
            }
        }catch (Exception e){}
    }

    public static void actualizarCortes(Context context)
    {
        try {
            String respuesta = new ConsultorGETAPI("cortes", Global.token_usuario_actual, OBTENER_CORTES, null).execute().get();
            StringBuilder msg_error = new StringBuilder();
            if(ParserJSON.esError(respuesta, msg_error)){
                if(context != null) {
                    Toast.makeText(context, "No es posible actualizar cortes", Toast.LENGTH_LONG).show();
                }
                return;
            }else{
                Global.cortes = ParserJSON.obtenerCortes(respuesta);
            }
        }catch (Exception e){}
    }

    public static void actualizarUsuarios(Context context)
    {
        try {
            String respuesta = new ConsultorGETAPI("usuarios", Global.token_usuario_actual, OBTENER_USUARIOS, null).execute().get();
            StringBuilder msg_error = new StringBuilder();
            if(ParserJSON.esError(respuesta, msg_error)){
                if(context != null) {
                    Toast.makeText(context, "No es posible obtener usuarios", Toast.LENGTH_LONG).show();
                }
                return;
            }else{
                Global.usuarios = ParserJSON.obtenerUsuarios(respuesta);
                int a = 0;
            }
        }catch (Exception e){}
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

    public static Empresa encontrarEmpresaPorId(int id_empresa)
    {
        for(int i = 0; i < Global.empresas.size(); i++)
        {
            if(Global.empresas.get(i).getId() == id_empresa)
            {
                return Global.empresas.get(i);
            }
        }
        return null;
    }

    public static Usuario encontrarUsuarioPorId(int id_usuario)
    {
        for(int i = 0; i < Global.usuarios.size(); i++)
        {
            if(Global.usuarios.get(i).getId() == id_usuario)
            {
                return Global.usuarios.get(i);
            }
        }
        if(Global.usuario_actual.getId() == id_usuario)
        {
            return usuario_actual;
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
        List<Reporte> reportes = new ArrayList<>(Global.usuario_actual.getReportes());

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
