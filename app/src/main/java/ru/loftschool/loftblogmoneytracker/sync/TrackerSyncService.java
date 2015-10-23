package ru.loftschool.loftblogmoneytracker.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;


public class TrackerSyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static TrackerSyncAdapter sTrackerSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sTrackerSyncAdapter == null) {
                sTrackerSyncAdapter = new TrackerSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return sTrackerSyncAdapter.getSyncAdapterBinder();
    }
}
