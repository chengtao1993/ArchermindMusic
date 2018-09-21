package com.archermind.music;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import com.archermind.music.R;
/*import android.car.export.Car;
import android.car.export.CarHUDManager;
import android.car.export.CarNotConnectedException;*/
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
/*import android.os.SystemProperties;*/
import android.provider.MediaStore.Images;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
/*import android.widget.Toast;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;*/
import com.archermind.music.BtMusicFragment;
import com.archermind.music.MusicFragment;
import com.archermind.music.MusicListFragment;
import com.archermind.music.utils.ScanMusic;
/*import com.archermind.media.photo.PictureFragment;
import com.archermind.media.video.FileInfo;
import com.archermind.media.video.VideoFragment;
import com.archermind.media.video.VideoPlayFragment;
import com.archermind.media.video.VideoUtils;*/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MediaActivity extends FragmentActivity /*implements VideoFragment.OnFragmentInteractionListener,VideoPlayFragment.OnFragmentInteractionListener,View.OnClickListener*/{
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_SETTINGS,
            Manifest.permission.MANAGE_DOCUMENTS,
           /* Manifest.permission.WRITE_MEDIA_STORAGE*/};
    /*public static CarHUDManager mCarHUDManager;*/
    public static int displayTab = 1;
    private int musicTab =1;
   /* private int videoTab =2;
    public static int pictureTab = 3;
    private int videoPlayTab = 4;
    public static int browserTab = 5;
    public static int fillperTab = 6;*/
    public static int musicFragmentTab=5;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    public MusicFragment musicFragment;


    /*private VideoFragment videoFragment;
    private PictureFragment pictureFragment;
    private VideoPlayFragment videoPlayFragment;*/
    private TextView musicView;
    /*private TextView videoView;
    private TextView photoView;
*/
    private Intent intent;
    public static String current_source_name = "本地";
    public static String current_source_path = "external";
    public static HashMap<String,String> name_path = new HashMap();
    private String startFragment;
    private BroadcastReceiver usb_out;
    private String videocontrol;
    private String videosetting;
    private String musicsetting;
    private String musiccontrol;
    private String type;
    public  static int currentPosition =0;

    // CH <BugId:2419> <lizhi> <20180324> modify begin
    private AudioManager audioManager;
    private BluetoothAdapter bluetoothAdapter;
    // CH <BugId:2419> <lizhi> <20180324> modify end

   /* private ArrayList<FileInfo> videoArraryList;*/
    public static boolean isFirstStart=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        //requestPermissions(PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
        checkRequiredPermission(this);
        setContentView(R.layout.activity_media);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // CH <BugId:2419> <lizhi> <20180324> modify begin
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        // CH <BugId:2419> <lizhi> <20180324> modify end
        IntentFilter filter = new IntentFilter("com.archermind.media.USBOUT");
        usb_out = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (name_path.containsKey(intent.getStringExtra("name"))){
                    name_path.remove(intent.getStringExtra("name"));
                    Map.Entry<String,String> entry = name_path.entrySet().iterator().next();
                    current_source_name = entry.getKey();
                    current_source_path = entry.getValue();
                    /*if (displayTab == videoTab){
                        videoFragment.resourceChanged();
                    }else if (displayTab == videoPlayTab) {
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.show(videoFragment).hide(videoPlayFragment);
                        fragmentTransaction.commit();
                        videoFragment.resourceChanged();
                        displayTab = videoTab;
                    }else if (displayTab == pictureTab){
                        pictureFragment.resourceChanged();
                    }else if (displayTab == browserTab){
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container,pictureFragment);
                        fragmentTransaction.commit();
                    }else if (displayTab == fillperTab){
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container,pictureFragment);
                        fragmentTransaction.commit();
                    }*/

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
        /*musicView = findViewById(R.id.music);
        musicView.setOnClickListener(this);*/
        /*videoView = findViewById(R.id.video);
        videoView.setOnClickListener(this);
        photoView = findViewById(R.id.photo);
        photoView.setOnClickListener(this);*/
        musicFragment = new MusicFragment();
        /*videoFragment = new VideoFragment();
        pictureFragment = new PictureFragment();
        videoPlayFragment = new VideoPlayFragment();*/
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
                musicView.setSelected(true);
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
                /*musicView.setSelected(true);*/
                displayTab = musicTab;
                /*SystemProperties.set("service.gr.show","1");*/
            }
        }
       /* initCarService();*/
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
    /*private Car mCar;
    private void initCarService() {
        mCar = Car.createCar(this, mConnection);
        mCar.connect();
    }*/
    public static  String  message;
    private void getMessage(Intent intent) {
        //获得的视频的动作
        Bundle actionBuddle=intent.getExtras();
        Log.i("ccc","actionBuddle"+actionBuddle);
        if(actionBuddle!=null) {
            //music_setting/music_control
            type = actionBuddle.getString("cmd");
            if (type==null){
                return;
            }
            //携带数据到fragment
           /* if (type.equals("video_setting")||type.equals("video_control")) {
                Log.i("ccc","displayTab"+displayTab);
                if(displayTab!=videoPlayTab){
                    videoPlayFragment = null;
                    videoPlayFragment = new VideoPlayFragment();
                    videoPlayFragment.setData(dataList, currentPosition);
                    videoPlayFragment.setArguments(actionBuddle);
                    Log.i("ccc", "actionBuddle1" + actionBuddle);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, videoPlayFragment);
                    transaction.commit();
                    displayTab = videoPlayTab;
                    videoView.setSelected(true);
                    musicView.setSelected(false);
                    photoView.setSelected(false);
                }else {
                    VideoPlayFragment videoplayFragment = (VideoPlayFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    message = actionBuddle.getString(type);
                    Log.i("ccc", "message"+message);
                    videoplayFragment.pauseOrPlay();
                }
                SystemProperties.set("service.gr.show", "2");

            }*/ if (type.equals("music_setting")||type.equals("music_control")) {
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
                   /* musicView.setSelected(true);*/
                    /*photoView.setSelected(false);
                    videoView.setSelected(false);*/
                    displayTab = musicTab;
                   /* SystemProperties.set("service.gr.show", "1");*/
                    /*Log.i("ccc", "///////////////////////////////////////" + SystemProperties.get("service.gr.show"));*/
                }
            }
        }
         /* SystemProperties.get("service.gr.show");*/
    }


    @Override
    protected void onPause() {
        super.onPause();
       /* SystemProperties.set("service.gr.show","-1");*/
//        if (videoPlayFragment != null){
//            if (videoPlayFragment.isVisible()){
//                videoPlayFragment.mVideoPlayController.stop();
//                fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.show(videoFragment).remove(videoPlayFragment);
//                fragmentTransaction.commit();
//                displayTab = videoTab;
//            }
//        }
    }
    @Override
    protected void onStop() {
        super.onStop();
       /* mCar.disconnect();*/

    }
    /*private ArrayList<FileInfo> dataList;*/
    @Override
    protected void onResume() {
        super.onResume();
       /* try {
            dataList = VideoUtils.getDataOrderByTime(this,MediaActivity.current_source_path);
        }catch (Exception e){
            Log.i("ccc","---MediaActivity---"+e);
        }*/

        /*if (videoFragment.isVisible()){
            SystemProperties.set("service.gr.show","2");
        }*/
        if (musicFragment.isVisible()){
           /* SystemProperties.set("service.gr.show","1");*/
        }
        //add by yanglan begin
        /*IntentActionPlay();*/
        //add by yanglan end
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
                   /* musicView.setSelected(true);*/
                   /* photoView.setSelected(false);
                    videoView.setSelected(false);*/
                    displayTab = musicTab;
                    /*SystemProperties.set("service.gr.show","1");*/
                }
            }/*else if (startFragment.equals("photo")){
                fragmentTransaction.replace(R.id.fragment_container,pictureFragment);
                fragmentTransaction.commit();
                photoView.setSelected(true);
                videoView.setSelected(false);
                musicView.setSelected(false);
                displayTab = pictureTab;
                SystemProperties.set("service.gr.show","-1");
            }else if (startFragment.equals("video")){
                if (displayTab == videoTab){
                    videoFragment.resourceChanged();
                }else{
                    fragmentTransaction.replace(R.id.fragment_container,videoFragment);
                    fragmentTransaction.commit();
                    displayTab = videoTab;
                    videoView.setSelected(true);
                    musicView.setSelected(false);
                    photoView.setSelected(false);
                    SystemProperties.set("service.gr.show","2");
                }
            }*/
        }else {
            getMessage(intent);
        }
    }
    private Fragment myFagment;
/*    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.music:
                if (displayTab == musicTab){
                    //not need process
                }else{
                    if (bluetoothAdapter != null
                            && bluetoothAdapter.isEnabled()
                            && bluetoothAdapter.getProfileConnectionState(11) == BluetoothProfile.STATE_CONNECTED) {
                        changeToBTMusic();
                    } else {
                        fragmentTransaction = fragmentManager.beginTransaction();
                        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                        fragmentTransaction.replace(R.id.fragment_container,musicFragment);
                        fragmentTransaction.commit();
                    }
                    displayTab = musicTab;
                    musicView.setSelected(true);
                    videoView.setSelected(false);
                    photoView.setSelected(false);

                    SystemProperties.set("service.gr.show","1");
                }
                break;
            case R.id.video:
                if (displayTab == videoTab || videoPlayFragment.isVisible()){
                    //not need process
                }else{
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container,videoFragment);
                    fragmentTransaction.commit();
                    displayTab = videoTab;
                    videoView.setSelected(true);
                    musicView.setSelected(false);
                    photoView.setSelected(false);
                    SystemProperties.set("service.gr.show","2");
                }
                break;
            case R.id.photo:
                if (displayTab == pictureTab){
                    //not need process
                }else{
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container,pictureFragment);
                    fragmentTransaction.commit();
                    displayTab = pictureTab;
                    photoView.setSelected(true);
                    videoView.setSelected(false);
                    musicView.setSelected(false);
                    SystemProperties.set("service.gr.show","-1");
                }
                break;

        }

    }*/
    public static boolean isBT=false;
    public void changeToBTMusic() {
        isBT=true;
        myFagment = null;
        myFagment = new BtMusicFragment();
        Bundle bundle = new Bundle();
//        bundle.putBoolean("flag",flag2);
        bundle.putString("titleName","蓝牙音乐");
        myFagment.setArguments(bundle);
        FragmentTransaction transaction= getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container,myFagment);
        transaction.commitAllowingStateLoss();
        musicView.setSelected(true);
        /*photoView.setSelected(false);
        videoView.setSelected(false);*/
        displayTab = musicTab;
       /* SystemProperties.set("service.gr.show","1");*/
    }

   /* @Override
    public void onVideoFragmentInteraction(String action, ArrayList<FileInfo> arrayList, int i) {
        if (action.equals("playVideo")){
            if (arrayList.equals(videoArraryList) && currentPosition == i){
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.show(videoPlayFragment).hide(videoFragment);
                fragmentTransaction.commit();
            }else {
                videoArraryList = arrayList;
                currentPosition = i;
                videoPlayFragment.setData(arrayList,i);
                fragmentTransaction = fragmentManager.beginTransaction();
                if (fragmentManager.findFragmentByTag("videoPlayFragment") == null) {
                    fragmentTransaction.add(R.id.fragment_container, videoPlayFragment, "videoPlayFragment");
                    fragmentTransaction.show(videoPlayFragment).hide(videoFragment);
                    fragmentTransaction.commit();
                }else {
                    fragmentTransaction.show(videoPlayFragment).hide(videoFragment);
                    fragmentTransaction.commit();
                    videoPlayFragment.startPlaying();
                }
            }
            displayTab = videoPlayTab;
            Log.e("ccc", "videoPlayTab生效了!");

        }
    }*/

   /* @Override
    public void onVideoPlayFragmentInteraction(String action) {
        if (action.equals("VideoList")){
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.show(videoFragment).hide(videoPlayFragment);
            fragmentTransaction.commit();
            displayTab = videoTab;
        }
    }*/


    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
           /* try {
                mCarHUDManager=(CarHUDManager)mCar.getCarManager(Car.HUD_SERVICE);
            } catch (CarNotConnectedException e) {
                e.printStackTrace();
            }*/
            Log.e("ccc", "Car is connected!");

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e("ccc", "Car service is disconnected");
        }
    };

    // CH <BugId:2419> <lizhi> <20180324> modify begin
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
    // CH <BugId:2419> <lizhi> <20180324> modify end
    //add by yanglan begin
/*    private void IntentActionPlay() {
    	if(getIntent() != null && getIntent().getData() != null) {
            if (displayTab == videoTab){
                //not need process
            }else{
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container,videoFragment);
                fragmentTransaction.commit();
                displayTab = videoTab;
                videoView.setSelected(true);
                musicView.setSelected(false);
                photoView.setSelected(false);
                SystemProperties.set("service.gr.show","2");
            }
            FileInfo info = new FileInfo();
            info.isFile = true;
            info.uri = intent.getData();
            info.name = getFileName(this, info.uri);
            ArrayList<FileInfo> arrayList = new ArrayList<FileInfo>();
            arrayList.add(info);
            onVideoFragmentInteraction("playVideo", arrayList, 0);
        }
    }*/


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
    //add by yanglan end
}
