package losmarinos.blackout;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import losmarinos.blackout.Actividades.Login;
import losmarinos.blackout.Objetos.Corte;
import losmarinos.blackout.Objetos.CorteInteres;
import losmarinos.blackout.Objetos.PuntoInteres;
import losmarinos.blackout.Objetos.Reporte;
import losmarinos.blackout.Objetos.Usuario;

import static java.lang.Math.abs;
import static losmarinos.blackout.Constantes.TAGAPI.OBTENER_CORTES;
import static losmarinos.blackout.Constantes.TAGAPI.OBTENER_CORTESINTERES_POR_USUARIO;
import static losmarinos.blackout.Constantes.TAGAPI.OBTENER_PUNTOSINTERES_POR_USUARIO;
import static losmarinos.blackout.Constantes.TAGAPI.OBTENER_REPORTES;
import static losmarinos.blackout.Constantes.TAGAPI.OBTENER_REPORTES_POR_USUARIO;
import static losmarinos.blackout.Constantes.TIEMPO_CHECKEO_SERVICIO;
import static losmarinos.blackout.Constantes.TIEMPO_PEDIR_CONFIRMACION_REPORTES;
import static losmarinos.blackout.Constantes.TIEMPO_RESOLVER_AUTOMATICAMENTE_REPORTES;

/**
 * Created by garci on 15/8/2017.
 */

public class ServicioPeriodico extends Service implements Runnable, ObservadorAPI {

    public Context context = this;
    public Handler handler = null;
    public static Runnable runnable = null;
    public static List<Corte> cortes = null;
    public static List<PuntoInteres> puntos_interes = null;
    public static List<CorteInteres> cortes_interes = null;
    public static List<Reporte> reportes = null;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //Toast.makeText(this, "Service created!", Toast.LENGTH_LONG).show();

        handler = new Handler();
        runnable = this;

        handler.postDelayed(runnable, 15000);
    }


    @Override
    public void run() {

        StringBuilder nombre = new StringBuilder();
        StringBuilder pass = new StringBuilder();
        StringBuilder mail = new StringBuilder();
        StringBuilder token = new StringBuilder();
        int[] id = new int[1];
        boolean ok_usuario = LocalDB.leerArchivoJSONUsuario(this, id, nombre, pass, mail, token);

        // region FORMA ASINCRONICA
        if (ok_usuario) {
            new ConsultorGETAPI("cortes", token.toString(), OBTENER_CORTES, this).execute();
            new ConsultorGETAPI("usuarios/" + String.valueOf(id[0]) + "/puntos-de-interes", token.toString(), OBTENER_PUNTOSINTERES_POR_USUARIO, this).execute();
            new ConsultorGETAPI("usuarios/" + String.valueOf(id[0]) + "/cortes-de-interes", token.toString(), OBTENER_CORTESINTERES_POR_USUARIO, this).execute();
            new ConsultorGETAPI("reporte", token.toString(), OBTENER_REPORTES, this).execute();

            if(cortes != null && puntos_interes != null){
                checkearPuntosInteres(puntos_interes, cortes);
            }
            if(cortes != null && cortes_interes != null){
                checkearCortesInteres(cortes, cortes_interes);
            }
            if(reportes != null){
                checkearReportes(reportes);
            }
        }
        // endregion

        //region FORMA SINCRONICA
        /*if(ok_usuario) {
            List<Corte> cortes = actualizarCortes(token.toString());

            List<PuntoInteres> puntos_interes = actualizarPtosInteres(id[0], token.toString());
            checkearPuntosInteres(puntos_interes, cortes);

            List<Corte> cortes_interes = actualizarCortesInteres(id[0], token.toString(), cortes);
            checkearCortesInteres(cortes_interes);

            List<Reporte> reportes_usuario = actualizarReportes(id[0], token.toString());
            checkearReportes(reportes_usuario);
        }*/
        // endregion

        handler.postDelayed(runnable, TIEMPO_CHECKEO_SERVICIO);
    }

    @Override
    public void obtenerRespuestaAPI(String respuesta, Constantes.TAGAPI tag, boolean correcto)
    {
        try {
            StringBuilder msg_error = new StringBuilder();
            if(ParserJSON.esError(respuesta, msg_error)){
                return;
            }

            switch (tag) {
                case OBTENER_CORTES:
                    cortes = ParserJSON.obtenerCortes(respuesta);
                    break;
                case OBTENER_PUNTOSINTERES_POR_USUARIO:
                    puntos_interes = ParserJSON.obtenerPuntosInteres(respuesta);
                    break;
                case OBTENER_CORTESINTERES_POR_USUARIO:
                    cortes_interes = ParserJSON.obtenerCortesInteres(respuesta);
                    break;
                case OBTENER_REPORTES:
                    reportes = ParserJSON.obtenerReportes(respuesta);
                    break;
            }
        }catch (Exception e){

        }
    }

    public static List<Corte> actualizarCortes(String token){
        List<Corte> cortes = new ArrayList<>();
        try {
            String respuesta = new ConsultorGETAPI("cortes", token, OBTENER_CORTES, null).execute().get();
            StringBuilder msg_error = new StringBuilder();
            if(!ParserJSON.esError(respuesta, msg_error)){
                cortes = ParserJSON.obtenerCortes(respuesta);
            }
        }catch (Exception e){}

        return cortes;
    }

    public static List<PuntoInteres> actualizarPtosInteres(int id_usuario, String token){

        List<PuntoInteres> ptos_interes = new ArrayList<>();
        try{
            String respuesta = new ConsultorGETAPI("usuarios/" + String.valueOf(id_usuario) + "/puntos-de-interes", token, OBTENER_PUNTOSINTERES_POR_USUARIO, null).execute().get();
            StringBuilder msg_error = new StringBuilder();
            if(!ParserJSON.esError(respuesta, msg_error)){
                ptos_interes = ParserJSON.obtenerPuntosInteres(respuesta);
            }
        }catch (Exception e){}

        return ptos_interes;
    }

    public static List<Corte> actualizarCortesInteres(int id_usuario, String token, List<Corte> cortes){
        List<CorteInteres> cortes_interes = new ArrayList<>();
        try {
            String respuesta = new ConsultorGETAPI("usuarios/" + String.valueOf(id_usuario) + "/cortes-de-interes", token, OBTENER_CORTESINTERES_POR_USUARIO, null).execute().get();
            StringBuilder msg_error = new StringBuilder();
            if(!ParserJSON.esError(respuesta, msg_error)){
                cortes_interes = ParserJSON.obtenerCortesInteres(respuesta);
            }
        }catch (Exception e){}

        List<Corte> cortes_retornar = new ArrayList<>();
        for(int i = 0; i < cortes_interes.size(); i++){
            for(int j = 0; j < cortes.size(); j++){
                if (cortes_interes.get(i).getCorteId() == cortes.get(j).getId()){
                    cortes_retornar.add(cortes.get(j));
                }
            }
        }

        return cortes_retornar;
    }

    public static List<Reporte> actualizarReportes(int id_usuario, String token){
        List<Reporte> reportes_usuario = new ArrayList<>();
        try {
            String respuesta = new ConsultorGETAPI("reporte", token, OBTENER_REPORTES, null).execute().get();
            StringBuilder msg_error = new StringBuilder();
            if(!ParserJSON.esError(respuesta, msg_error)){
                reportes_usuario = ParserJSON.obtenerReportes(respuesta);
            }
        }catch (Exception e){}

        for(int i = 0; i < reportes_usuario.size(); i++){
            if(reportes_usuario.get(i).getIdUsuario() != id_usuario){
                reportes_usuario.remove(i);
                i--;
            }
        }

        return reportes_usuario;
    }

    public void checkearPuntosInteres(List<PuntoInteres> puntos_interes, List<Corte> cortes) {
        for (int i = 0; i < puntos_interes.size(); i++) {
            PuntoInteres punto_actual = puntos_interes.get(i);

            for (int j = 0; j < cortes.size(); j++) {
                Corte corte_actual = cortes.get(j);

                if (punto_actual.getServicio() != null && punto_actual.getServicio() != corte_actual.getServicio())
                    continue;

                if (punto_actual.getEmpresa() != null && punto_actual.getEmpresa().getSubId() != corte_actual.getEmpresa().getSubId())
                    continue;

                if(LocalDB.estaEnArchivoJSONCortesAvisados(this, corte_actual.getId(), false))
                    continue;

                if (Calculos.hayInterseccion(
                        punto_actual.getUbicacion(), punto_actual.getRadio(),
                        corte_actual.getUbicacion(), corte_actual.getRadio())) {

                    LocalDB.agregarArchivoJSONCortesAvisados(this.context, corte_actual.getId(), false);

                    this.notificar("Nuevo corte de " + Constantes.servicioToString(corte_actual.getServicio()) + " en tu punto de interes");
                }
            }
        }
    }

    public void checkearCortesInteres(List<Corte> cortes, List<CorteInteres> interes){
        List<Corte> cortes_interes = new ArrayList<>();

        for(int i = 0; i < interes.size(); i++){
            for(int j = 0; j < cortes.size(); j++){
                if (interes.get(i).getCorteId() == cortes.get(j).getId()){
                    cortes_interes.add(cortes.get(j));
                }
            }
        }

        for(int i = 0; i < cortes_interes.size(); i++){
            Corte corte_actual = cortes_interes.get(i);

            if(LocalDB.estaEnArchivoJSONCortesAvisados(this.context, corte_actual.getId(), true))
                continue;

            if(cortes_interes.get(i).isResuelto())
            {
                LocalDB.agregarArchivoJSONCortesAvisados(this.context, corte_actual.getId(), true);

                this.notificar("Tu corte de interes fue resuelto");
            }
        }
    }

    public void checkearReportes(List<Reporte> reportes_usuario){
        for(int i = 0; i < reportes_usuario.size(); i++){
            Reporte reporte_actual = reportes_usuario.get(i);

            if(reporte_actual.isResuelto())
                continue;

            Date fecha_conf = LocalDB.estaEnArchivoJSONReportesConfirmados(this, reporte_actual.getId());

            if(fecha_conf == null){
                LocalDB.agregarArchivoJSONReportesConfirmados(this, reporte_actual.getId(), reporte_actual.getFecha());
            }else{
                Date ahora = Calendar.getInstance().getTime();
                long dif_mins_conf_horas = (ahora.getTime() - fecha_conf.getTime()) / (60 * 1000);

                if(dif_mins_conf_horas > TIEMPO_PEDIR_CONFIRMACION_REPORTES &&
                        dif_mins_conf_horas < TIEMPO_RESOLVER_AUTOMATICAMENTE_REPORTES)
                {
                    String str_servicio = Constantes.servicioToString(reporte_actual.getServicio());
                    this.notificar("Confirma o resuelve tu reporte de " + str_servicio);
                }
                else if (dif_mins_conf_horas > TIEMPO_RESOLVER_AUTOMATICAMENTE_REPORTES)
                {
                    reporte_actual.resolver();

                    String str_servicio = Constantes.servicioToString(reporte_actual.getServicio());
                    this.notificar("Se resolvio el reporte de " + str_servicio);
                }
            }
        }
    }

    public void notificar(String mensaje){
        // Creo la notificacion
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this.context);
        mBuilder.setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("ATENCION")
                .setContentText(mensaje)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});

        // Para que se abra el login cuando clickeo la notificacion
        // TODO: por ahora este codigo abre una actividad nueva, yo quiero que habra la vieja
        /*Intent resultIntent = new Intent(this.context, Login.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(this.context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(contentIntent);*/

        // Activo la notificacion
        Notification notification = mBuilder.build();
        //notification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;

        NotificationManager nm = (NotificationManager) this.context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1, notification);
    }

    @Override
    public void onDestroy() {
        /* IF YOU WANT THIS SERVICE KILLED WITH THE APP THEN UNCOMMENT THE FOLLOWING LINE */
        //handler.removeCallbacks(runnable);
        //Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    // IMPORTANTE
    // Esta funcion se usa para que la task nunca se interrumpa
    // "For this reason if you want to ensure something is always running, you can schedule a periodic restart by means of AlarmManager class."
    @Override
    public void onTaskRemoved(Intent rootIntent){
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);
    }
}
