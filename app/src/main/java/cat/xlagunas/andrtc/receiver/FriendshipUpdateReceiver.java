package cat.xlagunas.andrtc.receiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import cat.xlagunas.andrtc.R;
import cat.xlagunas.andrtc.view.activity.AddContactsActivity;
import cat.xlagunas.andrtc.view.activity.MainActivity;

/**
 * Created by xlagunas on 10/9/16.
 */
public class FriendshipUpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String type = intent.getStringExtra("type");

        if ("requested".equalsIgnoreCase(type)) {
            sendFriendshipRequestNotification(context, intent.getBundleExtra("information"));
        } else if ("accepted".equalsIgnoreCase(type)) {
            sendFriendshipAcceptedNotification(context, intent.getBundleExtra("information"));
        }
    }

    private void sendFriendshipRequestNotification(Context context, Bundle data) {
        String title = context.getString(R.string.notification_requested_title);
        String message = context.getString(R.string.notification_requested_text,
                data.getString("username"), data.getString("name"));

        sendNotification(context, title, message, AddContactsActivity.class);
    }

    private void sendFriendshipAcceptedNotification(Context context, Bundle data) {
        String title = context.getString(R.string.notification_updated_title);
        String message = context.getString(R.string.notification_requested_text,
                data.getString("username"), data.getString("name"));

        sendNotification(context, title, message);
    }


    private void sendNotification(Context context, String title, String message) {
        sendNotification(context, title, message, MainActivity.class);
    }

    private void sendNotification(Context context, String title, String message, Class pendingActivity) {
        Intent intent = new Intent(context, pendingActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }


}
