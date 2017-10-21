package losmarinos.blackout.Actividades;

import losmarinos.blackout.Calculos;
import losmarinos.blackout.ConsultorGETAPI;
import losmarinos.blackout.Global;
import losmarinos.blackout.LocalDB;
import losmarinos.blackout.Objetos.Corte;
import losmarinos.blackout.Objetos.Empresa;
import losmarinos.blackout.Objetos.PuntoInteres;
import losmarinos.blackout.Objetos.Sucursal;
import losmarinos.blackout.R;
import losmarinos.blackout.Constantes;
import losmarinos.blackout.ObservadorGPS;
import losmarinos.blackout.GPSTracker;
import losmarinos.blackout.ConsultorPOSTAPI;
import losmarinos.blackout.Objetos.Reporte;
import losmarinos.blackout.ServicioPeriodico;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Marker;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static losmarinos.blackout.Constantes.TAGAPI.OBTENER_CORTES;
import static losmarinos.blackout.Constantes.TAGAPI.OBTENER_CORTESINTERES_POR_USUARIO;
import static losmarinos.blackout.Constantes.TAGAPI.OBTENER_EMPRESAS;
import static losmarinos.blackout.Constantes.TAGAPI.OBTENER_PUNTOSINTERES_POR_USUARIO;
import static losmarinos.blackout.Constantes.TAGAPI.OBTENER_REPORTES;
import static losmarinos.blackout.Constantes.TAGAPI.OBTENER_SUCURSALES_POR_EMPRESA;
import static losmarinos.blackout.Constantes.TAGAPI.OBTENER_USUARIOS;

// Actividad del mapa principal
public class MapaPrincipal extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
                                                                OnMapReadyCallback,
                                                                GoogleMap.OnMarkerClickListener,
                                                                ObservadorGPS{
    GoogleMap mMap;
    Marker marcador_posicion_actual = null;
    Circle radio_reporte_seleccionado = null;

    ProgressBar progress_bar;
    TextView textview_carga;
    List<String> textos_carga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_principal);

        // SERVICIO
        if(!isMyServiceRunning(ServicioPeriodico.class))
            startService(new Intent(this, ServicioPeriodico.class));

        // GPS
        if (!GPSTracker.checkPermission(this)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

        if(!isMyServiceRunning(GPSTracker.class))
            startService(new Intent(this, GPSTracker.class));
        GPSTracker.addObserver(this);

        // MAPA
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        progress_bar = (ProgressBar)findViewById(R.id.prog_mapa_principal);
        textview_carga = (TextView)findViewById(R.id.txt_cargando_mapa_principal);
        textos_carga = new ArrayList<>();

        // CARGA DE DATOS
        this.cargarDrawer();

        Global.mapa_principal = this;

        new ConsultorGETAPI("empresa", Global.token_usuario_actual, OBTENER_EMPRESAS, new Global()).execute();
        new ConsultorGETAPI("usuarios", Global.token_usuario_actual, OBTENER_USUARIOS, new Global()).execute();
        new ConsultorGETAPI("usuarios/" + String.valueOf(Global.usuario_actual.getIdUsuario()) + "/puntos-de-interes", Global.token_usuario_actual, OBTENER_PUNTOSINTERES_POR_USUARIO, new Global()).execute();
        new ConsultorGETAPI("usuarios/" + String.valueOf(Global.usuario_actual.getIdUsuario()) + "/cortes-de-interes", Global.token_usuario_actual, OBTENER_CORTESINTERES_POR_USUARIO, new Global()).execute();

        textos_carga.add("Cargando puntos de interes...");
        this.inicioCarga();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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

    public void cargarDrawer()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        TextView username_header = (TextView)header.findViewById(R.id.lbl_username_nav_header_mapa_principal);
        TextView mail_header = (TextView)header.findViewById(R.id.lbl_mail_nav_header_mapa_principal);

        String tipo = "";
        if(Global.usuario_actual.getTipo() == Constantes.TIPOSUSUARIO.PERSONA) {
            tipo = " (USUARIO)";
            navigationView.getMenu().findItem(R.id.drawer_agregar_corte_programado).setVisible(false);
            navigationView.getMenu().findItem(R.id.drawer_agregar_sucursal).setVisible(false);
            navigationView.getMenu().findItem(R.id.drawer_mis_cortes_programados).setVisible(false);
            navigationView.getMenu().findItem(R.id.drawer_crear_empresa).setVisible(false);
        }else if(Global.usuario_actual.getTipo() == Constantes.TIPOSUSUARIO.EMPRESA){
            tipo = " (EMPRESA)";
            navigationView.getMenu().findItem(R.id.drawer_crear_reporte).setVisible(false);
            navigationView.getMenu().findItem(R.id.drawer_mis_reportes).setVisible(false);
            navigationView.getMenu().findItem(R.id.drawer_crear_empresa).setVisible(false);
        }else{
            tipo = " (ADMIN)";
            navigationView.getMenu().findItem(R.id.drawer_agregar_corte_programado).setVisible(false);
            navigationView.getMenu().findItem(R.id.drawer_agregar_sucursal).setVisible(false);
            navigationView.getMenu().findItem(R.id.drawer_mis_cortes_programados).setVisible(false);
        }

        username_header.setText(Global.usuario_actual.getNombre() + tipo);
        mail_header.setText(Global.usuario_actual.getMail());

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.drawer_crear_reporte) {
            Intent i = new Intent(getApplicationContext(), CrearReporte.class);
            startActivity(i);
        } else if (id == R.id.drawer_mis_reportes) {
            Intent i = new Intent(getApplicationContext(), MisReportes.class);
            startActivity(i);
        } else if (id == R.id.drawer_mis_puntos_interes) {
            Intent i = new Intent(getApplicationContext(), MisPuntosInteres.class);
            startActivity(i);
        } else if (id == R.id.drawer_mis_cortes_programados) {
            Intent i = new Intent(getApplicationContext(), MisCortesProgramados.class);
            startActivity(i);
        } else if (id == R.id.drawer_accion_filtrar){
            Intent i = new Intent(getApplicationContext(), FiltrarMapaPrincipal.class);
            startActivity(i);
        } else if (id == R.id.drawer_agregar_punto_interes){
            Intent i = new Intent(getApplicationContext(), CrearPuntoInteres.class);
            startActivity(i);
        } else if (id == R.id.drawer_buscar_empresa){
            Intent i = new Intent(getApplicationContext(), BuscarEmpresa.class);
            startActivity(i);
        } else if (id == R.id.drawer_estadisticas){
            Intent i = new Intent(getApplicationContext(), ConsultarEstadisticas.class);
            startActivity(i);
        } else if (id == R.id.drawer_preferencias){
            Intent i = new Intent(getApplicationContext(), Preferencias.class);
            startActivity(i);
        } else if (id == R.id.drawer_agregar_corte_programado){
            Intent i = new Intent(getApplicationContext(), CrearCorteProgramado.class);
            startActivity(i);
        } else if (id == R.id.drawer_agregar_sucursal){
            Intent i = new Intent(getApplicationContext(), CrearSucursal.class);
            startActivity(i);
        } else if (id == R.id.drawer_crear_empresa){
            Intent i = new Intent(getApplicationContext(), CrearEmpresa.class);
            startActivity(i);
        } else if (id == R.id.drawer_cerrar_sesion){
            LocalDB.borrarArchivoJSONUsuario(this);
            Global.vaciarTodo();

            // TODO: ARREGLAR ESTE CRASH
            //if(isMyServiceRunning(ServicioPeriodico.class))
            //    stopService(new Intent(this, ServicioPeriodico.class));

            Intent i = new Intent(getApplicationContext(), Login.class);
            startActivity(i);
            this.finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onDestroy() {
        GPSTracker.removeObserver(this);
        super.onDestroy();
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
        if(mMap != null) {
            if (marcador_posicion_actual == null) {
                marcador_posicion_actual = mMap.addMarker(new MarkerOptions()
                        .position(posicion)
                        .title("Usuario")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icono_ubicacion_usuario))
                        .zIndex(3));
            } else {
                marcador_posicion_actual.setPosition(posicion);
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();

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
        else if(marker.getTitle().equals("Reporte"))
        {
            if(this.radio_reporte_seleccionado != null){
                this.radio_reporte_seleccionado.remove();
            }

            Reporte rep_actual = (Reporte)marker.getTag();
            this.radio_reporte_seleccionado = mMap.addCircle(new CircleOptions()
                    .center(rep_actual.getUbicacion())
                    .radius(rep_actual.getRadio())
                    .strokeColor(Constantes.STROKE_COLOR_CIRCLE)
                    .fillColor(Constantes.COLOR_CIRCLE)
                    .strokeWidth(5));
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

        new ConsultorGETAPI("cortes", Global.token_usuario_actual, OBTENER_CORTES, new Global()).execute();
        new ConsultorGETAPI("reporte", Global.token_usuario_actual, OBTENER_REPORTES, new Global()).execute();

        textos_carga.add("Cargando cortes...");
        textos_carga.add("Cargando reportes...");

        this.inicioCarga();

        if(FiltrarMapaPrincipal.mostrar_sucursales && FiltrarMapaPrincipal.id_empresa != -1){
            Empresa empresa = Global.encontrarEmpresaPorId(FiltrarMapaPrincipal.id_empresa);
            textos_carga.add("Cargando sucursales...");
            empresa.actualizarSucursales(this);
            this.cargarSucursalesEnMapa(empresa);
        }

        //Global.calcularNuevosCortes();

        //this.cargarCortesEnMapa();
        //this.cargarReportesEnMapa();
        this.cargarPuntosInteresEnMapa();

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


    public void cargarPuntosInteresEnMapa()
    {
        if (!FiltrarMapaPrincipal.mostrar_puntos_interes){
            this.terminoCarga();
            return;
        }

        List<PuntoInteres> puntos = Global.usuario_actual.getPuntosInteres();
        for(int i = 0; i < puntos.size(); i++) {
            PuntoInteres punto_actual = puntos.get(i);

            Marker punto = mMap.addMarker(new MarkerOptions()
                    .position(punto_actual.getUbicacion())
                    .title("Punto de interes")
                    .snippet(punto_actual.generarTexto())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icono_punto_interes)));
            punto.setTag(punto_actual);

            mMap.addCircle(new CircleOptions()
                    .center(punto_actual.getUbicacion())
                    .radius(punto_actual.getRadio())
                    .strokeColor(Constantes.STROKE_COLOR_CIRCLE)
                    .fillColor(0x30ff0000)
                    .strokeWidth(5));
        }

        this.terminoCarga();
    }

    public void cargarCortesEnMapa() {
        if (!FiltrarMapaPrincipal.mostrar_cortes){
            this.terminoCarga();
            return;
        }

        List<Corte> cortes = Global.cortes;
        for(int i = 0; i < cortes.size(); i++)
        {
            Corte corte_actual = cortes.get(i);

            if(corte_actual.isResuelto()) {
                continue;
            }

            if(FiltrarMapaPrincipal.id_empresa != -1 &&
                FiltrarMapaPrincipal.id_empresa != corte_actual.getIdEmpresa())
            {
                continue;
            }

            if(FiltrarMapaPrincipal.servicio != null &&
                    FiltrarMapaPrincipal.servicio != corte_actual.getServicio())
            {
                continue;
            }

            boolean programado = corte_actual.isProgramado();
            boolean interes = Global.usuario_actual.esCorteDeInteres(corte_actual.getId());

            if(programado && !interes && !FiltrarMapaPrincipal.mostrar_cortes_programados){
                continue;
            }

            if(interes && !programado && !FiltrarMapaPrincipal.mostrar_cortes_interes){
                continue;
            }

            Marker corte = mMap.addMarker(new MarkerOptions()
                    .position(corte_actual.getUbicacion())
                    .title("Corte")
                    .icon(Constantes.getIconoCorte(corte_actual.getServicio(), programado, interes)));
            corte.setTag(corte_actual);

            mMap.addCircle(new CircleOptions()
                    .center(corte_actual.getUbicacion())
                    .radius(corte_actual.getRadio())
                    .strokeColor(Constantes.STROKE_COLOR_CIRCLE)
                    .fillColor(Constantes.COLOR_CIRCLE)
                    .strokeWidth(5));
        }
        this.terminoCarga();
    }

    public void cargarReportesEnMapa()
    {
        if(!FiltrarMapaPrincipal.mostrar_reportes) {
            this.terminoCarga();
            return;
        }

        List<Reporte> reportes = Global.reportes;
        for(int i = 0; i < reportes.size(); i++)
        {
            Reporte rep_actual = reportes.get(i);

            if(FiltrarMapaPrincipal.id_empresa != -1 &&
                    FiltrarMapaPrincipal.id_empresa != rep_actual.getIdEmpresa())
            {
                continue;
            }

            if(FiltrarMapaPrincipal.servicio != null &&
                    FiltrarMapaPrincipal.servicio != rep_actual.getServicio())
            {
                continue;
            }

            if(rep_actual.isResuelto()) {
                continue;
            }

            mMap.addMarker(new MarkerOptions()
                    .position(rep_actual.getUbicacion())
                    .title("Reporte")
                    .snippet(rep_actual.generarSnippet())
                    .icon(Constantes.getIconoReporte(rep_actual.getServicio())))
            .setTag(rep_actual);
        }

        this.terminoCarga();
    }

    public void cargarSucursalesEnMapa(Empresa empresa)
    {
        List<Sucursal> sucursales = empresa.getSucursales();
        for(int i = 0; i < sucursales.size(); i++)
        {
            Sucursal sucursal = sucursales.get(i);

            mMap.addMarker(new MarkerOptions()
                    .position(sucursales.get(i).getUbicacion())
                    .title("Sucursal " + empresa.getNombre())
                    .snippet(sucursal.getDireccion() + " (Tel: " + sucursal.getTelefono() + ")")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icono_sucursal)));
        }
    }

    public void inicioCarga(){
        textview_carga.setVisibility(View.VISIBLE);
        progress_bar.setVisibility(View.VISIBLE);
        textview_carga.setText(textos_carga.get(0));
    }

    public void terminoCarga(){
        if(textos_carga.size() > 1) {
            textos_carga.remove(0);
            textview_carga.setText(textos_carga.get(0));
        }else{
            textos_carga.clear();
            textview_carga.setVisibility(View.GONE);
            progress_bar.setVisibility(View.GONE);
        }
    }

    public void irAPerfilEmpresa(View view){
        if(Global.usuario_actual.getTipo() == Constantes.TIPOSUSUARIO.EMPRESA) {
            Intent i = new Intent(getApplicationContext(), PerfilEmpresa.class);
            i.putExtra("idEmpresa", Global.usuario_actual.getSubId());
            try {
                startActivity(i);
            } catch (Exception e) {
                throw e;
            }
        }
    }
}

