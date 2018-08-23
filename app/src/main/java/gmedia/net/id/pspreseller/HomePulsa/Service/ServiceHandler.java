package gmedia.net.id.pspreseller.HomePulsa.Service;

import android.app.Activity;
import android.content.Intent;

import com.maulana.custommodul.ItemValidation;

public class ServiceHandler {

    public static Intent intent;
    private ItemValidation iv = new ItemValidation();

    public ServiceHandler(Activity activity){

        if(!iv.isServiceRunning(activity.getApplicationContext(), USSDService.class)){
            intent = new Intent(activity.getApplicationContext(), USSDService.class);
            activity.startService(intent);
        }
    }
}
