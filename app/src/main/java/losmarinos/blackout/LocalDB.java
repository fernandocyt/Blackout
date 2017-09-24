package losmarinos.blackout;

import android.content.Context;
import android.util.Xml;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import losmarinos.blackout.Objetos.Reporte;

import static android.R.attr.data;
import static java.security.AccessController.getContext;

/**
 * Created by garci on 20/8/2017.
 */

public class LocalDB {
    static String file_name_usuario = "userData";
    static String file_name_cortes_avisados = "cortesAvisados";
    static String file_name_cortes_resueltos_avisados = "cortesResueltosAvisados";
    static String file_name_reportes_confirmados = "reportesConfirmados";
    static String file_name_preferencias = "preferencias";

    // region Archivos XML
    public static void crearXMLUsuario(Context contexto, String nombre, String pass, String mail, String token)
    {
        try {
            FileOutputStream fileos= contexto.openFileOutput(file_name_usuario, Context.MODE_PRIVATE);
            XmlSerializer xmlSerializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();
            xmlSerializer.setOutput(writer);
            xmlSerializer.startDocument("UTF-8", true);
            xmlSerializer.startTag(null, file_name_usuario);

            xmlSerializer.startTag(null, "nombre");
            xmlSerializer.text(nombre);
            xmlSerializer.endTag(null, "nombre");

            xmlSerializer.startTag(null, "pass");
            xmlSerializer.text(pass);
            xmlSerializer.endTag(null, "pass");

            xmlSerializer.startTag(null, "mail");
            xmlSerializer.text(mail);
            xmlSerializer.endTag(null, "mail");

            xmlSerializer.startTag(null, "token");
            xmlSerializer.text(token);
            xmlSerializer.endTag(null, "token");

            xmlSerializer.endTag(null, file_name_usuario);
            xmlSerializer.endDocument();
            xmlSerializer.flush();
            String dataWrite = writer.toString();
            fileos.write(dataWrite.getBytes());
            fileos.close();
        }
        catch (Exception e) {
            return;
        }
    }

    public static void borrarXMLUsuario(Context context){
        context.deleteFile(file_name_usuario);
    }

    public static ArrayList<String> leerXMLUsuario(Context context)
    {
        ArrayList<String> userData = new ArrayList<String>();
        FileInputStream fis;
        InputStreamReader isr;
        char[] inputBuffer;
        String data = null;
        try {
            fis = context.openFileInput(file_name_usuario);
            isr = new InputStreamReader(fis);
            inputBuffer = new char[fis.available()];
            isr.read(inputBuffer);
            data = new String(inputBuffer);
            isr.close();
            fis.close();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(data));

            int eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) {
                    System.out.println("Start document");
                } else if (eventType == XmlPullParser.START_TAG) {
                    System.out.println("Start tag " + xpp.getName());
                } else if (eventType == XmlPullParser.END_TAG) {
                    System.out.println("End tag " + xpp.getName());
                } else if (eventType == XmlPullParser.TEXT) {
                    userData.add(xpp.getText());
                }

                eventType = xpp.next();
            }
        }catch (Exception e){
            return null;
        }
        return userData;
    }

    // endregion

    public static void borrarTodasLasDB(Context contexto){
        borrarArchivoJSONUsuario(contexto);
        borrarArchivoJSONCortesAvisados(contexto);
        borrarArchivoJSONCortesResueltosAvisados(contexto);
        borrarArchivoJSONReportesConfirmados(contexto);
    }

    public static void crearArchivoJSONUsuario(Context contexto, int id, String nombre, String pass, String mail, String token) {
        try {
            JSONObject usuario = new JSONObject();
            usuario.put("id", id);
            usuario.put("nombre", nombre);
            usuario.put("pass", pass);
            usuario.put("mail", mail);
            usuario.put("token", token);

            FileOutputStream fileos = contexto.openFileOutput(file_name_usuario, 0);
            fileos.write(usuario.toString().getBytes());
            fileos.close();
        }catch (Exception e){

        }
    }

    public static boolean leerArchivoJSONUsuario(Context contexto, int[] id, StringBuilder nombre, StringBuilder pass, StringBuilder mail, StringBuilder token) {
        try {
            FileInputStream fis = contexto.openFileInput(file_name_usuario);
            InputStreamReader isr = new InputStreamReader(fis);
            char[] inputBuffer = new char[fis.available()];
            isr.read(inputBuffer);
            String data = new String(inputBuffer);

            JSONObject obj = new JSONObject(data);
            id[0] = obj.getInt("id");
            nombre.append(obj.getString("nombre"));
            pass.append(obj.getString("pass"));
            mail.append(obj.getString("mail"));
            token.append(obj.getString("token"));

            isr.close();
            fis.close();

            return true;
        }catch (Exception e){
            return false;
        }
    }

    public static void borrarArchivoJSONUsuario(Context context){
        context.deleteFile(file_name_usuario);
    }

    public static void crearArchivoJSONCortesAvisados(Context contexto, JSONArray array, boolean resuelto) {
        try {
            String file_name = "";
            if(resuelto){
                file_name = file_name_cortes_resueltos_avisados;
            }else{
                file_name = file_name_cortes_avisados;
            }

            FileOutputStream fileos = contexto.openFileOutput(file_name, Context.MODE_PRIVATE);
            fileos.write(array.toString().getBytes());
            fileos.close();
        }catch (Exception e){

        }
    }

    public static void agregarArchivoJSONCortesAvisados(Context contexto, int id_corte, boolean resuelto) {
        try {
            String file_name = "";
            if(resuelto){
                file_name = file_name_cortes_resueltos_avisados;
            }else{
                file_name = file_name_cortes_avisados;
            }

            FileInputStream fis = contexto.openFileInput(file_name);
            InputStreamReader isr = new InputStreamReader(fis);
            char[] inputBuffer = new char[fis.available()];
            isr.read(inputBuffer);
            String data = new String(inputBuffer);

            JSONArray arr = new JSONArray(data);
            arr.put(id_corte);

            isr.close();
            fis.close();

            borrarArchivoJSONCortesAvisados(contexto);
            crearArchivoJSONCortesAvisados(contexto, arr, resuelto);

        }catch (FileNotFoundException e){
            JSONArray arr = new JSONArray();
            arr.put(id_corte);

            crearArchivoJSONCortesAvisados(contexto, arr, resuelto);
        }catch (Exception e){
            borrarArchivoJSONCortesAvisados(contexto);
        }
    }

    public static boolean estaEnArchivoJSONCortesAvisados(Context contexto, int id_corte, boolean resuelto) {
        try {
            String file_name = "";
            if(resuelto){
                file_name = file_name_cortes_resueltos_avisados;
            }else{
                file_name = file_name_cortes_avisados;
            }

            FileInputStream fis = contexto.openFileInput(file_name);
            InputStreamReader isr = new InputStreamReader(fis);
            char[] inputBuffer = new char[fis.available()];
            isr.read(inputBuffer);
            String data = new String(inputBuffer);

            JSONArray arr = new JSONArray(data);

            isr.close();
            fis.close();

            for(int i = 0; i < arr.length(); i++){
                if(arr.getInt(i) == id_corte){
                    return true;
                }
            }

            return false;
        }catch (Exception e){
            return false;
        }
    }

    public static void borrarArchivoJSONCortesAvisados(Context context){
        context.deleteFile(file_name_cortes_avisados);
    }

    public static void borrarArchivoJSONCortesResueltosAvisados(Context context){
        context.deleteFile(file_name_cortes_resueltos_avisados);
    }

    public static void crearArchivoJSONReportesConfirmados(Context contexto, JSONArray array) {
        try {
            FileOutputStream fileos = contexto.openFileOutput(file_name_reportes_confirmados, Context.MODE_PRIVATE);
            fileos.write(array.toString().getBytes());
            fileos.close();
        }catch (Exception e){

        }
    }

    public static void agregarArchivoJSONReportesConfirmados(Context contexto, int id_reporte, Date fecha) {
        try {
            FileInputStream fis = contexto.openFileInput(file_name_reportes_confirmados);
            InputStreamReader isr = new InputStreamReader(fis);
            char[] inputBuffer = new char[fis.available()];
            isr.read(inputBuffer);
            String data = new String(inputBuffer);

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String str_fecha = format.format(fecha);

            JSONArray arr = new JSONArray(data);

            boolean estaba = false;
            for(int i = 0; i < arr.length(); i++) {
                JSONObject obj_reporte = arr.getJSONObject(i);
                if(obj_reporte.getInt("id") == id_reporte){
                    obj_reporte.put("ultima_confirmacion", str_fecha);
                    estaba = true;
                }
            }
            if(!estaba) {
                JSONObject rep = new JSONObject();
                rep.put("id", id_reporte);
                rep.put("ultima_confirmacion", str_fecha);
                arr.put(rep);
            }

            isr.close();
            fis.close();

            borrarArchivoJSONReportesConfirmados(contexto);
            crearArchivoJSONReportesConfirmados(contexto, arr);

        }catch (FileNotFoundException e){
            try {
                JSONArray arr = new JSONArray();
                    JSONObject rep = new JSONObject();
                    rep.put("id", id_reporte);
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String str_fecha = format.format(fecha);
                    rep.put("ultima_confirmacion", str_fecha);
                arr.put(rep);

                crearArchivoJSONReportesConfirmados(contexto, arr);
            }catch (Exception e2){}
        }catch (Exception e){
            borrarArchivoJSONReportesConfirmados(contexto);
        }
    }

    public static Date estaEnArchivoJSONReportesConfirmados(Context contexto, int id_reporte) {
        try {
            FileInputStream fis = contexto.openFileInput(file_name_reportes_confirmados);
            InputStreamReader isr = new InputStreamReader(fis);
            char[] inputBuffer = new char[fis.available()];
            isr.read(inputBuffer);
            String data = new String(inputBuffer);

            JSONArray arr = new JSONArray(data);

            isr.close();
            fis.close();

            for(int i = 0; i < arr.length(); i++) {
                JSONObject obj_reporte = arr.getJSONObject(i);
                if(obj_reporte.getInt("id") == id_reporte){
                    String str_fecha = obj_reporte.getString("ultima_confirmacion");

                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    return format.parse(str_fecha);
                }
            }

            return null;
        }catch (Exception e){
            return null;
        }
    }

    public static void borrarArchivoJSONReportesConfirmados(Context context){
        context.deleteFile(file_name_reportes_confirmados);
    }

    public static void crearArchivoJSONPreferencias(Context contexto, boolean notificar, boolean vibrar, boolean sonar, boolean gps) {
        try {
            JSONObject pref = new JSONObject();
            pref.put("notificar", notificar);
            pref.put("vibrar", vibrar);
            pref.put("sonar", sonar);
            pref.put("gps", gps);

            FileOutputStream fileos = contexto.openFileOutput(file_name_preferencias, 0);
            fileos.write(pref.toString().getBytes());
            fileos.close();
        }catch (Exception e){

        }
    }

    public static boolean leerArchivoJSONPreferencias(Context contexto, int[] notificar, int[] vibrar, int[] sonar, int[] gps) {
        try {
            FileInputStream fis = contexto.openFileInput(file_name_preferencias);
            InputStreamReader isr = new InputStreamReader(fis);
            char[] inputBuffer = new char[fis.available()];
            isr.read(inputBuffer);
            String data = new String(inputBuffer);

            JSONObject obj = new JSONObject(data);
            boolean bool_notif = obj.getBoolean("notificar");
            boolean bool_vib = obj.getBoolean("vibrar");
            boolean bool_son = obj.getBoolean("sonar");
            boolean bool_gps = obj.getBoolean("gps");

            if(notificar != null) {
                if (bool_notif) {
                    notificar[0] = 1;
                } else {
                    notificar[0] = 0;
                }
            }
            if(vibrar != null) {
                if (bool_vib) {
                    vibrar[0] = 1;
                } else {
                    vibrar[0] = 0;
                }
            }
            if(sonar != null) {
                if (bool_son) {
                    sonar[0] = 1;
                } else {
                    sonar[0] = 0;
                }
            }
            if (gps != null) {
                if(bool_gps){
                    gps[0] = 1;
                } else {
                    gps[0] = 0;
                }
            }

            isr.close();
            fis.close();

            return true;
        }catch (Exception e){
            return false;
        }
    }
}
