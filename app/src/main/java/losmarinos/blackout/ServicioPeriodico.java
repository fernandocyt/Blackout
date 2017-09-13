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

import java.util.List;

import losmarinos.blackout.Actividades.Login;
import losmarinos.blackout.Objetos.Corte;
import losmarinos.blackout.Objetos.PuntoInteres;
import losmarinos.blackout.Objetos.Usuario;

import static losmarinos.blackout.Constantes.TIEMPO_CHECKEO_SERVICIO;

/**
 * Created by garci on 15/8/2017.
 */

public class ServicioPeriodico extends Service implements Runnable {

    public Context context = this;
    public Handler handler = null;
    public static Runnable runnable = null;

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
        if(Global.usuario_actual != null) {
            List<PuntoInteres> puntos_interes = Global.usuario_actual.getPuntosInteres();

            for (int i = 0; i < puntos_interes.size(); i++) {
                PuntoInteres punto_actual = puntos_interes.get(i);

                for (int j = 0; j < Global.cortes.size(); j++) {
                    Corte corte_actual = Global.cortes.get(j);

                    if (punto_actual.getServicio() != null && punto_actual.getServicio() != corte_actual.getServicio())
                        continue;

                    if (punto_actual.getEmpresa() != null && punto_actual.getEmpresa().getId() != corte_actual.getEmpresa().getId())
                        continue;

                    if(LocalDB.estaEnArchivoJSONCortesAvisados(this.context, corte_actual.getId(), false))
                        continue;

                    if (Calculos.hayInterseccion(
                            punto_actual.getUbicacion(), punto_actual.getRadio(),
                            corte_actual.getUbicacion(), corte_actual.getRadio())) {

                        LocalDB.agregarArchivoJSONCortesAvisados(this.context, corte_actual.getId(), false);

                        this.notificar("Nuevo corte de " + Constantes.servicioToString(corte_actual.getServicio()) + " en tu punto de interes");
                    }
                }
            }

            List<Corte> cortes_interes = Global.usuario_actual.getCortesInteres();

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


        handler.postDelayed(runnable, TIEMPO_CHECKEO_SERVICIO);
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
