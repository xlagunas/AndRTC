package cat.xlagunas.andrtc.gcm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import javax.inject.Inject;

import cat.xlagunas.andrtc.CustomApplication;
import cat.xlagunas.andrtc.data.cache.UserCache;
import cat.xlagunas.andrtc.di.components.UserComponent;
import cat.xlagunas.andrtc.view.activity.CallRequestActivity;
import timber.log.Timber;
import xlagunas.cat.andrtc.domain.DefaultSubscriber;
import xlagunas.cat.andrtc.domain.interactor.UpdateProfileUseCase;

public class MyGcmListenerService extends FirebaseMessagingService {

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

        UserComponent component = CustomApplication.getApp(MyGcmListenerService.this).getUserComponent();

        component.inject(this);

    }

    @Override
    public void onMessageReceived(RemoteMessage message) {

        int messageType = Integer.parseInt(message.getData().get("type"));

        Timber.d("From: " + message.getFrom() + "type: " + messageType);

        Bundle information = convertToBundle(message);

        switch (messageType) {
            case FRIENDSHIP_REQUESTED_TYPE:
                executeUseCase("requested", information);
                break;
            case FRIENDSHIP_ACCEPTED_TYPE:
                executeUseCase("accepted", information);
                break;
            case FRIENDSHIP_REJECTED_TYPE:
                executeUseCase("rejected", information);
                break;
            case FRIENDSHIP_DELETED_TYPE:
                executeUseCase("deleted", information);
                break;
            case CALL_RECEIVED_TYPE:
                Timber.d("Call message notification sent");
                startActivity(CallRequestActivity
                        .makeCalleeIntent(MyGcmListenerService.this,
                                message.getData().get("callerId"),
                                message.getData().get("roomId")));
                break;
            case CALL_ACCEPTED_TYPE:
                Timber.d("Requestee accepted the call");
                Intent intent = new Intent(BROADCAST_CALL_ACCEPTED);
                intent.putExtra("roomId", message.getData().get("roomId"));
                LocalBroadcastManager.getInstance(MyGcmListenerService.this)
                        .sendBroadcast(intent);
                break;
        }

    }

    private Bundle convertToBundle(RemoteMessage message) {
        //TODO FIX TO MANTAIN COMPATIBILITY FROM GCM
        Bundle bundle = new Bundle();
        for (String key : message.getData().keySet()) {
            bundle.putString(key, message.getData().get(key));
        }
        return bundle;
    }

    private void executeUseCase(String messageType, Bundle information) {
        useCase.execute(new DefaultSubscriber() {
            @Override
            public void onCompleted() {
                generateAndSendBroadcastMessage(messageType, information);
                Timber.d("Push notification received, from type: " + messageType);
            }
        });
    }

    private void generateAndSendBroadcastMessage(String messageType, Bundle information) {
        Intent intent = new Intent("CONTACTS_UPDATE");
        intent.putExtra("type", messageType);
        intent.putExtra("information", information);

        sendOrderedBroadcast(intent, null);
    }
}