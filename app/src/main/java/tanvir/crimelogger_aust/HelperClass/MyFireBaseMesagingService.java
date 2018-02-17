package tanvir.crimelogger_aust.HelperClass;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import tanvir.crimelogger_aust.Activity.PushNotification;
import tanvir.crimelogger_aust.R;

/**
 * Created by USER on 16-Feb-18.
 */

public class MyFireBaseMesagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {

            ///setShareddPrefference(remoteMessage);

            Intent intent = new Intent(this, PushNotification.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("cameFromWhichActivity","push_onMessageReceived");
            intent.putExtra("push_data",remoteMessage.getData().get("push_data"));
            intent.putExtra("push_image_url",remoteMessage.getData().get("push_image_url"));
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
            notificationBuilder.setContentTitle("Farhan FCM");
            notificationBuilder.setContentText(remoteMessage.getNotification().getBody());
            notificationBuilder.setAutoCancel(true);
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
            notificationBuilder.setContentIntent(pendingIntent);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notificationBuilder.build());

        }


    }


}
