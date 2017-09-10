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
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static android.R.attr.data;
import static java.security.AccessController.getContext;

/**
 * Created by garci on 20/8/2017.
 */

public class LocalDB {
    static String file_name_usuario = "userData";
    static String file_name_cortes_avisados = "cortesAvisados";

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

    public static void crearArchivoJSONUsuario(Context contexto, String nombre, String pass, String mail, String token) {
        try {
            JSONObject usuario = new JSONObject();
            usuario.put("nombre", nombre);
            usuario.put("pass", pass);
            usuario.put("mail", mail);
            usuario.put("token", token);

            FileOutputStream fileos = contexto.openFileOutput(file_name_usuario, Context.MODE_PRIVATE);
            fileos.write(usuario.toString().getBytes());
            fileos.close();
        }catch (Exception e){

        }
    }

    public static boolean leerArchivoJSONUsuario(Context contexto, StringBuilder nombre, StringBuilder pass, StringBuilder mail, StringBuilder token) {
        try {
            FileInputStream fis = contexto.openFileInput(file_name_usuario);
            InputStreamReader isr = new InputStreamReader(fis);
            char[] inputBuffer = new char[fis.available()];
            isr.read(inputBuffer);
            String data = new String(inputBuffer);

            JSONObject obj = new JSONObject(data);
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

    public static void crearArchivoJSONCortesAvisados(Context contexto, JSONArray array) {
        try {
            FileOutputStream fileos = contexto.openFileOutput(file_name_cortes_avisados, Context.MODE_PRIVATE);
            fileos.write(array.toString().getBytes());
            fileos.close();
        }catch (Exception e){

        }
    }

    public static void agregarArchivoJSONCortesAvisados(Context contexto, int id_corte) {
        try {
            FileInputStream fis = contexto.openFileInput(file_name_cortes_avisados);
            InputStreamReader isr = new InputStreamReader(fis);
            char[] inputBuffer = new char[fis.available()];
            isr.read(inputBuffer);
            String data = new String(inputBuffer);

            JSONArray arr = new JSONArray(data);
            arr.put(id_corte);

            isr.close();
            fis.close();

            borrarArchivoJSONCortesAvisados(contexto);
            crearArchivoJSONCortesAvisados(contexto, arr);

        }catch (FileNotFoundException e){
            JSONArray arr = new JSONArray();
            arr.put(id_corte);

            crearArchivoJSONCortesAvisados(contexto, arr);
        }catch (Exception e){
            borrarArchivoJSONCortesAvisados(contexto);
        }
    }

    public static boolean estaEnArchivoJSONCortesAvisados(Context contexto, int id_corte) {
        try {
            FileInputStream fis = contexto.openFileInput(file_name_cortes_avisados);
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

}
