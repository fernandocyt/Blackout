package losmarinos.blackout.Actividades;

import losmarinos.blackout.Aviso;
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
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.Button;
import android.widget.ImageButton;
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

import static losmarinos.blackout.Constantes.COLOR_CIRCLE_ROJO;
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
    ImageButton button_actualizar;
    ImageButton button_centrar;
    Marker marcador_posicion_actual = null;
    Circle radio_reporte_seleccionado = null;

    List<Marker> marcadores_cortes = new ArrayList<>();
    List<Circle> radios_cortes = new ArrayList<>();

    List<Marker> marcadores_reportes = new ArrayList<>();

    List<Marker> marcadores_puntos_interes = new ArrayList<>();
    List<Circle> radios_puntos_interes = new ArrayList<>();

    List<Marker> marcadores_sucursales = new ArrayList<>();

    // Cargando
    ProgressBar progress_bar;
    TextView textview_carga;
    List<String> textos_carga;

    public static boolean flag_cerrar_sesion = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_principal);

        button_actualizar = (ImageButton)findViewById(R.id.btn_actualizar_mapa_principal);
        button_centrar = (ImageButton)findViewById(R.id.btn_centrar_mapa_principal);

        // SERVICIO
        if(!isMyServiceRunning(ServicioPeriodico.class)) {
            startService(new Intent(this, ServicioPeriodico.class));
        }

        // GPS
        if (GPSTracker.checkPermission(this)) {
            if(!isMyServiceRunning(GPSTracker.class)) {
                startService(new Intent(this, GPSTracker.class));
            }
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }


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

        textos_carga.add("Cargando empresas...");
        textos_carga.add("Cargando usuarios...");
        textos_carga.add("Cargando puntos de interés...");
        textos_carga.add("Cargando cortes de interés...");
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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 0 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if(!isMyServiceRunning(GPSTracker.class)) {
                startService(new Intent(this, GPSTracker.class));
            }
        }
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
        TextView tipo_header = (TextView)header.findViewById(R.id.lbl_tipo_nav_header_mapa_principal);
        if(Global.usuario_actual.getTipo() == Constantes.TIPOSUSUARIO.PERSONA) {
            tipo_header.setText("Cliente");
            tipo_header.setVisibility(View.GONE);
            navigationView.getMenu().findItem(R.id.drawer_agregar_corte_programado).setVisible(false);
            navigationView.getMenu().findItem(R.id.drawer_agregar_sucursal).setVisible(false);
            navigationView.getMenu().findItem(R.id.drawer_mis_cortes_programados).setVisible(false);
            navigationView.getMenu().findItem(R.id.drawer_crear_empresa).setVisible(false);
        }else if(Global.usuario_actual.getTipo() == Constantes.TIPOSUSUARIO.EMPRESA){
            tipo_header.setText("Empresa");
            tipo_header.setVisibility(View.VISIBLE);
            navigationView.getMenu().findItem(R.id.drawer_crear_reporte).setVisible(false);
            navigationView.getMenu().findItem(R.id.drawer_mis_reportes).setVisible(false);
            navigationView.getMenu().findItem(R.id.drawer_crear_empresa).setVisible(false);
        }else{
            tipo_header.setText("Administrador");
            tipo_header.setVisibility(View.VISIBLE);
            navigationView.getMenu().findItem(R.id.drawer_agregar_corte_programado).setVisible(false);
            navigationView.getMenu().findItem(R.id.drawer_agregar_sucursal).setVisible(false);
            navigationView.getMenu().findItem(R.id.drawer_mis_cortes_programados).setVisible(false);
        }

        username_header.setText(Global.usuario_actual.getNombre());
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
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            cerrarSesion();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("¿Seguro que quieres cerrar sesión?")
                    .setPositiveButton("Si", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener)
                    .show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void cerrarSesion(){
        LocalDB.borrarArchivoJSONUsuario(this);
        Global.vaciarTodo();

        // TODO: ARREGLAR ESTE CRASH
        //if(isMyServiceRunning(ServicioPeriodico.class))
        //    stopService(new Intent(this, ServicioPeriodico.class));

        Intent i = new Intent(getApplicationContext(), Login.class);
        startActivity(i);
        this.finish();
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

        GPSTracker.addObserver(this);

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

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(posicion, 13.0f));
                button_centrar.setVisibility(View.VISIBLE);
            } else {
                marcador_posicion_actual.setPosition(posicion);
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();

        if(this.radio_reporte_seleccionado != null){
            this.radio_reporte_seleccionado.remove();
            this.radio_reporte_seleccionado = null;
        }

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
        //mMap.clear();
        button_actualizar.setVisibility(View.GONE);
        textos_carga.clear();
        textview_carga.setVisibility(View.GONE);
        progress_bar.setVisibility(View.GONE);

        new ConsultorGETAPI("cortes", Global.token_usuario_actual, OBTENER_CORTES, new Global()).execute();
        new ConsultorGETAPI("reporte", Global.token_usuario_actual, OBTENER_REPORTES, new Global()).execute();
        new ConsultorGETAPI("usuarios/" + String.valueOf(Global.usuario_actual.getIdUsuario()) + "/puntos-de-interes", Global.token_usuario_actual, OBTENER_PUNTOSINTERES_POR_USUARIO, new Global()).execute();

        textos_carga.add("Cargando cortes...");
        textos_carga.add("Cargando reportes...");

        this.inicioCarga();

        if(FiltrarMapaPrincipal.mostrar_sucursales && FiltrarMapaPrincipal.id_empresa != -1){
            final Empresa empresa = Global.encontrarEmpresaPorId(FiltrarMapaPrincipal.id_empresa);
            textos_carga.add("Cargando sucursales...");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Global.cargo_sucursales = empresa.actualizarSucursales(MapaPrincipal.this);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cargarSucursalesEnMapa(empresa);
                        }
                    });
                }
            }).start();
        }else{
            vaciarListaMarcadores(marcadores_sucursales);
        }

        textos_carga.add("Cargando puntos de interés...");
        this.cargarPuntosInteresEnMapa();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(flag_cerrar_sesion){
            flag_cerrar_sesion = false;
            this.cerrarSesion();
        }

        if(mMap != null)
            this.actualizarMapaPrincipal(null);
    }

    public void cargarEmpresas(){
        this.terminoCarga();
    }

    public void cargarUsuarios(){
        this.terminoCarga();
    }

    public void cargarCortesInteres(){
        this.terminoCarga();
    }

    public void cargarPuntosInteresEnMapa()
    {
        if (!FiltrarMapaPrincipal.mostrar_puntos_interes){
            vaciarListaMarcadores(marcadores_puntos_interes);
            vaciarListaRadios(radios_puntos_interes);
            this.terminoCarga();
            return;
        }

        if(Global.cargo_puntos_interes) {
            vaciarListaMarcadores(marcadores_puntos_interes);
            vaciarListaRadios(radios_puntos_interes);

            List<PuntoInteres> puntos = Global.usuario_actual.getPuntosInteres();
            for (int i = 0; i < puntos.size(); i++) {
                PuntoInteres punto_actual = puntos.get(i);

                Marker punto = mMap.addMarker(new MarkerOptions()
                        .position(punto_actual.getUbicacion())
                        .title("Punto de interés")
                        .snippet(punto_actual.generarTexto())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icono_punto_interes)));
                punto.setTag(punto_actual);
                marcadores_puntos_interes.add(punto);

                Circle radio = mMap.addCircle(new CircleOptions()
                        .center(punto_actual.getUbicacion())
                        .radius(punto_actual.getRadio())
                        .strokeColor(Constantes.STROKE_COLOR_CIRCLE)
                        .fillColor(COLOR_CIRCLE_ROJO)
                        .strokeWidth(5));
                radios_puntos_interes.add(radio);
            }
        }

        this.terminoCarga();
    }

    public void cargarCortesEnMapa() {
        if (!FiltrarMapaPrincipal.mostrar_cortes){
            vaciarListaMarcadores(marcadores_cortes);
            vaciarListaRadios(radios_cortes);
            this.terminoCarga();
            return;
        }

        if(Global.cargo_cortes) {
            vaciarListaMarcadores(marcadores_cortes);
            vaciarListaRadios(radios_cortes);

            List<Corte> cortes = Global.cortes;
            for (int i = 0; i < cortes.size(); i++) {
                Corte corte_actual = cortes.get(i);

                if (corte_actual.isResuelto()) {
                    continue;
                }

                if (FiltrarMapaPrincipal.id_empresa != -1 &&
                        FiltrarMapaPrincipal.id_empresa != corte_actual.getIdEmpresa()) {
                    continue;
                }

                if (FiltrarMapaPrincipal.servicio != null &&
                        FiltrarMapaPrincipal.servicio != corte_actual.getServicio()) {
                    continue;
                }

                boolean programado = corte_actual.isProgramado();
                boolean interes = Global.usuario_actual.esCorteDeInteres(corte_actual.getId());

                if (programado && !interes && !FiltrarMapaPrincipal.mostrar_cortes_programados) {
                    continue;
                }

                if (interes && !programado && !FiltrarMapaPrincipal.mostrar_cortes_interes) {
                    continue;
                }

                Marker corte = mMap.addMarker(new MarkerOptions()
                        .position(corte_actual.getUbicacion())
                        .title("Corte")
                        .icon(Constantes.getIconoCorte(corte_actual.getServicio(), programado, interes)));
                corte.setTag(corte_actual);
                marcadores_cortes.add(corte);

                Circle radio = mMap.addCircle(new CircleOptions()
                        .center(corte_actual.getUbicacion())
                        .radius(corte_actual.getRadio())
                        .strokeColor(Constantes.STROKE_COLOR_CIRCLE)
                        .fillColor(Constantes.COLOR_CIRCLE)
                        .strokeWidth(5));
                radios_cortes.add(radio);
            }
        }
        this.terminoCarga();
    }

    public void cargarReportesEnMapa()
    {
        if(this.radio_reporte_seleccionado != null){
            this.radio_reporte_seleccionado.remove();
            this.radio_reporte_seleccionado = null;
        }

        if(!FiltrarMapaPrincipal.mostrar_reportes) {
            vaciarListaMarcadores(marcadores_reportes);
            this.terminoCarga();
            return;
        }

        if(Global.cargo_reportes) {
            vaciarListaMarcadores(marcadores_reportes);

            List<Reporte> reportes = Global.reportes;
            for (int i = 0; i < reportes.size(); i++) {
                Reporte rep_actual = reportes.get(i);

                if (FiltrarMapaPrincipal.id_empresa != -1 &&
                        FiltrarMapaPrincipal.id_empresa != rep_actual.getIdEmpresa()) {
                    continue;
                }

                if (FiltrarMapaPrincipal.servicio != null &&
                        FiltrarMapaPrincipal.servicio != rep_actual.getServicio()) {
                    continue;
                }

                if (rep_actual.isResuelto()) {
                    continue;
                }

                Marker reporte = mMap.addMarker(new MarkerOptions()
                        .position(rep_actual.getUbicacion())
                        .title("Reporte")
                        .snippet(rep_actual.generarSnippet())
                        .icon(Constantes.getIconoReporte(rep_actual.getServicio())));
                reporte.setTag(rep_actual);
                marcadores_reportes.add(reporte);
            }
        }

        this.terminoCarga();
    }

    public void cargarSucursalesEnMapa(Empresa empresa)
    {
        if(Global.cargo_sucursales) {
            vaciarListaMarcadores(marcadores_sucursales);

            List<Sucursal> sucursales = empresa.getSucursales();
            for (int i = 0; i < sucursales.size(); i++) {
                Marker sucursal = mMap.addMarker(new MarkerOptions()
                        .position(sucursales.get(i).getUbicacion())
                        .title("Sucursal " + empresa.getNombre())
                        .snippet(sucursales.get(i).getDireccion() + " (Tel: " + sucursales.get(i).getTelefono() + ")")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.icono_sucursal)));
                marcadores_sucursales.add(sucursal);
            }
        }
        terminoCarga();
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
            button_actualizar.setVisibility(View.VISIBLE);

            if(!Global.cargo_usuarios || !Global.cargo_empresas || !Global.cargo_cortes
                    || !Global.cargo_reportes || !Global.cargo_puntos_interes || !Global.cargo_cortes_interes){
                Aviso.showToast(this, "Error al actualizar datos. Compruebe su conexión a internet.");
            }
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

    public void vaciarListaMarcadores(List<Marker> lista){
        for(int i = 0; i < lista.size(); i++){
            lista.get(i).remove();
        }
        lista.clear();
    }

    public void vaciarListaRadios(List<Circle> lista){
        for(int i = 0; i < lista.size(); i++){
            lista.get(i).remove();
        }
        lista.clear();
    }
}

