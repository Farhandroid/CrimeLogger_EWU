package tanvir.crimelogger_aust.HelperClass;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by USER on 16-Feb-18.
 */

public class MyFireBaseInsTanceIdService extends FirebaseInstanceIdService {

    private static final String REG_TOKEN="REG_TOKEN";

    @Override
    public void onTokenRefresh() {
        String recent_token = FirebaseInstanceId.getInstance().getToken();
        Log.d(REG_TOKEN,recent_token);
    }
}
