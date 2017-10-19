package losmarinos.blackout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.Toast;

/**
 * Created by garci on 19/10/2017.
 */

public class Aviso {

    public static void showToast(final Activity activity, final String toast)
    {
        activity.runOnUiThread(new Runnable() {
            public void run()
            {
                Toast.makeText(activity, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static ProgressDialog showProgressDialog(final Activity activity, String text){

        final ProgressDialog progress_dialog = new ProgressDialog(activity);
        progress_dialog.setIndeterminate(true);
        progress_dialog.setMessage(text);
        progress_dialog.setCanceledOnTouchOutside(false);
        progress_dialog.show();

        activity.runOnUiThread(new Runnable() {
            public void run()
            {
                progress_dialog.show();
            }
        });

        return progress_dialog;
    }
    public static void hideProgressDialog(final Activity activity, final ProgressDialog progress_dialog){
        activity.runOnUiThread(new Runnable() {
            public void run()
            {
                if(progress_dialog.isShowing()) {
                    progress_dialog.dismiss();
                }
            }
        });
    }
}
