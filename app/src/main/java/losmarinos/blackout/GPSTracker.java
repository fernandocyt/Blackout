package losmarinos.blackout;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.maps.model.LatLng;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

// Clase para obtener la localizacion del GPS en segundo plano
public class GPSTracker extends Service implements LocationListener {

    static GPSTracker instance = null;

    // Flag for GPS status
    boolean isGPSEnabled = false;

    // Flag for network status
    boolean isNetworkEnabled = false;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = Constantes.DISTANCIALOCALIZAR;

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = Constantes.TIEMPOLOCALIZAR;

    // Declaring a Location Manager
    protected LocationManager locationManager;

    public static LatLng ubicacion_actual = null;

    public static List<ObservadorGPS> observadores = new ArrayList<ObservadorGPS>();

    @Override
    public void onCreate() {
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // Getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // Getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // No network provider is enabled
            } else {
                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, null);
                    //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                }
                // If GPS enabled, get latitude/longitude using GPS Services
                if (isGPSEnabled) {
                    //locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean checkPermission(final Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }


    public static void addObserver(ObservadorGPS observador_gps)
    {
        GPSTracker.observadores.add(observador_gps);

        // Si ya tenia una posicion actual calculada se la paso al nuevo observador asi no tiene que esperar
        if(GPSTracker.ubicacion_actual != null)
        {
            observador_gps.actualizarPosicionActual(GPSTracker.ubicacion_actual);
        }
    }

    public static void removeObserver(ObservadorGPS observador_gps)
    {
        GPSTracker.observadores.remove(observador_gps);
    }

    //overrides locationListener
    @Override
    public void onLocationChanged(Location location) {
        ubicacion_actual = new LatLng(location.getLatitude(), location.getLongitude());

        for(int i = 0; i < GPSTracker.observadores.size(); i++) {
            GPSTracker.observadores.get(i).actualizarPosicionActual(ubicacion_actual);
        }

    }

    @Override
    public void onProviderDisabled(String provider) {
    }


    @Override
    public void onProviderEnabled(String provider) {
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    //overrides servicio

    @Override
    public void onDestroy() {
        locationManager.removeUpdates(this);

        List<ObservadorGPS> borrados = new ArrayList<ObservadorGPS>();;
        observadores.removeAll(borrados);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}