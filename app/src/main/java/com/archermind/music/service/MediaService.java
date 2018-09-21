package com.archermind.music.service;


import android.content.Intent;
import android.database.Cursor;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.util.Log;

import java.util.List;


/**
 * Created by archermind on 12/02/18.
 */

@RequiresApi(api = Build.VERSION_CODES.O)
public class MediaService extends MediaBrowserServiceCompat {

    private MediaSession mediaSession = null;
    //  "content://media/external/audio/albums" 查询外置SD卡
    private Uri albumExternalUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;

    //  "content://media/internal/audio/albums" 查询内置SD卡
    private Uri albumInternalUri = MediaStore.Audio.Albums.INTERNAL_CONTENT_URI;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaSession = new MediaSession(MediaService.this, "xiaolajiao");
//        服务中通过MediaStore内容提供者查询设备上的音乐得到Cursor对象
        /**
         * 设置MediaSession的Callback，用于接收UI界面上的媒体控制指令(MediaController.TransportControls)
         * 重写MediaSession.Callback对应的方法，用于接收UI界面对应的控制指令
         */
        mediaSession.setCallback(new MediaSession.Callback() {
            @Override
            public void onPlay() {
                super.onPlay();
                Log.e("xiaolajiao","mediaSession onPlay");

                /**
                 * UI界面播放指令触发，MediaSession播放某首音乐，并通知UI界面，播放状态改变
                 */
                PlaybackState playbackState  = new PlaybackState.Builder()
                        .build();
                mediaSession.setPlaybackState(playbackState);
            }

            @Override
            public void onPause() {
                super.onPause();
                Log.e("xiaolajiao","mediaSession onPause");
            }

        });
    }

    private void getUriColumns(Uri uri){
        Cursor cursor = getContentResolver().query(albumInternalUri, null, null, null, null);
        while (cursor.moveToNext()){
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return null;
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {

    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class LocalBinder extends Binder {
        public LocalBinder() {
        }

        public MediaSession.Token getSessionToken() {
            if (mediaSession == null) {
                mediaSession = new MediaSession(MediaService.this, "xiaolajiao");
                mediaSession.isActive();
            }
            return mediaSession.getSessionToken();

        }

        public MediaService getMediaService(){
            return MediaService.this;
        }
    }
}