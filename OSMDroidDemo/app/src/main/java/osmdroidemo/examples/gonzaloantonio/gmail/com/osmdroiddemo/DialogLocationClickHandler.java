package osmdroidemo.examples.gonzaloantonio.gmail.com.osmdroiddemo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;

class DialogLocationClickHandler implements DialogInterface.OnClickListener {
    boolean openSettings;
    Context context;

    public DialogLocationClickHandler (Context context, boolean openSettings) {
        this.openSettings = openSettings;
    }

    public void onClick (DialogInterface dialogInterface, int id) {
        if (openSettings) {
            Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
            context.startActivity(intent);
        }
        dialogInterface.cancel ();
    }
}
