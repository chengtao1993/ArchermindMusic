package com.archermind.music.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.archermind.music.R;
import com.archermind.music.bean.MusicBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
/**
 * Created by n009654 on 2018/1/18.
 */

public class ScanMusic {
    //获得专辑封面的uri
    private static  final Uri albumArtUri=Uri.parse("content://media/external/audio/albumart");
    public static ArrayList<MusicBean> data = new ArrayList<MusicBean>();
    private static Cursor cursor;

    public ArrayList<MusicBean> query(ArrayList<MusicBean>list, Context mContext) {
        //创建ArryList
        ArrayList<MusicBean>arrayList;
        //实例化ArryList对象
        arrayList = new ArrayList<MusicBean>();
        //创建一个扫描游标
        Cursor c=mContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if(c!=null)
        {
            //创建Model对象
            MusicBean model;
            //循环读取
            //实例化Model对象
            while(c.moveToNext()){

                model = new MusicBean();
                //扫描本地文件，得到歌曲的相关信息
                //歌曲的名称
                String music_name=c.getString(c.getColumnIndex(MediaStore.Audio.Media.TITLE));
                //歌手的名称
                String music_singer=c.getString(c.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                //歌曲的全路径
                String path = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA));
                //歌曲的专辑名：MediaStore.Audio.Media.ALBUM
                String album=c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                //歌曲的总播放时长 ：MediaStore.Audio.Media.DURATION

                //歌曲文件的大小 ：MediaStore.Audio.Media.SIZE

                //歌曲文件的全名称：
                String display_name=c.getString(c.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                //专辑的id
                long albumID=c.getInt(c.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                //音乐ｉｄ
                long id=c.getLong(c.getColumnIndex(MediaStore.Audio.Media._ID));
                //设置值到Model的封装类中
                model.setText_song(music_name);
                model.setText_singer(music_singer);
                model.setPath(path);
                model.setAlbum(album);
                model.setDisplay_name(display_name);
                model.setAlbumID(albumID);
                model.setId(id);
                //将model值加入到数组中
                arrayList.add(model);

            }
            //打印出数组的长度
            System.out.println(arrayList.size());

        }
        //得到一个数组的返回值
        return arrayList;
    }

    public static ArrayList getData(Context context,String path){
        data.clear();
        try {
            cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
            while (cursor.moveToNext()){
                MusicBean musicBean = new MusicBean();
                musicBean.setText_song(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                musicBean.setText_singer(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                musicBean.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                musicBean.setAlbum(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
                musicBean.setDisplay_name(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
                musicBean.setAlbumID(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
                musicBean.setId(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
                musicBean.setUri(null);
                if(path.equals("external")){
                    if(musicBean.getPath().contains("storage/emulated")){
                        data.add(musicBean);
                    }
                }else {
                    if(!musicBean.getPath().contains("storage/emulated")){
                        data.add(musicBean);
                    }
                }


//            if (path.equals("external")){
//                cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,
//                        MediaStore.Audio.AudioColumns.DATA+" like "+"'/storage/emulated/0%'",null,MediaStore.Audio.Media.DATE_ADDED);
//                while (cursor.moveToNext()){
//                    MusicBean musicBean = new MusicBean();
//                    musicBean.setText_song(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
//                    musicBean.setText_singer(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
//                    musicBean.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
//                    musicBean.setAlbum(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
//                    musicBean.setDisplay_name(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
//                    musicBean.setAlbumID(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
//                    musicBean.setId(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
//                    musicBean.setUri(null);
//                    data.add(musicBean);
//                }
//            }else {
//                cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,
//                        MediaStore.Audio.AudioColumns.DATA+" like "+"'"+path+"%'",null,MediaStore.Audio.Media.DATE_ADDED);
//                while (cursor.moveToNext()){
//                    MusicBean musicBean = new MusicBean();
//                    musicBean.setText_song(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
//                    musicBean.setText_singer(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
//                    musicBean.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
//                    musicBean.setAlbum(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
//                    musicBean.setDisplay_name(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
//                    musicBean.setAlbumID(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
//                    musicBean.setId(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
//                    Uri uri = UriUtils.getUri(musicBean.getPath());
//                    musicBean.setUri(uri);
//                    data.add(musicBean);
//                }
            }
            cursor.close();
        }catch (Exception e){
            Log.i("ccc","出现了异常"+e);
        }

        return data;
    }


    //获得默认专辑的图片
    @SuppressWarnings("ResourceType")
    public static Bitmap getDefaultArtwork(Context context, boolean small){
        Options opts=new Options();
        opts.inPreferredConfig=Bitmap.Config.RGB_565;
        if (small){//返回小图片
            return BitmapFactory.decodeStream(context.getResources().openRawResource(R.drawable.default_image),null,opts);
        }
        return  BitmapFactory.decodeStream(context.getResources().openRawResource(R.drawable.default_image),null,opts);
    }

    //从文件中获取专辑封面位图


    public static ArrayList<MusicBean> redLrc(String path) {
        ArrayList<MusicBean> alist = new ArrayList<MusicBean>();

        File f = new File(path.replace(".mp3", ".lrc"));

        try {
            FileInputStream fs = new FileInputStream(f);
            InputStreamReader inputStreamReader = new InputStreamReader(fs,
                    "utf-8");
            BufferedReader br = new BufferedReader(inputStreamReader);
            String s = "";
            while (null != (s = br.readLine())) {
                if (!TextUtils.isEmpty(s)) {
                    MusicBean lrcModle = new MusicBean();
                    String lylrc = s.replace("[", "");
                    String data_ly[] = lylrc.split("]");
                    if (data_ly.length > 1) {
                        String time = data_ly[0];
                        //lrcModle.setTime(LrcData(time));
                        String lrc = data_ly[1];
                        //lrcModle.setLrc(lrc);
                        alist.add(lrcModle);
                    }

                }

            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return alist;

    }

    public static int LrcData(String time) {
        time = time.replace(":", "#");
        time = time.replace(".", "#");
        String mTime[] = time.split("#");
        int mtime = Integer.parseInt(mTime[0]);
        int stime = Integer.parseInt(mTime[1]);
        int mitime = Integer.parseInt(mTime[2]);
        int ctime = (mtime * 60 + stime) * 1000 + mitime * 10;
        return ctime;
    }

    private static Bitmap getArtworkFromFile(Context context, long songid, long albumid){
        Bitmap bm = null;
        if(albumid < 0 && songid < 0) {
            throw new IllegalArgumentException("Must specify an album or a song id");
        }
        try {
            Options options = new Options();
            FileDescriptor fd = null;
            if(albumid < 0){
                Uri uri = Uri.parse("content://media/external/audio/media/"
                        + songid + "/albumart");
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if(pfd != null) {
                    fd = pfd.getFileDescriptor();
                }
            } else {
                Uri uri = ContentUris.withAppendedId(albumArtUri, albumid);
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if(pfd != null) {
                    fd = pfd.getFileDescriptor();
                }
            }
            options.inSampleSize = 1;
            options.inJustDecodeBounds = true;

            BitmapFactory.decodeFileDescriptor(fd, null, options);
            options.inSampleSize = 100;
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            bm = BitmapFactory.decodeFileDescriptor(fd, null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bm;
    }

    /**
     * 获取专辑图片
     * @param context
     * @param song_id
     * @param album_id
     * @param allowdefalut
     * @return
     */
    public static Bitmap getArtwork(Context context, long song_id, long album_id, boolean allowdefalut, boolean small){
        if(album_id < 0) {
            if(song_id < 0) {
                Bitmap bm = getArtworkFromFile(context, song_id, -1);
                if(bm != null) {
                    return bm;
                }
            }
            if(allowdefalut) {
                return getDefaultArtwork(context, small);
            }
            return null;
        }
        ContentResolver res = context.getContentResolver();
        Uri uri = ContentUris.withAppendedId(albumArtUri, album_id);
        if(uri != null) {
            InputStream in = null;
            try {
                in = res.openInputStream(uri);
                Options options = new Options();

                options.inSampleSize = 1;

                options.inJustDecodeBounds = true;

                BitmapFactory.decodeStream(in, null, options);

                if(small){
                    options.inSampleSize = computeSampleSize(options, 40);
                } else{
                    options.inSampleSize = computeSampleSize(options, 600);
                }
                options.inJustDecodeBounds = false;
                options.inDither = false;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                in = res.openInputStream(uri);
                return BitmapFactory.decodeStream(in, null, options);
            } catch (FileNotFoundException e) {
                Bitmap bm = getArtworkFromFile(context, song_id, album_id);
                if(bm != null) {
                    if(bm.getConfig() == null) {
                        bm = bm.copy(Bitmap.Config.RGB_565, false);
                        if(bm == null && allowdefalut) {
                            return getDefaultArtwork(context, small);
                        }
                    }
                } else if(allowdefalut) {
                    bm = getDefaultArtwork(context, small);
                }
                return bm;
            } finally {
                try {
                    if(in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 缩放
     * @param options
     * @param target
     * @return
     */
    public static int computeSampleSize(Options options, int target) {
        int w = options.outWidth;
        int h = options.outHeight;
        int candidateW = w / target;
        int candidateH = h / target;
        int candidate = Math.max(candidateW, candidateH);
        if(candidate == 0) {
            return 1;
        }
        if(candidate > 1) {
            if((w > target) && (w / candidate) < target) {
                candidate -= 1;
            }
        }
        if(candidate > 1) {
            if((h > target) && (h / candidate) < target) {
                candidate -= 1;
            }
        }
        return candidate;
    }
}
