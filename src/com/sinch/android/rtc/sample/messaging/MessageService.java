package com.sinch.android.rtc.sample.messaging;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.sinch.android.rtc.*;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.WritableMessage;

public class MessageService extends Service implements SinchClientListener {

    private static final String APP_KEY = "enter-application-key";

    private static final String APP_SECRET = "enter-application-secret";

    private static final String ENVIRONMENT = "sandbox.sinch.com";

    public static final String INTENT_EXTRA_USERNAME = "intentExtraUsername";

    private static final String TAG = MessageService.class.getSimpleName();

    private final MessageServiceInterface mServiceInterface = new MessageServiceInterface();

    private SinchClient mSinchClient = null;

    private MessageClient mMessageClient = null;

    public class MessageServiceInterface extends Binder {

        public void sendMessage(String recipientUserId, String textBody) {
            MessageService.this.sendMessage(recipientUserId, textBody);
        }

        public void addMessageClientListener(MessageClientListener listener) {
            MessageService.this.addMessageClientListener(listener);
        }

        public void removeMessageClientListener(MessageClientListener listener) {
            MessageService.this.removeMessageClientListener(listener);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mServiceInterface;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        stop();
        stopSelf();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        stop();
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String username = intent.getStringExtra(INTENT_EXTRA_USERNAME);

        if (username != null && !isSinchClientStarted()) {
            startSinchClient(username);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private boolean isSinchClientStarted() {
        return mSinchClient != null && mSinchClient.isStarted();
    }

    public void sendMessage(String recipientUserId, String textBody) {
        if (mMessageClient != null) {
            WritableMessage message = new WritableMessage(recipientUserId, textBody);
            mMessageClient.send(message);
        }
    }

    public void addMessageClientListener(MessageClientListener listener) {
        if (mMessageClient != null) {
            mMessageClient.addMessageClientListener(listener);
        }
    }

    public void removeMessageClientListener(MessageClientListener listener) {
        if (mMessageClient != null) {
            mMessageClient.removeMessageClientListener(listener);
        }
    }

    public void startSinchClient(String userName) {
        mSinchClient = Sinch.getSinchClientBuilder().context(this).userId(userName).applicationKey(APP_KEY)
                .applicationSecret(APP_SECRET).environmentHost(ENVIRONMENT).build();

        mSinchClient.addSinchClientListener(this);

        mSinchClient.setSupportMessaging(true);
        mSinchClient.setSupportActiveConnectionInBackground(true);
        mSinchClient.startListeningOnActiveConnection();
        mMessageClient = mSinchClient.getMessageClient();

        mSinchClient.checkManifest();
        mSinchClient.start();
    }

    public void stop() {
        if (isSinchClientStarted()) {
            mSinchClient.removeSinchClientListener(this);
            mSinchClient.terminate();
        }
        mMessageClient = null;
        mSinchClient = null;
    }

    @Override
    public void onClientFailed(SinchClient client, SinchError error) {
        Log.e(TAG, "SinchClient error: " + error);
        Toast.makeText(this, "SinchClient error: " + error, Toast.LENGTH_LONG).show();
        mSinchClient = null;
    }

    @Override
    public void onClientStarted(SinchClient client) {
        Log.d(TAG, "SinchClient started");
        Intent intent = new Intent(this, MessagingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onClientStopped(SinchClient client) {
        Log.d(TAG, "SinchClient stopped");
    }


    @Override
    public void onLogMessage(int level, String area, String message) {
        switch (level) {
            case Log.DEBUG:
                Log.d(area, message);
                break;
            case Log.ERROR:
                Log.e(area, message);
                break;
            case Log.INFO:
                Log.i(area, message);
                break;
            case Log.VERBOSE:
                Log.v(area, message);
                break;
            case Log.WARN:
                Log.w(area, message);
                break;
        }
    }

    @Override
    public void onRegistrationCredentialsRequired(SinchClient client, ClientRegistration clientRegistration) {
        //Left intentionally blank
    }
}
