package com.calmbox.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent servicio = new Intent(context, CalmService.class);

        context.startService(servicio);
    }
}
