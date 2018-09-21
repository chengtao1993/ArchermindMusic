package com.archermind.music.bean;

import android.net.Uri;

import java.io.Serializable;

/**
 * Created by n009654 on 2018/1/18.
 * 封装的音乐的信息
 */

public class MusicBean implements Serializable {
    String text_song;
    String text_singer;
    String path;
    Uri uri;

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    long id;

    public long getAlbumID() {
        return albumID;
    }

    public void setAlbumID(long albumID) {
        this.albumID = albumID;
    }

    long albumID;

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    String display_name;

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    String album;

    public String getText_song() {
        return text_song;
    }
    public void setText_song(String text_song) {
        this.text_song = text_song;
    }
    public String getText_singer() {
        return text_singer;
    }
    public void setText_singer(String text_singer) {
        this.text_singer = text_singer;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }

}
