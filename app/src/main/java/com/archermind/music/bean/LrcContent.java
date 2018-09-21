package com.archermind.music.bean;

/**
 * 歌词的实体类
 * Created by archermind on 18-1-23.
 */

public class LrcContent {
    //歌词内容
    private String lrcStr;
    //歌词当前时间
    private int lrcTime;

    public String getLrcStr() {
        return lrcStr;
    }

    public void setLrcStr(String lrcStr) {
        this.lrcStr = lrcStr;
    }

    public int getLrcTime() {
        return lrcTime;
    }

    public void setLrcTime(int lrcTime) {
        this.lrcTime = lrcTime;
    }
}
