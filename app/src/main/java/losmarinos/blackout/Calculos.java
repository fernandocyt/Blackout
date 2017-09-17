package losmarinos.blackout;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import losmarinos.blackout.Objetos.Corte;
import losmarinos.blackout.Objetos.Empresa;
import losmarinos.blackout.Objetos.Reporte;

/**
 * Created by garci on 12/8/2017.
 */

public class Calculos {

    public static class valorEmpresa implements Comparable<valorEmpresa>
    {
        public double valor;
        public String unidad;
        public int id_empresa;

        public int compareTo(valorEmpresa v) {

            if (this.valor > v.valor) {
                return -1;
            }
            else if (this.valor < v.valor) {
                return 1;
            }
            else {
                return 0;
            }

        }
    }

    public static boolean hayInterseccion(LatLng p1, double r1, LatLng p2, double r2)
    {
        double distancia_centros = Calculos.calcularDistancia(p1, p2);

        double suma_radios = r1 + r2;

        if(distancia_centros < suma_radios)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static double calcularDistancia(LatLng p1, LatLng p2)
    {
        Location loc1 = new Location("loc1");
        loc1.setLatitude(p1.latitude);
        loc1.setLongitude(p1.longitude);

        Location loc2 = new Location("loc2");
        loc2.setLatitude(p2.latitude);
        loc2.setLongitude(p2.longitude);

        return loc1.distanceTo(loc2);
    }

    public static List<valorEmpresa> porcentajeCortesTotal(Constantes.SERVICIO servicio)
    {
        List<Corte> cortes = Global.cortes;
        List<Empresa> empresas = Global.empresas;
        int total_cortes_servicio = 0;
        List<valorEmpresa> stat_empresas = new ArrayList<>();

        // Primero obtengo todas las empresas y las agrego a la lista a retornar
        for(int i = 0; i < empresas.size(); i++){
            if(servicio == null || empresas.get(i).getTipoServicio() == servicio) {
                valorEmpresa nuevo_stat = new valorEmpresa();
                nuevo_stat.id_empresa = empresas.get(i).getSubId();
                nuevo_stat.valor = 0;
                nuevo_stat.unidad = "%";
                stat_empresas.add(nuevo_stat);
            }
        }

        // Recorro todos los cortes
        for(int i = 0; i < cortes.size(); i++){
            if(servicio == null || cortes.get(i).getServicio() == servicio) {
                total_cortes_servicio++;
                int id_empresa = cortes.get(i).getIdEmpresa();
                // Me fijo si estaba en la lista le sumo 1 al valor
                for(int j = 0; j < stat_empresas.size(); j++){
                    if(stat_empresas.get(j).id_empresa == id_empresa){
                        stat_empresas.get(j).valor++;
                    }
                }
            }
        }

        // Los valores, que hasta ahora eran la cantidad de cortes, los paso a %
        for(int i = 0; i < stat_empresas.size(); i++){
            stat_empresas.get(i).valor = stat_empresas.get(i).valor * 100 / total_cortes_servicio;
        }

        Collections.sort(stat_empresas);

        return stat_empresas;
    }

    public static List<valorEmpresa> cantidadCortesProgramados(Constantes.SERVICIO servicio)
    {
        List<Corte> cortes = Global.cortes;
        List<Empresa> empresas = Global.empresas;
        int total_cortes_servicio = 0;
        List<valorEmpresa> stat_empresas = new ArrayList<>();

        // Primero obtengo todas las empresas y las agrego a la lista a retornar
        for(int i = 0; i < empresas.size(); i++){
            if(servicio == null || empresas.get(i).getTipoServicio() == servicio) {
                valorEmpresa nuevo_stat = new valorEmpresa();
                nuevo_stat.id_empresa = empresas.get(i).getSubId();
                nuevo_stat.valor = 0;
                nuevo_stat.unidad = "cortes";
                stat_empresas.add(nuevo_stat);
            }
        }

        // Recorro todos los cortes
        for(int i = 0; i < cortes.size(); i++){

            if(!cortes.get(i).isProgramado())
                continue;

            if(servicio == null || cortes.get(i).getServicio() == servicio) {
                total_cortes_servicio++;
                int id_empresa = cortes.get(i).getIdEmpresa();
                // Me fijo si estaba en la lista le sumo 1 al valor
                for(int j = 0; j < stat_empresas.size(); j++){
                    if(stat_empresas.get(j).id_empresa == id_empresa){
                        stat_empresas.get(j).valor++;
                    }
                }
            }
        }

        Collections.sort(stat_empresas);

        return stat_empresas;
    }

    public static List<valorEmpresa> tiempoPromedioDeResolucionPorEmpresa(Constantes.SERVICIO servicio)
    {
        List<Corte> cortes = Global.cortes;
        List<Empresa> empresas = Global.empresas;
        int total_cortes_servicio = 0;
        List<valorEmpresa> stat_empresas = new ArrayList<>();

        // Primero obtengo todas las empresas y las agrego a la lista a retornar
        for(int i = 0; i < empresas.size(); i++){
            if(servicio == null || empresas.get(i).getTipoServicio() == servicio) {
                valorEmpresa nuevo_stat = new valorEmpresa();
                nuevo_stat.id_empresa = empresas.get(i).getSubId();
                nuevo_stat.valor = 0;
                nuevo_stat.unidad = "dias";
                stat_empresas.add(nuevo_stat);
            }
        }

        for(int i = 0; i < cortes.size(); i++){
            if(servicio == null || cortes.get(i).getServicio() == servicio) {
                total_cortes_servicio++;
                int id_empresa = cortes.get(i).getIdEmpresa();

                // Me fijo si estaba en la lista
                for(int j = 0; j < stat_empresas.size(); j++){
                    if(stat_empresas.get(j).id_empresa == id_empresa){

                        // Si esta calculo la diferencia de dias del corte y
                        Date inicio = cortes.get(i).getFechaInicio();
                        Date fin = cortes.get(i).getFechaFin();

                        long diferencia = fin.getTime() - inicio.getTime();
                        long dias = diferencia / (24 * 60 * 60 * 1000);

                        // Si ya habia una cantidad de dias saco el promedio, sino la meto directo
                        if(stat_empresas.get(j).valor == 0){
                            stat_empresas.get(j).valor = dias;
                        }else{
                            stat_empresas.get(j).valor = (stat_empresas.get(j).valor + dias) / 2;
                        }
                    }
                }
            }
        }

        Collections.sort(stat_empresas);

        return stat_empresas;
    }
}
