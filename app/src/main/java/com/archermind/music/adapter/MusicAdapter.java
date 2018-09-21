package com.archermind.music.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.archermind.music.R;
import com.archermind.music.MusicFragment;
import com.archermind.music.bean.MusicBean;
import com.archermind.music.utils.ScanMusic;

import java.util.ArrayList;

/**
 * Created by xing on 2017/7/11.
 */

public class MusicAdapter extends BaseAdapter {
    private Context mContext;
    //列表位置
    private int pos=-1;

    private ArrayList<MusicBean> fileInfo;
    private MusicBean bean;

    public MusicAdapter(Context mContext, ArrayList<MusicBean> fileInfo) {
        this.mContext = mContext;
        this.fileInfo =fileInfo;
    }

    public void setData(ArrayList<MusicBean> fileInfo){
        this.fileInfo = fileInfo;
    }

    @Override
    public int getCount() {
        int count = 0;
        if (fileInfo!=null && fileInfo.size()>0){
            count = fileInfo.size();
        }
        return count;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    class ViewHolder{
        ImageView ivImage;
        TextView music_name;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView==null){
            viewHolder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_music,null);
            viewHolder.ivImage = (ImageView)convertView.findViewById(R.id.iv_music);
            viewHolder.music_name=(TextView) convertView.findViewById(R.id.music_name);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        if (fileInfo.size()>0){
            bean = fileInfo.get(position);
            if(position==pos){

            }else {
                Bitmap bitmap= ScanMusic.getArtwork(mContext,bean.getId(),bean.getAlbumID(),true,true);
                viewHolder.ivImage.setImageBitmap(bitmap);
            }
            viewHolder.music_name.setText(fileInfo.get(position).getText_song());
            if (position== MusicFragment.currentNumber){
                viewHolder.music_name.setTextColor(Color.GREEN);
            }else {
                viewHolder.music_name.setTextColor(Color.WHITE);
            }
        }

        return convertView;
    }




}