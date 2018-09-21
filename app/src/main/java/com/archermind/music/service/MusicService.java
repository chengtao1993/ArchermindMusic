package com.archermind.music.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.archermind.music.MediaActivity;
import com.archermind.music.R;
import com.archermind.music.MusicFragment;
import com.archermind.music.MusicListFragment;
/*import android.os.SystemProperties;
import android.car.export.Car;*/
import android.widget.Toast;

import java.util.Random;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {
    //初始化媒体播放器
    public MediaPlayer mediaPlayer;
    // CH <BugId:2419> <lizhi> <20180324> modify begin
    public AudioManager audioManager;
    // CH <BugId:2419> <lizhi> <20180324> modify end
    public boolean tag = false;
    private Random rand;
    private int randnumber;

    public MusicService() {
        mediaPlayer = new MediaPlayer();
//        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//               //SystemProperties.set("service.gr.play","0");
//            }
//        });
        try {
            if (MusicFragment.bean.getUri() == null) {
                mediaPlayer.setDataSource(MusicFragment.bean.getPath());
            } else {
                mediaPlayer.setDataSource(this, MusicFragment.bean.getUri());
            }
            mediaPlayer.prepare();
            mediaPlayer.setLooping(false);
            mediaPlayer.setOnCompletionListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //  通过 Binder 来保持 Activity 和 Service 的通信
    public MusicBinder binder = new MusicBinder();

    // CH <BugId:2419> <lizhi> <20180324> modify begin
    AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            Log.i("lizhi", "MusicService onAudioFocusChange focusChange = " + focusChange);
            switch (focusChange) {
                // 获得音频焦点 1
                case AudioManager.AUDIOFOCUS_GAIN:
                    playOrPause();
                    break;
                // 长久的失去音频焦点，释放MediaPlayer -1
                case AudioManager.AUDIOFOCUS_LOSS:
                    stop();
                    break;
                // 暂时失去音频焦点，暂停播放等待重新获得音频焦点 -2
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    if (mediaPlayer.isPlaying())
                        mediaPlayer.pause();
                    break;
                // 失去音频焦点，无需停止播放，降低声音即可 -3
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.setVolume(0.1f, 0.1f);
                    }
                    break;
            }
        }
    };
    // CH <BugId:2419> <lizhi> <20180324> modify end

    // CH <BugId:2419> <lizhi> <20180324> modify begin
    @Override
    public void onCreate() {
        super.onCreate();
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
    }
    // CH <BugId:2419> <lizhi> <20180324> modify end

   //对于歌曲的监听
    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.i("ccc","播放完了");
        //单曲循环
        if(MusicFragment.modeNumber==2) {
            mediaPlayer.setLooping(true);
            Log.i("ccc","单曲循环");
            lastOrnext();
            //列表循环
        }else if(MusicFragment.modeNumber==1){
            Log.i("ccc","列表循环");
            if (MusicFragment.currentNumber<MusicFragment.beans.size()-1){
                mediaPlayer.setLooping(false);
                MusicFragment.currentNumber=MusicFragment.currentNumber+1;
            }else {
                mediaPlayer.setLooping(false);
                MusicFragment.currentNumber=0;
            }
            MusicFragment.bean=MusicFragment.beans.get(MusicFragment.currentNumber);
            //lastOrnext();
            if(MediaActivity.musicFragmentTab==5) {
                MusicFragment.changeView();
            }else if(MediaActivity.musicFragmentTab==6){
                MusicListFragment.changeView();
                lastOrnext();
            }
        }else {
            //随机
            Log.i("ccc","随机播放");
            if (rand==null) {
                rand = new Random();
            }
            randnumber = rand.nextInt(MusicFragment.beans.size());
            MusicFragment.currentNumber=randnumber;
            if(MediaActivity.musicFragmentTab==5) {
                MusicFragment.changeView();
            }else if(MediaActivity.musicFragmentTab==6){
                MusicListFragment.changeView();
                lastOrnext();
            }
        }
    }

    public class MusicBinder extends Binder {
       public MusicService getService() {
            return MusicService.this;
        }
    }

    //暂停和开始播放
       public void playOrPause() {

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            /*if (MediaActivity.mCarHUDManager != null) {
                MediaActivity.mCarHUDManager.SendHudMoudlesValue(Car.HUD_MUSIC_PLAYING_STATUS, "0");
            }*/
           /*SystemProperties.set("service.gr.play","0");*/
        } else {
           Log.i("ccc","mediaPlayer.start()");
            mediaPlayer.start();
           /* if (MediaActivity.mCarHUDManager != null) {
                MediaActivity.mCarHUDManager.SendHudMoudlesValue(Car.HUD_MUSIC_PLAYING_STATUS, "1");
            }*/
            // CH <BugId:2419> <lizhi> <20180324> modify begin
            mediaPlayer.setVolume(1.0f, 1.0f);
            requestAudioFocus();
            // CH <BugId:2419> <lizhi> <20180324> modify end
            /*SystemProperties.set("service.gr.play","1");*/
        }
    }

    public void playPause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            /*if (MediaActivity.mCarHUDManager != null) {
                MediaActivity.mCarHUDManager.SendHudMoudlesValue(Car.HUD_MUSIC_PLAYING_STATUS,"0");
            }*/
        }
    }
    public boolean isPlaying(){

        return mediaPlayer.isPlaying();
    }
    //停止
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            /*if (MediaActivity.mCarHUDManager != null) {
                MediaActivity.mCarHUDManager.SendHudMoudlesValue(Car.HUD_MUSIC_CLOSE);
            }*/
           /* SystemProperties.set("service.gr.play","0");*/
            try {
                mediaPlayer.reset();
                if (MusicFragment.bean.getUri() == null) {
                    mediaPlayer.setDataSource(MusicFragment.bean.getPath());
                }else {
                    mediaPlayer.setDataSource(this,MusicFragment.bean.getUri());
                }
                mediaPlayer.prepare();
                mediaPlayer.seekTo(0);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // CH <BugId:2419> <lizhi> <20180324> modify begin
                abandonAudioFocus();
                // CH <BugId:2419> <lizhi> <20180324> modify end
            }
        }
    }

    //下一首
    public void lastOrnext(){
        mediaPlayer.stop();
        /*SystemProperties.set("service.gr.play","0");*/
        try {
            mediaPlayer.reset();
            Log.i("music======","uri==========="+MusicFragment.bean.getUri());
            if (MusicFragment.bean.getUri() == null) {
                mediaPlayer.setDataSource(MusicFragment.bean.getPath());
            }else {
                mediaPlayer.setDataSource(this,MusicFragment.bean.getUri());
            }
            mediaPlayer.prepare();
            mediaPlayer.start();
            requestAudioFocus();
            /*if (MediaActivity.mCarHUDManager != null) {
                MediaActivity.mCarHUDManager.SendHudMoudlesValue(Car.HUD_MUSIC_PLAYING_STATUS,"1");
            }*/
           /* SystemProperties.set("service.gr.play","1");*/
        } catch (Exception e) {
            Log.i("ccc","播放失败"+e);
            Toast.makeText(getApplicationContext(),"不支持该文件类型",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void dataSourceChanged(){
        try {
            mediaPlayer.stop();
            /*SystemProperties.set("service.gr.play","0");*/
            mediaPlayer.reset();
            Log.i("music======","uri==========="+MusicFragment.bean.getUri());
            if (MusicFragment.bean.getUri() == null) {
                mediaPlayer.setDataSource(MusicFragment.bean.getPath());
            }else {
                mediaPlayer.setDataSource(this,MusicFragment.bean.getUri());
            }
            mediaPlayer.prepare();
            mediaPlayer.start();
            requestAudioFocus();
            /*if (MediaActivity.mCarHUDManager != null) {
                MediaActivity.mCarHUDManager.SendHudMoudlesValue(Car.HUD_MUSIC_PLAYING_STATUS,"1");
            }*/
            /*SystemProperties.set("service.gr.play","1");*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    // CH <BugId:2419> <lizhi> <20180324> modify begin
    private void requestAudioFocus() {
        if (audioManager == null) return;
        int status = audioManager.requestAudioFocus(
                audioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        Log.i("lizhi", "MusicService requestAudioFocus status = " + status);
    }
    // CH <BugId:2419> <lizhi> <20180324> modify end

    // CH <BugId:2419> <lizhi> <20180324> modify begin
    private void abandonAudioFocus() {
        if (audioManager == null) return;
        int status = audioManager.abandonAudioFocus(
                audioFocusChangeListener);
        Log.i("lizhi", "MusicService abandonAudioFocus status = " + status);
    }
    // CH <BugId:2419> <lizhi> <20180324> modify end
}

