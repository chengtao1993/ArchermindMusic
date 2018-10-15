package com.archermind.music.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;



public class MusicAppWidgetReceiver extends BroadcastReceiver{
    String action;
    @Override
    public void onReceive(Context context, Intent intent) {
        if(null != intent ){
            action = intent.getAction();
            if (action.equals("")){
               //播放暂停
                }
            }else if(action.equals("")){
                //上一首下一首
            }
        }

    }

