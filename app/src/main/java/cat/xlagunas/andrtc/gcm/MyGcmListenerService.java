package cat.xlagunas.andrtc.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import javax.inject.Inject;

import cat.xlagunas.andrtc.CustomApplication;
import cat.xlagunas.andrtc.view.activity.AddContactsActivity;
import cat.xlagunas.andrtc.view.activity.MainActivity;
import xlagunas.cat.andrtc.domain.DefaultSubscriber;
import xlagunas.cat.andrtc.domain.interactor.UpdateProfileUseCase;


public class MyGcmListenerService extends GcmListenerService {

    private static final int FRIENDSHIP_REQUESTED_TYPE = 1;
    private static final int FRIENDSHIP_ACCEPTED_TYPE = 3;
    private static final int FRIENDSHIP_REJECTED_TYPE = 4;
    private static final int CALL_TYPE = 2;

    private static final String TAG = "MyGcmListenerService";

    @Inject
    UpdateProfileUseCase useCase;

    @Override
    public void onCreate() {
        super.onCreate();
        Context context = MyGcmListenerService.this;
        Log.d(TAG, "Context is null? "+context == null ? "yes" : "no");
        if (context != null) {
            CustomApplication.getApp(context).getUserComponent().inject(this);
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
                    }
                });
                break;
            case CALL_TYPE:
                Log.d(TAG, "Call message notification sent");
                sendNotification("Title", "SOMEBODY IS CALLING YOU!");
                break;

            case FRIENDSHIP_ACCEPTED_TYPE:
                useCase.execute(new DefaultSubscriber() {
                    @Override
                    public void onCompleted() {
                        sendFriendshipAcceptedNotification(information);
                    }
                });
                break;
            case FRIENDSHIP_REJECTED_TYPE:
                useCase.execute(new DefaultSubscriber() {
                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        Log.d(TAG, "someone rejected relationship, roster updated");
                    }
                });
        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        // [END_EXCLUDE]
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