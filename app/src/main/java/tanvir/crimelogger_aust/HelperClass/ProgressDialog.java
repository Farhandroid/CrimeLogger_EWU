package tanvir.crimelogger_aust.HelperClass;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import tanvir.crimelogger_aust.R;

public class ProgressDialog {

    AlertDialog alertDialog;
    AlertDialog.Builder dialogBuilder;
    Context context;

    public AlertDialog getAlertDialog() {
        return alertDialog;
    }
    public ProgressDialog(Context context) {
        this.context = context;
    }

    public void showProgressDialog() {
        dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View dialogView = inflater.inflate(R.layout.progress_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        alertDialog = dialogBuilder.create();
        alertDialog.show();
    }
    public void showProgressDialog(String message) {
        dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View dialogView = inflater.inflate(R.layout.progress_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setMessage(message);
        dialogBuilder.setCancelable(false);
        alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    public void hideProgressDialog(){
        alertDialog.dismiss();
    }
}
