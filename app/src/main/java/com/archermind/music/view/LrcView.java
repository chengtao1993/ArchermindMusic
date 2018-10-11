package com.archermind.music.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.archermind.music.bean.LrcContent;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by archermind on 18-1-23.
 * 自定义绘画歌词，产生滚动效果
 */

public class LrcView extends TextView {
    private  float width; //歌词视图宽度
    private float height;
    private  float textSize=27;//文本大小
    private  float textHeight=33;//文本高度
    private int index=0;//list集合下标


    private List<LrcContent> mlrclist=new ArrayList<LrcContent>();
    private Paint currentPaint;
    private Paint notCurrentPaint;

    public void setmLrcList(List<LrcContent> mLrcList){
        this.mlrclist=mLrcList;
    }

    public LrcView(Context context) {
        super(context);
        init();
    }

    public LrcView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LrcView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //设置可对焦
        setFocusable(true);
        //高亮部分
        currentPaint = new Paint();
        //设置抗锯齿，让文字美观饱和
        currentPaint.setAntiAlias(true);
        //设置文本对齐方式
        currentPaint.setTextAlign(Paint.Align.CENTER);

        //非高亮部分
        notCurrentPaint = new Paint();
        notCurrentPaint.setAntiAlias(true);
        notCurrentPaint.setTextAlign(Paint.Align.CENTER);
    }

    /**
     * 绘画歌词
     * */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canvas==null){
            return;
        }
        currentPaint.setColor(Color.argb(210,111,246,253));
        notCurrentPaint.setColor(Color.argb(140,255,255,255));
        currentPaint.setTextSize(31);
        currentPaint.setTypeface(Typeface.SANS_SERIF);
        notCurrentPaint.setTextSize(textSize);
        currentPaint.setTypeface(Typeface.DEFAULT);
     try {
         setText("");

         canvas.drawText(mlrclist.get(index).getLrcStr(),width/2,height/2,currentPaint);

         float tempy=height/2;
         //画出本句之前的句子
         for (int i = index-1; i >0 ; i--) {
             //向上推移
             tempy=tempy-textHeight;
             canvas.drawText(mlrclist.get(i).getLrcStr(),width/2,tempy,notCurrentPaint);
         }
         tempy=height/2;
         for (int i=index+1;i<mlrclist.size();i++){
             //往下推移
             tempy=tempy+textHeight;
             canvas.drawText(mlrclist.get(i).getLrcStr(),width/2,tempy,notCurrentPaint);
         }
          }catch (Exception e){
            setText("...未找到歌词...");
          }

    }
/**
 * 当view大小改变时候调用的方法
 * */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.width=w;
        this.height=h;
    }

    public void setIndex(int index){
        this.index=index;
    }

}
