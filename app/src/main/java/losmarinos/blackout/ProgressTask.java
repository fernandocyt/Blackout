package losmarinos.blackout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by Fernando on 7/10/2017.
 */

public class ProgressTask extends AsyncTask <Void, Void, Void> {
    private ProgressDialog dialog;

    public ProgressTask(Context activity) {
        dialog = new ProgressDialog(activity);
    }

    @Override
    protected void onPreExecute() {
            dialog.setMessage("Cargando...");
            dialog.show();
    }

    @Override
    protected void onPostExecute(Void result) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }
}