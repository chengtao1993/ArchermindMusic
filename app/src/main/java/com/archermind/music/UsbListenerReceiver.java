package com.archermind.music;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;


public class UsbListenerReceiver extends BroadcastReceiver {
    private String action;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null){
            action = intent.getAction();
        }
        if (action != null && action.equals("android.intent.action.UsbListenerReceiver")){
            String name = intent.getStringExtra("name");
            String path = intent.getStringExtra("path");
            if (!MediaActivity.name_path.containsKey(name)){
                MediaActivity.name_path.put(name,path);
            }
        }else {
            Intent usb_out = new Intent();
            usb_out.setAction("com.archermind.media.USBOUT");
            usb_out.putExtra("name", intent.getStringExtra("name"));
            context.sendBroadcast(usb_out);
        }

    }


}
