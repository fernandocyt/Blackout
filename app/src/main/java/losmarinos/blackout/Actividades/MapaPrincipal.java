package losmarinos.blackout.Actividades;

import losmarinos.blackout.Objetos.Corte;
import losmarinos.blackout.Objetos.Empresa;
import losmarinos.blackout.Objetos.Sucursal;
import losmarinos.blackout.R;
import losmarinos.blackout.Constantes;
import losmarinos.blackout.ObservadorGPS;
import losmarinos.blackout.GPSTracker;
import losmarinos.blackout.ConsultorAPI;
import losmarinos.blackout.Objetos.Reporte;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Marker;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

// Actividad del mapa principal
public class MapaPrincipal extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
                                                                OnMapReadyCallback,
                                                                GoogleMap.OnMarkerClickListener,
                                                                ObservadorGPS{
    GoogleMap mMap;
    Marker marcador_posicion_actual = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_principal);

        ConsultorAPI.cargarDatosPruebas();

        startService(new Intent(this, GPSTracker.class));
        GPSTracker.addObserver(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        this.buscarPelicula();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.crear_reporte) {
            Intent i = new Intent(getApplicationContext(), CrearReporte.class);
            startActivity(i);
        } else if (id == R.id.mis_reportes) {
            Intent i = new Intent(getApplicationContext(), MisReportes.class);
            startActivity(i);
        } else if (id == R.id.accion_filtrar){
            Intent i = new Intent(getApplicationContext(), FiltrarMapaPrincipal.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Constantes.BSAS, 11.0f));
        mMap.setOnMarkerClickListener(this);

        this.actualizarMapaPrincipal(null);
    }

    @Override
    public void actualizarPosicionActual(LatLng posicion) {
        if(marcador_posicion_actual == null) {
            marcador_posicion_actual = mMap.addMarker(new MarkerOptions()
                    .position(posicion)
                    .title("Usuario")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icono_ubicacion_usuario))
                    .zIndex(1));
        } else {
            marcador_posicion_actual.setPosition(posicion);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if(marker.getTitle().equals("Corte"))
        {
            Corte corte_seleccionado = (Corte)marker.getTag();

            Intent i = new Intent(getApplicationContext(), PerfilCorte.class);
            i.putExtra("idCorte", corte_seleccionado.getId());
            try {
                startActivity(i);
            }
            catch (Exception e)
            {
                throw e;
            }
        }
        return true;
    }

    public void centrarMapaPrincipal(View view)
    {
        if(marcador_posicion_actual != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(marcador_posicion_actual.getPosition().latitude,
                            marcador_posicion_actual.getPosition().longitude),
                            14.0f));
        }
    }

    public void actualizarMapaPrincipal(View view)
    {
        mMap.clear();

        if(FiltrarMapaPrincipal.mostrar_cortes)
            this.cargarCortesEnMapa();

        if(FiltrarMapaPrincipal.mostrar_reportes)
            this.cargarReportesEnMapa();

        if(FiltrarMapaPrincipal.mostrar_sucursales)
            this.cargarSucursalesEnMapa();

        this.marcador_posicion_actual = null;
        if(GPSTracker.ubicacion_actual != null)
            this.actualizarPosicionActual(GPSTracker.ubicacion_actual);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mMap != null)
            this.actualizarMapaPrincipal(null);
    }


    public void cargarCortesEnMapa()
    {
        List<Corte> cortes = ConsultorAPI.cortes;
        for(int i = 0; i < cortes.size(); i++)
        {
            Corte corte_actual = cortes.get(i);

            Marker corte = mMap.addMarker(new MarkerOptions()
                    .position(corte_actual.getUbicacion())
                    .title("Corte")
                    .icon(Constantes.getIconoCorte(corte_actual.getServicio())) );
            corte.setTag(corte_actual);

            mMap.addCircle(new CircleOptions()
                    .center(corte_actual.getUbicacion())
                    .radius(corte_actual.getRadio())
                    .strokeColor(Constantes.STROKE_COLOR_CIRCLE)
                    .fillColor(Constantes.COLOR_CIRCLE)
                    .strokeWidth(5));
        }
    }

    public void cargarReportesEnMapa()
    {
        List<Reporte> reportes = ConsultorAPI.reportes;
        for(int i = 0; i < reportes.size(); i++)
        {
            Reporte rep_actual = reportes.get(i);

            if(!rep_actual.isResuelto()) {
                mMap.addMarker(new MarkerOptions()
                        .position(rep_actual.getUbicacion())
                        .title("Reporte")
                        .icon(Constantes.getIconoReporte(rep_actual.getServicio())));

                mMap.addCircle(new CircleOptions()
                        .center(rep_actual.getUbicacion())
                        .radius(rep_actual.getRadio())
                        .strokeColor(Constantes.STROKE_COLOR_CIRCLE)
                        .fillColor(Constantes.COLOR_CIRCLE)
                        .strokeWidth(5));
            }
        }
    }

    public void cargarSucursalesEnMapa()
    {
        List<Empresa> empresas = ConsultorAPI.empresas;
        for(int i = 0; i < empresas.size(); i++)
        {
            List<Sucursal> sucursales_actual = empresas.get(i).getSucursales();

            for(int j = 0; j < sucursales_actual.size(); j++)
            {
                mMap.addMarker(new MarkerOptions()
                        .position(sucursales_actual.get(j).getUbicacion())
                        .title("Sucursal " + empresas.get(i).getNombre())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icono_sucursal)));
            }
        }
    }

    public void buscarPelicula() {
        String titulo = "";
        String url = String.format("http://45.79.78.110/empresa", titulo);
        new ConsultorAPI().execute(url);
    }

}

