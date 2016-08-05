package cat.xlagunas.andrtc.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import javax.inject.Inject;

import cat.xlagunas.andrtc.CustomApplication;
import cat.xlagunas.andrtc.data.cache.UserCache;
import cat.xlagunas.andrtc.di.components.UserComponent;
import cat.xlagunas.andrtc.view.activity.AddContactsActivity;
import cat.xlagunas.andrtc.view.activity.CallRequestActivity;
import cat.xlagunas.andrtc.view.activity.MainActivity;
import xlagunas.cat.andrtc.domain.DefaultSubscriber;
import xlagunas.cat.andrtc.domain.User;
import xlagunas.cat.andrtc.domain.interactor.UpdateProfileUseCase;


public class MyGcmListenerService extends GcmListenerService {

    private static final int FRIENDSHIP_REQUESTED_TYPE = 1;
    private static final int FRIENDSHIP_ACCEPTED_TYPE = 2;
    private static final int FRIENDSHIP_REJECTED_TYPE = 3;
    private static final int FRIENDSHIP_DELETED_TYPE = 4;

    private static final int CALL_RECEIVED_TYPE = 100;
    private static final int CALL_ACCEPTED_TYPE = 101;

    private static final String TAG = "MyGcmListenerService";
    public static final String BROADCAST_CALL_ACCEPTED = "call_accepted";

    @Inject
    UpdateProfileUseCase useCase;
    @Inject
    UserCache userCache;

    @Override
    public void onCreate() {
        super.onCreate();
        Context context = MyGcmListenerService.this;
        Log.d(TAG, "Context is null? "+context == null ? "yes" : "no");
        if (context != null) {
            UserComponent component = CustomApplication.getApp(context).getUserComponent();

            component.inject(this);
        }
    }

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        int messageType = Integer.parseInt(data.getString("type"));

        Log.d(TAG, "From: " + from + "type: "+messageType);

        final Bundle information = data;

        switch (messageType) {
            case FRIENDSHIP_REQUESTED_TYPE:
                useCase.execute(new DefaultSubscriber() {
                    @Override
                    public void onCompleted() {
                        sendFriendshipRequestNotification(information);
                        LocalBroadcastManager.getInstance(MyGcmListenerService.this)
                                .sendBroadcast(new Intent("UPDATE_USERS"));
                    }
                });
                break;
            case FRIENDSHIP_ACCEPTED_TYPE:
                useCase.execute(new DefaultSubscriber() {
                    @Override
                    public void onCompleted() {
                        sendFriendshipAcceptedNotification(information);
                        LocalBroadcastManager.getInstance(MyGcmListenerService.this)
                                .sendBroadcast(new Intent("UPDATE_USERS"));
                    }
                });
                break;
            case FRIENDSHIP_REJECTED_TYPE:
                useCase.execute(new DefaultSubscriber() {
                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        Log.d(TAG, "someone rejected relationship, roster updated");
                        LocalBroadcastManager.getInstance(MyGcmListenerService.this)
                                .sendBroadcast(new Intent("UPDATE_USERS"));
                    }
                });
                break;
            case FRIENDSHIP_DELETED_TYPE:
                useCase.execute(new DefaultSubscriber(){
                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        Log.d(TAG, "Someone deleted relationship, roster updated");
                        LocalBroadcastManager.getInstance(MyGcmListenerService.this)
                                .sendBroadcast(new Intent("UPDATE_USERS"));
                    }
                });
                break;
            case CALL_RECEIVED_TYPE:
                Log.d(TAG, "Call message notification sent");
                startActivity(CallRequestActivity
                        .makeCalleeIntent(MyGcmListenerService.this,
                                data.getString("callerId"),
                                data.getString("roomId")));
                break;
            case CALL_ACCEPTED_TYPE:
                Log.d(TAG, "Requestee accepted the call");
                Intent intent = new Intent(BROADCAST_CALL_ACCEPTED);
                intent.putExtra("roomId", data.getString("roomId"));
                LocalBroadcastManager.getInstance(MyGcmListenerService.this)
                        .sendBroadcast(intent);
                break;


        }


    }

    private void sendFriendshipAcceptedNotification(Bundle data) {
        String title = "You have a new Contact";
        String message = String.format("%s (%s) has accepted your friendship request",
                data.getString("username"), data.getString("name"));
        sendNotification(title, message);
    }

    private void sendFriendshipRequestNotification(Bundle data) {
        String title = "New friendship request";
        String message = String.format("%s (%s) wants to be friends with you",
                data.getString("username"), data.getString("name"));
        sendNotification(title, message, AddContactsActivity.class);
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String title, String message) {
        sendNotification(title, message, MainActivity.class);
    }

    private void sendNotification(String title, String message, Class pendingActivity) {
        Intent intent = new Intent(this, pendingActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}