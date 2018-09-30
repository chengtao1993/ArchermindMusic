package com.archermind.music.utils;

import android.util.Log;

import com.archermind.music.bean.LrcContent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 处理歌词的类
 *
 */

public class LrcProcess {
    private List<LrcContent> lrcList;//list集合存放歌词内容对象
    private LrcContent mLrcContent;//声明一个歌词内容对象
    /**
     * 无参构造函数用来实例化对象
     * */
    public LrcProcess(){
        mLrcContent=new LrcContent();
        lrcList=new ArrayList<LrcContent>();
    }
    /**
     * 读取歌词
     * */
    public String readLRC(String path){
        //定义一个stringbuilder对象用来存放歌词内容
        StringBuilder stringBuilder = new StringBuilder();
        File f = new File(path.replace(".mp3", ".lrc"));
        try {
        //创建一个文件输入流对象
            FileInputStream fis = new FileInputStream(f);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String s="";
            while ((s=br.readLine())!=null){
                    //替换字符
                    s = s.replace("[", "");
                    s = s.replace("]", "@");
                    //分离＠字符
                    Log.i("ccc", "sss" + s.toString());
                    String[] spiltLrcData = s.split("@");
                    if (spiltLrcData.length > 1) {
                        Log.i("ccc", "" +spiltLrcData.length  );
                        mLrcContent.setLrcStr(spiltLrcData[1]);
                        Log.i("ccc", "" +spiltLrcData[1]  );
                        //处理歌词获得歌曲的时间
                        int lrcTime = timeToStr(spiltLrcData[0]);
                        mLrcContent.setLrcTime(lrcTime);
                        //添加进列表数组
                        lrcList.add(mLrcContent);
                        //新创建歌词内容对象
                        mLrcContent = new LrcContent();
                    }
                }
        }catch (Exception e){
            stringBuilder.append("没有读取到歌词");
        }
        return stringBuilder.toString();
    }
    /**
     * 解析歌词时间
     * */
    public int timeToStr(String timeStr){
        timeStr=timeStr.replace(":",".");
        timeStr=timeStr.replace(".","@");
        //分离＠字符
        String timeData[] = timeStr.split("@");//将时间分隔成字符串数组
        //分离出分，秒并转化为整型
        int minute=Integer.parseInt(timeData[0]);
        int second=Integer.parseInt(timeData[1]);
        int millisecond=Integer.parseInt(timeData[2]);
        //计算上一行和下一行时间转化为毫秒数
        int currentTieme=(minute*60+second)*1000+millisecond*10;
        return currentTieme;

    }
    public List<LrcContent> getLrcList(){
        return lrcList;
    }
}
