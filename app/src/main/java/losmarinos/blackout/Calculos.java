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

import static java.lang.Math.abs;

/**
 * Created by garci on 12/8/2017.
 */

public class Calculos {

    public static class valorEmpresa implements Comparable<valorEmpresa>
    {
        public double valor;
        public String unidad;
        public int id_empresa;

        public static int ordenar = 0;
        // ordenar = 0 -> mayor a menor
        // ordenar = 1 -> menor a mayor
        // ordenar = 2 -> alfabeticamente

        public int compareTo(valorEmpresa v) {
            switch (ordenar)
            {
                case 0:
                    if (this.valor > v.valor) {
                        return -1;
                    } else if (this.valor < v.valor) {
                        return 1;
                    }
                    break;
                case 1:
                    if (this.valor < v.valor) {
                        return -1;
                    } else if (this.valor > v.valor) {
                        return 1;
                    }
                    break;
                case 2:
                    String emp1 = Global.encontrarEmpresaPorId(this.id_empresa).getNombre();
                    String emp2 = Global.encontrarEmpresaPorId(v.id_empresa).getNombre();
                    int comp = emp1.compareTo(emp2);
                    if(comp != 0) {
                        return comp;
                    }
                    break;
            }
            return 0;
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


    public static double calificacionEmpresa(Empresa empresa)
    {
        // 2ptos - cantidad de cortes no programados ultimo año
        //      +100 = 0pts     -20 = 3pts
        // 3ptos - duracion promedio de cortes
        //      +24hs = 0pts    -2hs = 2pts
        // FUTURO --> 1ptos - respuestas x corte ratio

        List<Corte> cortes_empresa = Global.encontrarCortesPorEmpresa(empresa.getSubId());
        int cant_cortes_no_programados_ultimo_año = 0;
        double hs_promedio_corte = 0;
        for(int i = 0; i < cortes_empresa.size(); i++){

            Date inicio = cortes_empresa.get(i).getFechaInicio();
            Date fin = cortes_empresa.get(i).getFechaFin();
            Date ahora = Calendar.getInstance().getTime();

            // Cantidad de cortes totales ultimo año
            long dif_inicio_ahora_dias = abs(ahora.getTime() - inicio.getTime()) / (24 * 60 * 60 * 1000);
            if(dif_inicio_ahora_dias < 365){
                cant_cortes_no_programados_ultimo_año++;
            }

            // Promedio duracion cortes
            if(fin != null){
                long dif_inicio_fin_horas = abs(fin.getTime() - inicio.getTime()) / (60 * 60 * 1000);

                if(hs_promedio_corte == 0){
                    hs_promedio_corte = dif_inicio_fin_horas;
                }else{
                    hs_promedio_corte = (hs_promedio_corte + dif_inicio_fin_horas)/2;
                }
            }
        }


        // La parte 1 es cantidad de cortes en 1 año
        double parte_1 = 0; //acumulado real de esta parte
        double valor_1 = 3; //vale 3 estrellas
        double min_1 = 2;  //si el valor es menor a 20, le doy las 3 estrellas
        double max_1 = 10; //si el valor es mayor a 100, no le doy un choto

        if(cant_cortes_no_programados_ultimo_año < min_1){
            parte_1 = valor_1;
        }else if(cant_cortes_no_programados_ultimo_año < max_1){
            parte_1 = valor_1 - (cant_cortes_no_programados_ultimo_año - min_1) * valor_1 / (max_1 - min_1);
        }

        // La parte 2 es el promedio de la duracion de los cortes
        double parte_2 = 0; //acumulado real de esta parte
        double valor_2 = 2; //vale 2 estrellas
        double min_2 = 2;  //si el valor es menor a 2, le doy las 2 estrellas
        double max_2 = 24; //si el valor es mayor a 24, no le doy un choto

        if(hs_promedio_corte < min_2){
            parte_2 = valor_2;
        }else if(hs_promedio_corte < max_2){
            parte_2 = valor_2 - (hs_promedio_corte - min_2) * valor_2 / (max_2 - min_2);
        }

        return parte_1 + parte_2;
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
                nuevo_stat.valor = -1;
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

                        if(inicio == null || fin == null){
                            continue;
                        }

                        long diferencia = fin.getTime() - inicio.getTime();
                        long horas = diferencia / (60 * 60 * 1000);

                        // Si ya habia una cantidad de dias saco el promedio, sino la meto directo
                        if(stat_empresas.get(j).valor == 0){
                            stat_empresas.get(j).valor = horas;
                        }else{
                            stat_empresas.get(j).valor = (stat_empresas.get(j).valor + horas) / 2;
                        }
                    }
                }
            }
        }
        return stat_empresas;
    }
}
