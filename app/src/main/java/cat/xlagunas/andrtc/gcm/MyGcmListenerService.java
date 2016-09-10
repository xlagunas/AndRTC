package cat.xlagunas.andrtc.gcm;

import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import javax.inject.Inject;

import cat.xlagunas.andrtc.CustomApplication;
import cat.xlagunas.andrtc.data.cache.UserCache;
import cat.xlagunas.andrtc.di.components.UserComponent;
import cat.xlagunas.andrtc.view.activity.CallRequestActivity;
import xlagunas.cat.andrtc.domain.DefaultSubscriber;
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

        UserComponent component = CustomApplication.getApp(MyGcmListenerService.this).getUserComponent();

        component.inject(this);

    }

    @Override
    public void onMessageReceived(String from, Bundle data) {
        int messageType = Integer.parseInt(data.getString("type"));

        Log.d(TAG, "From: " + from + "type: " + messageType);

        final Bundle information = data;

        switch (messageType) {
            case FRIENDSHIP_REQUESTED_TYPE:
                useCase.execute(new DefaultSubscriber() {
                    @Override
                    public void onCompleted() {
                        generateAndSendBroadcastMessage("requested", information);
                    }
                });
                break;
            case FRIENDSHIP_ACCEPTED_TYPE:
                useCase.execute(new DefaultSubscriber() {
                    @Override
                    public void onCompleted() {
                        generateAndSendBroadcastMessage("accepted", information);
                    }
                });
                break;
            case FRIENDSHIP_REJECTED_TYPE:
                useCase.execute(new DefaultSubscriber() {
                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        Log.d(TAG, "someone rejected relationship, roster updated");
                        generateAndSendBroadcastMessage("rejected", information);
                    }
                });
                break;
            case FRIENDSHIP_DELETED_TYPE:
                useCase.execute(new DefaultSubscriber() {
                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        Log.d(TAG, "Someone deleted relationship, roster updated");
                        generateAndSendBroadcastMessage("deleted", information);
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

    private void generateAndSendBroadcastMessage(String messageType, Bundle information) {
        Intent intent = new Intent("CONTACTS_UPDATE");
        intent.putExtra("type", messageType);
        intent.putExtra("information", information);

        sendOrderedBroadcast(intent, null);
    }
}