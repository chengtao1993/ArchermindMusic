package com.archermind.music;

import android.app.Service;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.IBinder;
import android.os.storage.StorageManager;
import android.support.annotation.IntDef;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Iterator;

public class UsbListenerService extends Service {
    private UsbManager usbManager;
    public UsbListenerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        String str = intent.getStringExtra("usb");
        if (usbManager == null){
            usbManager = (UsbManager) getSystemService(USB_SERVICE);
        }
        if (("in").equals(str)){
            Toast.makeText(this,"in",Toast.LENGTH_SHORT).show();
        }else if (("out").equals(str)){
            Toast.makeText(this,"out",Toast.LENGTH_SHORT).show();
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
