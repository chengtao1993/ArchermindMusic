package com.archermind.music;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadata;
import android.media.browse.MediaBrowser;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.archermind.music.MediaActivity;
import com.archermind.music.R;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static com.archermind.music.MediaActivity.displayTab;

/**
 * Created by root on 18-2-27.
 */

public class BtMusicFragment extends Fragment implements View.OnClickListener {

    private final static String TAG = "Media_BtMusicFragment";


    public static String artist;
    public static String mMusicName;
    private PlaybackState mState;
    private View view;
    private ImageButton changeBt,mPreMusic,mPlayMusic,mNextMusic;
    private Context mContext = null;
    private MediaBrowser mMediaBrowser = null;
    public TextView btMusicSonger,btMusicName;
    private TextView mBtTextView;

    private boolean flag;
    private MediaSession.Token token = null;
    private MediaController mediaController = null;
    private MediaController.TransportControls transportControls = null;

    private AlertDialog sourceDialog;
    private TextView data_source;

    public BtMusicFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_btmusic, container, false);
        initView();
        getPlayState();
        btMusicName.setText(mMusicName);
        btMusicSonger.setText(artist);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: ");
        MediaActivity.isBT=true;
        MediaActivity.musicFragmentTab=8;
        mBtTextView.setText("蓝牙音乐");
    }
    @Override
    public void onStop() {
        Log.i(TAG, "onStop: ");
        super.onStop();
        MediaActivity.isBT=false;
    }

    private void initView(){

        btMusicName = (TextView) view.findViewById(R.id.bt_music_name);
        btMusicSonger = (TextView) view.findViewById(R.id.Songer);
        changeBt = (ImageButton) view.findViewById(R.id.ib_change);
        mPreMusic = (ImageButton) view.findViewById(R.id.music_pre);
        mPlayMusic = (ImageButton) view.findViewById(R.id.btMusic_play);
        mNextMusic = (ImageButton) view.findViewById(R.id.music_next);

        mBtTextView = (TextView)view.findViewById(R.id.bt_text_change);
        mBtTextView.setOnClickListener(this);


        changeBt.setOnClickListener(this);
        mPreMusic.setOnClickListener(this);
        mPlayMusic.setOnClickListener(this);
        mNextMusic.setOnClickListener(this);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.music_pre:
                if (transportControls != null) {
                    transportControls.skipToPrevious();
                }
                break;

            case R.id.btMusic_play:
                PlaybackState playbackState = mState;
                Log.i(TAG,"playbackState"+playbackState);
                if (playbackState == null) {
                    transportControls.play();
                    mPlayMusic.setSelected(true);
                    break;
                }
                long transportControlFlags = playbackState.getActions();
                if (playbackState.getState() == PlaybackState.STATE_PLAYING) {
                    if ((transportControlFlags & PlaybackState.ACTION_PAUSE) != 0) {
                        transportControls.pause();
                        mPlayMusic.setSelected(false);
                    } else if ((transportControlFlags & PlaybackState.ACTION_STOP) != 0) {
                        transportControls.stop();
                        mPlayMusic.setSelected(false);
                    }
                } else if (playbackState.getState() == PlaybackState.STATE_BUFFERING) {
                    if ((transportControlFlags & PlaybackState.ACTION_STOP) != 0) {
                        transportControls.stop();
                        mPlayMusic.setSelected(false);
                    } else if ((transportControlFlags & PlaybackState.ACTION_PAUSE) != 0) {
                        transportControls.pause();
                        mPlayMusic.setSelected(false);
                    }
                } else {
                    transportControls.play();
                    mPlayMusic.setSelected(true);
                }
                break;

            case R.id.music_next:

                if (transportControls != null) {
                    transportControls.skipToNext();
                    Log.e(TAG, "transportControls next");
                }
                break;

            case R.id.ib_change:
            case R.id.bt_text_change:
                sourceDialog = new AlertDialog.Builder(getActivity()).create();
                LinearLayout source_layout = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.source_layout,null);
                for (String key : MediaActivity.name_path.keySet()){
                    LinearLayout item = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.source_item_layout,null);
                    TextView item_title;
                    item_title = item.findViewById(R.id.source_title);
                    item_title.setText(key);
                    if (key.equals("本地")){
                        Drawable left = getActivity().getDrawable(R.drawable.folder);
                        item_title.setCompoundDrawablesWithIntrinsicBounds(left,null,null,null);
                    } else {
                        Drawable left = getActivity().getDrawable(R.drawable.usb);
                        item_title.setCompoundDrawablesWithIntrinsicBounds(left,null,null,null);
                    }
                    item_title.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mBtTextView.getText().equals(((TextView)v).getText())){

                            }else {

                                MediaActivity.current_source_name = (String) ((TextView)v).getText();

                                    changeMusicFragent();
                                    transportControls.pause();

                            }
                            sourceDialog.dismiss();
                        }
                    });
                    source_layout.addView(item);
                }
                LinearLayout item = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.source_item_layout,null);
                TextView item_title;
                item_title = item.findViewById(R.id.source_title);
                item_title.setText("蓝牙音乐");
                Drawable left = getActivity().getDrawable(R.drawable.usb);
                item_title.setCompoundDrawablesWithIntrinsicBounds(left,null,null,null);
                item_title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mBtTextView.getText().equals(((TextView)v).getText())){

                        }
                        sourceDialog.dismiss();
                    }
                });
                source_layout.addView(item);
                sourceDialog.setView(source_layout);
                sourceDialog.show();
                Window dialogWindow1 = sourceDialog.getWindow();
                WindowManager.LayoutParams lp1 = dialogWindow1.getAttributes();
                lp1.height = 400;
                lp1.width = 942;
                lp1.gravity = Gravity.TOP|Gravity.START;
                lp1.x = 68;
                lp1.y = 126;
                dialogWindow1.setAttributes(lp1);
                break;
        }
    }

    private Fragment myFagment;
    private void changeMusicFragent(){
        myFagment = null;
        myFagment = new MusicFragment();
        FragmentTransaction transaction= getActivity().getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putBoolean("flag",flag);
        myFagment.setArguments(bundle);
        transaction.replace(R.id.fragment_container,myFagment);
        transaction.commit();
    }



    public void getPlayState(){
        mContext = this.getActivity().getApplicationContext();
        checkAppointPerssion();
        ComponentName componentNames = new ComponentName("com.android.bluetooth",
                "com.android.bluetooth.a2dpsink.mbs.A2dpMediaBrowserService");
        mMediaBrowser = new MediaBrowser(mContext, componentNames,
                new MediaBrowser.ConnectionCallback() {
                    @Override
                    public void onConnected() {
                        super.onConnected();
                        Log.i(TAG, "onConnected: mMediaBrowser.isConnected()="+mMediaBrowser.isConnected());
                        //这个地方的调用，需要已连接状态后才能调用。
                        token = mMediaBrowser.getSessionToken();
                        mediaController = new MediaController(mContext, token);
                        //MediaController.Callback 用于接收MediaSession的接口方法回调
                        mediaController.registerCallback(new MediaController.Callback() {
                            //MedSession通知回调播放状态改变
                            @Override
                            public void onPlaybackStateChanged(PlaybackState state) {
                                super.onPlaybackStateChanged(state);
                                Log.i(TAG, "onPlaybackStateChanged: state="+state.getState());
                                mState =  state;
                                if(mState.getState()==PlaybackState.STATE_PLAYING) {
                                    mPlayMusic.setSelected(true);
                                    if (MusicFragment.music_play!=null) {
                                        MusicFragment.music_play.setSelected(false);
                                        //需要跳转到蓝牙播放界面
                                        if(MediaActivity.isBT==false&&displayTab==1){
                                            changeToBTMusic();
                                        }

                                    }
                                }else {
                                    mPlayMusic.setSelected(false);
                                }
                            }

                            @Override
                            public void onMetadataChanged(@Nullable MediaMetadata mediaMetadata) {
                                super.onMetadataChanged(mediaMetadata);
                                Log.i(TAG, "onMetadataChanged: ");
                                if (mediaMetadata == null) {
                                    return;
                                }

                                mMusicName = mediaMetadata.getString(MediaMetadata.METADATA_KEY_TITLE);
                                artist = mediaMetadata.getString(MediaMetadata.METADATA_KEY_ARTIST);

                                Log.i(TAG,"----------WWWWWW---------" +mMusicName + "---"+btMusicName);
                                btMusicName.setText(mMusicName);
                                btMusicSonger.setText(artist);

                            }
                        });
                        transportControls = mediaController.getTransportControls();
                    }

                    @Override
                    public void onConnectionSuspended() {
                        super.onConnectionSuspended();
                        Log.i(TAG, "onConnectionSuspended: ");
                    }

                    @Override
                    public void onConnectionFailed() {
                        super.onConnectionFailed();
                        Log.i(TAG, "onConnectionFailed: ");
                    }
                },
                null);
        mMediaBrowser.connect();
    }
    private Activity mActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity=activity;
    }
    public Activity getMyActivity()
    {
        return mActivity;
    }
    public void changeToBTMusic() {
        MediaActivity myActivity = (MediaActivity) getMyActivity();
        myActivity.changeToBTMusic();
    }


    public void checkAppointPerssion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = ContextCompat.checkSelfPermission(mContext,READ_EXTERNAL_STORAGE);
            if (result == PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "权限有过了");
                return;
            } else {
                Log.e(TAG, "权限没有了");
                requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, 68);
            }
        }
    }
}
