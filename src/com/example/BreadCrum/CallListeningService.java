package com.example.BreadCrum;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


public class CallListeningService extends Service {
    private CallHelper callHelper;
    public CallListeningService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        callHelper = new CallHelper(this);

        int res = super.onStartCommand(intent, flags, startId);
        callHelper.start();
        return res;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        callHelper.stop();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // not supporting binding
        return null;
    }
}
