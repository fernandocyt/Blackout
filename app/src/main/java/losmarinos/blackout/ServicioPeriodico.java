package losmarinos.blackout;

import android.app.AlarmManager;
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

                //if (punto_actual.isActivo()) {

                    for (int j = 0; j < Global.cortes.size(); j++) {
                        Corte corte_actual = Global.cortes.get(j);

                        if (punto_actual.avisoCorte(corte_actual.getId()))
                            continue;

                        if (punto_actual.getServicio() != null && punto_actual.getServicio() != corte_actual.getServicio())
                            continue;

                        if (punto_actual.getEmpresa() != null && punto_actual.getEmpresa().getId() != corte_actual.getEmpresa().getId())
                            continue;

                        if (Calculos.hayInterseccion(
                                punto_actual.getUbicacion(), punto_actual.getRadio(),
                                corte_actual.getUbicacion(), corte_actual.getRadio())) {

                            punto_actual.addIdCorteAvisado(corte_actual.getId());

                            NotificationCompat.Builder b = new NotificationCompat.Builder(this.context);
                            b.setAutoCancel(true)
                                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                                    .setWhen(System.currentTimeMillis())
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle("ATENCION")
                                    .setContentText("Nuevo corte de " + Constantes.servicioToString(corte_actual.getServicio()) + " en tu punto de interes")
                                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});

                            NotificationManager nm = (NotificationManager) this.context.getSystemService(Context.NOTIFICATION_SERVICE);
                            nm.notify(1, b.build());
                        }
                    }
                //}
            }
        }


        handler.postDelayed(runnable, TIEMPO_CHECKEO_SERVICIO);
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
