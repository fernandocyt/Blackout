package losmarinos.blackout;

import android.content.Context;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by garci on 7/10/2017.
 */

public class Validador {

    public static boolean validarPasswords(Context context, String pass1, String pass2){
        if(!pass1.equals(pass2)){
            Toast.makeText(context, "Las claves no coinciden", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public static boolean validarCamposVacios(Context context, LinearLayout layout){
        for (int i = 0; i < layout.getChildCount(); i++){
            Object child = layout.getChildAt(i);
            if (child instanceof EditText){
                // EDIT TEXT
                EditText e = (EditText)child;
                if(e.getText().toString().isEmpty()){
                    Toast.makeText(context, "Debe completar todos los campos", Toast.LENGTH_LONG).show();
                    return false;
                }
            }else if(child instanceof LinearLayout){
                // LAYOUT
                if(!validarCamposVacios(context, (LinearLayout)child)){
                    return false;
                }
            }else if(child instanceof TableLayout) {
                // TABLA
                TableLayout tabla = (TableLayout) child;
                for (int j = 0; j < tabla.getChildCount(); j++) {
                    if (tabla.getChildAt(j) instanceof TableRow) {
                        if (!validarCamposVacios(context, (TableRow) tabla.getChildAt(j))) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public static boolean validarCamposVacios(Context context, TableRow table_row){
        for (int i = 0; i < table_row.getChildCount(); i++){
            Object child = table_row.getChildAt(i);
            if (child instanceof EditText){
                EditText e = (EditText)child;
                if(e.getText().toString().isEmpty()){
                    Toast.makeText(context, "Debe completar todos los campos", Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean validarMail(Context context, String mail){
        int pos = mail.indexOf(".");
        if(pos == -1){
            Toast.makeText(context, "Falta el punto en el mail", Toast.LENGTH_LONG).show();
            return false;
        }
        pos = mail.indexOf("@");
        if(pos == -1){
            Toast.makeText(context, "Falta el @ en el mail", Toast.LENGTH_LONG).show();
            return false;
        }
        String[] separado_arroba = mail.split("\\@");
        if(separado_arroba[0].length() <= 4){
            Toast.makeText(context, "La primer parte del mail debe contener al menos 5 caracteres", Toast.LENGTH_LONG).show();
            return false;
        }
        /*String[] separado_punto = separado_arroba[1].split(".");
        if(separado_punto[0].length() <= 4){
            Toast.makeText(context, "La segunda parte del mail debe contener al menos 5 caracteres", Toast.LENGTH_LONG).show();
            return false;
        }*/

        return true;
    }
}
