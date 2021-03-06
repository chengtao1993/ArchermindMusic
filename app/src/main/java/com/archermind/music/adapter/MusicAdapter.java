package com.archermind.music.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
        RelativeLayout itemContainer;
        ImageView isPlaying;
        TextView music_name;
        TextView music_singer;
        TextView music_album;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView==null){
            viewHolder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_music,null);
            viewHolder.itemContainer = (RelativeLayout)convertView.findViewById(R.id.item_container);
            viewHolder.isPlaying = (ImageView) convertView.findViewById(R.id.icon_playing);
            viewHolder.music_name = (TextView) convertView.findViewById(R.id.music_name);
            viewHolder.music_singer = (TextView) convertView.findViewById(R.id.music_singer);
            viewHolder.music_album = (TextView) convertView.findViewById(R.id.music_album);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        if (fileInfo.size()>0){
            bean = fileInfo.get(position);
            viewHolder.music_name.setText(fileInfo.get(position).getText_song());
            viewHolder.music_singer.setText(fileInfo.get(position).getText_singer());
            viewHolder.music_album.setText(fileInfo.get(position).getAlbum());
            if (position== MusicFragment.currentNumber){
                viewHolder.itemContainer.setBackground(mContext.getResources().getDrawable(R.drawable.btn_choose));
                viewHolder.isPlaying.setVisibility(View.VISIBLE);
                viewHolder.music_name.setTextColor(mContext.getResources().getColor(R.color.textSelected));
                viewHolder.music_singer.setTextColor(mContext.getResources().getColor(R.color.textSelected));
                viewHolder.music_album.setTextColor(mContext.getResources().getColor(R.color.textSelected));
            }else {
                viewHolder.itemContainer.setBackground(null);
                viewHolder.isPlaying.setVisibility(View.INVISIBLE);
                viewHolder.music_name.setTextColor(Color.WHITE);
                viewHolder.music_singer.setTextColor(Color.WHITE);
                viewHolder.music_album.setTextColor(Color.WHITE);

            }
        }

        return convertView;
    }




}