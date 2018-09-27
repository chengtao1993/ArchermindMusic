package com.archermind.music;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore.Images;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.WindowManager;
import com.archermind.music.utils.ScanMusic;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MediaActivity extends FragmentActivity {
    public static int displayTab = 1;
    private int musicTab =1;
    public static int musicFragmentTab=5;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    public MusicFragment musicFragment;
    private Intent intent;
    public static String current_source_name = "本地";
    public static String current_source_path = "external";
    public static HashMap<String,String> name_path = new HashMap();
    private String startFragment;
    private BroadcastReceiver usb_out;
    private String type;
    private AudioManager audioManager;
    private BluetoothAdapter bluetoothAdapter;
    public static boolean isFirstStart=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        checkRequiredPermission(this);
        setContentView(R.layout.activity_media);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        IntentFilter filter = new IntentFilter("com.archermind.media.USBOUT");
        usb_out = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (name_path.containsKey(intent.getStringExtra("name"))){
                    name_path.remove(intent.getStringExtra("name"));
                    Map.Entry<String,String> entry = name_path.entrySet().iterator().next();
                    current_source_name = entry.getKey();
                    current_source_path = entry.getValue();
                    if (musicFragmentTab == 5){//对应的是MusicFragment
                        if (musicFragment.data_source.getText().equals(intent.getStringExtra("name"))){
                            musicFragment.resourceChanged();
                        }
                    }else if (musicFragmentTab == 6){//对应的是MusicListFragment
                        MusicListFragment.fileInfo = ScanMusic.getData(MediaActivity.this,MediaActivity.current_source_path);
                        MusicListFragment.usbPullOut();
                    }
                }

            }
        };
        registerReceiver(usb_out,filter);
        name_path.put("本地","external");
        musicFragment = new MusicFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        intent = getIntent();
        startFragment = intent.getStringExtra("startFragment");
        if (startFragment != null){
            current_source_name = intent.getStringExtra("name");
            current_source_path = intent.getStringExtra("path");
            if (!name_path.containsKey(current_source_name)) {
                name_path.put(current_source_name, current_source_path);
            }
            if (startFragment.equals("music")){
                fragmentTransaction.add(R.id.fragment_container,musicFragment);
                fragmentTransaction.commit();
                displayTab = musicTab;
                /*SystemProperties.set(service.gr.show","1");*/
            }
        }else {
            if (bluetoothAdapter != null
                    && bluetoothAdapter.isEnabled()
                    && bluetoothAdapter.getProfileConnectionState(11) == BluetoothProfile.STATE_CONNECTED) {
                changeToBTMusic();
            } else {
                musicFragment.setArguments(intent.getExtras());
                fragmentTransaction.add(R.id.fragment_container, musicFragment);
                fragmentTransaction.commit();
                displayTab = musicTab;

            }
        }
        getMessage(this.getIntent());
    }
    private String[] permissionsArray=new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_SETTINGS,
            Manifest.permission.MANAGE_DOCUMENTS
    };
    private List<String> permissionList=new ArrayList<String>();
    //申请权限后的返回码
    public final int REQUEST_CODE_ASK_PERMISSIONS=1;
    private int number=0;
    private void checkRequiredPermission(Activity activity){
        for (String permission: permissionsArray) {
            if(ContextCompat.checkSelfPermission(activity,permission)!= PackageManager.PERMISSION_GRANTED){
                permissionList.add(permission);
            }
        }
        if (permissionList.size()>0) {
            number = permissionList.size();
            ActivityCompat.requestPermissions(activity, permissionList.toArray(new String[permissionList.size()]), REQUEST_CODE_ASK_PERMISSIONS);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        switch (requestCode){
            case REQUEST_CODE_ASK_PERMISSIONS:

                break;
            default:
                super.onRequestPermissionsResult(requestCode,permissions,grantResults);

        }
    }
    public static  String  message;
    private void getMessage(Intent intent) {
        //获得的视频的动作
        Bundle actionBuddle=intent.getExtras();
        Log.i("ccc","actionBuddle"+actionBuddle);
        if(actionBuddle!=null) {
            type = actionBuddle.getString("cmd");
            if (type==null){
                return;
            }
            //携带数据到fragment
       if (type.equals("music_setting")||type.equals("music_control")) {
                if(displayTab==musicTab){
                    Log.i("ccc", "actionBuddle4" + actionBuddle);
                    message = actionBuddle.getString(type);
                    MusicFragment musicfg = (MusicFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    try {
                        musicfg.receiveMessageAndSetData();
                    }catch (Exception e){
                        Log.i("ccc", "33333" + e);
                    }
                }else {
                    musicFragment = null;
                    musicFragment = new MusicFragment();
                    musicFragment.setArguments(actionBuddle);
                    Log.i("ccc", "actionBuddle2" + actionBuddle);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, musicFragment);
                    transaction.commit();
                    displayTab = musicTab;
                }
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (usb_out!=null)
                unregisterReceiver(usb_out);
               unbindService(mConnection);
        }catch (Exception e){
            Log.i("media",""+e);
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //根据intent的类型来进行选择
        fragmentTransaction = fragmentManager.beginTransaction();
        startFragment = intent.getStringExtra("startFragment");
        if (startFragment != null){
            current_source_name = intent.getStringExtra("name");
            current_source_path = intent.getStringExtra("path");
            if (!name_path.containsKey(current_source_name)) {
                name_path.put(current_source_name, current_source_path);
            }
            if (startFragment.equals("music")){
                if (musicFragmentTab == 5){
                    musicFragment.resourceChanged();
                }else {
                    fragmentTransaction.replace(R.id.fragment_container, musicFragment);
                    fragmentTransaction.commit();
                    displayTab = musicTab;

                }
            }
        }else {
            getMessage(intent);
        }
    }
    private Fragment myFagment;

    public static boolean isBT=false;
    public void changeToBTMusic() {
        isBT=true;
        myFagment = null;
        myFagment = new BtMusicFragment();
        Bundle bundle = new Bundle();
        bundle.putString("titleName","蓝牙音乐");
        myFagment.setArguments(bundle);
        FragmentTransaction transaction= getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container,myFagment);
        transaction.commitAllowingStateLoss();
        displayTab = musicTab;
    }



    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.e("ccc", "Car is connected!");

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e("ccc", "Car service is disconnected");
        }
    };

    public int requestAudioFocus(AudioManager.OnAudioFocusChangeListener l, int streamType, int durationHint) {
        if (audioManager == null) return -1;
        return audioManager.requestAudioFocus(
                l,
                streamType,
                durationHint);
    }

    public int abandonAudioFocus(AudioManager.OnAudioFocusChangeListener l) {
        if (audioManager == null) return -1;
        return audioManager.abandonAudioFocus(l);
    }

    public void setVoiceVolume(float value) {
        try {
            int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);//(最大值是15)
            int flag = value > 0 ? -1 : 1;
            currentVolume += flag * 0.1 * maxVolume;
            // 对currentVolume进行限制
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getFileName(final Context context, final Uri uri) {
        if (null == uri) 
            return null;
        //
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme) ) {
            Cursor cursor = context.getContentResolver().query(uri, new String[] {Images.ImageColumns.DATA }, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(Images.ImageColumns.DATA);
                    if (index > -1){
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        //
        if(data == null){
            data = uri.toString();
        }
        String fileName = data.substring(data.lastIndexOf("/") + 1, data.length());
        return fileName;
    }

}
