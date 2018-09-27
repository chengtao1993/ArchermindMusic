package com.archermind.music.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;


import com.archermind.music.R;

import java.util.ArrayList;


public class ViewPagerAdapter extends PagerAdapter {
    private ArrayList<View> views;
    private Context mContext;
    private int[] tabName = new int[]{R.string.tab_all,R.string.tab_favorite};

    public ViewPagerAdapter(ArrayList<View> views,Context mContext){
        this.views = views;
        this.mContext = mContext;
    }
    @Override
    public int getCount() {
        Log.d("hct","views.size() = "+views.size());
        return views.size();
    }
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.d("hct","position = "+position);
        View v = views.get(position);
        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View v = views.get(position);
        container.removeView(v);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
       return mContext.getString(tabName[position]);
    }
}
