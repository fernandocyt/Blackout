package losmarinos.blackout;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import losmarinos.blackout.Actividades.MapaPrincipal;
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

public class Global implements ObservadorAPI {
    public static Usuario usuario_actual = null;
    public static String token_usuario_actual = null;

    public static List<Corte> cortes = new ArrayList<>();
    public static List<Usuario> usuarios = new ArrayList<>();
    public static List<Empresa> empresas = new ArrayList<>();
    public static List<Reporte> reportes = new ArrayList<>();

    public static MapaPrincipal mapa_principal;

    public static void vaciarTodo(){
        Global.cortes.clear();
        Global.usuarios.clear();
        Global.empresas.clear();
        Global.reportes.clear();
    }

    public void obtenerRespuestaAPI(String respuesta, Constantes.TAGAPI tag, boolean correcto)
    {
        try {
            StringBuilder msg_error = new StringBuilder();
            switch(tag){
                case OBTENER_CORTES:
                        if(ParserJSON.esError(respuesta, msg_error)){
                            this.mostrarToastEnMapaPrincipal("Error al obtener cortes");
                            return;
                        }else{
                            Global.cortes = ParserJSON.obtenerCortes(respuesta);
                            this.cargarCortesEnMapaPrincipal();
                        }
                    break;
                case OBTENER_EMPRESAS:
                    if(ParserJSON.esError(respuesta, msg_error)){
                        this.mostrarToastEnMapaPrincipal("Error al obtener empresas");
                        return;
                    }else{
                        Global.empresas = ParserJSON.obtenerEmpresas(respuesta);
                    }
                    break;
                case OBTENER_REPORTES:
                    if(ParserJSON.esError(respuesta, msg_error)){
                        this.mostrarToastEnMapaPrincipal("Error al obtener reportes");
                        return;
                    }else{
                        Global.reportes = ParserJSON.obtenerReportes(respuesta);
                        this.cargarReportesEnMapaPrincipal();
                    }
                    break;
                case OBTENER_USUARIOS:
                    if(ParserJSON.esError(respuesta, msg_error)){
                        this.mostrarToastEnMapaPrincipal("Error al obtener usuarios");
                        return;
                    }else{
                        Global.usuarios = ParserJSON.obtenerUsuarios(respuesta);
                    }
                    break;
                case OBTENER_PUNTOSINTERES_POR_USUARIO:
                    if(ParserJSON.esError(respuesta, msg_error)){
                        this.mostrarToastEnMapaPrincipal("Error al obtener puntos de interes del usuario");
                        return;
                    }else{
                        Global.usuario_actual.setPuntosInteres(ParserJSON.obtenerPuntosInteres(respuesta));
                        //this.cargarPuntosDeInteresEnMapaPrincipal();
                    }
                    break;
                case OBTENER_CORTESINTERES_POR_USUARIO:
                    if(ParserJSON.esError(respuesta, msg_error)){
                        this.mostrarToastEnMapaPrincipal("Error al obtener cortes de interes del usuario");
                        return;
                    }else{
                        Global.usuario_actual.setCortesInteres(ParserJSON.obtenerCortesInteres(respuesta));
                    }
                    break;
            }
        }catch (Exception e){}
    }

    public void mostrarToastEnMapaPrincipal(String mensaje){
        final String mensaje_mostrar = mensaje;
        if(mapa_principal != null) {
            mapa_principal.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mapa_principal, mensaje_mostrar, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void cargarReportesEnMapaPrincipal(){
        if(mapa_principal != null) {
            mapa_principal.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mapa_principal.cargarReportesEnMapa();
                }
            });
        }
    }

    public void cargarCortesEnMapaPrincipal(){
        if(mapa_principal != null) {
            mapa_principal.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mapa_principal.cargarCortesEnMapa();
                }
            });
        }
    }

    public void cargarPuntosDeInteresEnMapaPrincipal(){
        if(mapa_principal != null) {
            mapa_principal.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mapa_principal.cargarPuntosInteresEnMapa();
                }
            });
        }
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
            if(Global.empresas.get(i).getSubId() == id_empresa)
            {
                return Global.empresas.get(i);
            }
        }
        return null;
    }

    public static Corte encontrarCortePorId(int id_corte)
    {
        for(int i = 0; i < Global.cortes.size(); i++)
        {
            if(Global.cortes.get(i).getId() == id_corte)
            {
                return Global.cortes.get(i);
            }
        }
        return null;
    }

    public static Usuario encontrarUsuarioPorId(int id_usuario)
    {
        for(int i = 0; i < Global.usuarios.size(); i++)
        {
            if(Global.usuarios.get(i).getIdUsuario() == id_usuario)
            {
                return Global.usuarios.get(i);
            }
        }
        if(Global.usuario_actual.getIdUsuario() == id_usuario)
        {
            return usuario_actual;
        }
        return null;
    }

    public static List<Reporte> encontrarReportesPorUsuario(int id_usuario)
    {
        List<Reporte> reportes_retornar = new ArrayList<>();
        for(int i = 0; i < Global.reportes.size(); i++)
        {
            if(Global.reportes.get(i).getIdPersona() == id_usuario)
            {
                reportes_retornar.add(reportes.get(i));
            }
        }
        return reportes_retornar;
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
