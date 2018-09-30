package com.archermind.music;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.PermissionChecker;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.archermind.music.adapter.MusicAdapter;
import com.archermind.music.adapter.ViewPagerAdapter;
import com.archermind.music.bean.LrcContent;
import com.archermind.music.bean.MusicBean;
import com.archermind.music.service.MusicService;
import com.archermind.music.utils.LrcProcess;
import com.archermind.music.utils.ScanMusic;
import com.archermind.music.view.LrcView;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class MusicFragment extends Fragment implements View.OnClickListener,AdapterView.OnItemClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "MusicFragment";
    //Add by yanglin for Music Null string begin
    private static final String STRING_NULL = "";
    //Add by yanglin for Music Null string end

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View view;
    private ImageButton music_next;
    private ImageButton music_pre;
    public static ImageButton music_play;
    public static SeekBar seekBar;
    public static TextView musictotal;
    public static TextView current_time;
    private ImageButton folder;
    public static ArrayList<MusicBean> beans;
    public static TextView song;
    private ImageButton words;
    private ImageButton setting;
    private ImageButton playMode;
    public TextView data_source;
    private ImageButton source_switch;
    private AlertDialog sourceDialog;
    private ListView music_lv;
    public static MusicAdapter musicAdapter;
    public static ArrayList<MusicBean> fileInfo;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private int[] layouts = new int[]{R.layout.all_list,R.layout.favorite_list};
    public ArrayList<View> view_list = new ArrayList<>();
    private static final int ALL_MUSIC_PAGE = 0;
    private LrcView music_words;
    private int currentTime;
    private List<LrcContent> lrcList;
    private LrcProcess mlrcProcess;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MusicFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MusicFragment newInstance(String param1, String param2) {
        MusicFragment fragment = new MusicFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    private String musicsetting;
    private String musiccontrol;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for (int i = 0; i <layouts.length ; i++) {
            View v = getLayoutInflater().inflate(layouts[i],null);
            view_list.add(v);
        }
        Log.i("ccc","oncreate"+getArguments());
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            musiccontrol="";
            musicsetting="";
            musiccontrol = getArguments().getString("music_control");
            musicsetting = getArguments().getString("music_setting");
            Log.i("ccc","musiccontrol"+musiccontrol);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_music, container, false);
        mViewPager = view.findViewById(R.id.view_pager);
        mViewPager.setAdapter(new ViewPagerAdapter(view_list,getContext()));
        mTabLayout = view.findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(mViewPager,true);
        fileInfo = ScanMusic.getData(getActivity(),MediaActivity.current_source_path);
        data_source = view.findViewById(R.id.data_source);
        data_source.setText(MediaActivity.current_source_name);
        source_switch = view.findViewById(R.id.ib);
        source_switch.setOnClickListener(this);
        data_source.setOnClickListener(this);
        scanMusic();

        return view;
    }
    
    public static SimpleDateFormat time = new SimpleDateFormat("mm:ss");
    //控制ａｎｉｍａｔｏｒ
    private boolean tag1 = false;
    //控制当前时间
    public static boolean tag2 = false;
    private  ArrayList<MusicBean> list;
    public static  MusicBean bean;
    public static MusicService musicService;

    //  通过 Handler 更新 UI 上的组件状态
    public static Handler handler = new Handler();
    public  static Runnable runnable = new Runnable() {
        @Override
        public void run() {
            seekBar.setProgress(musicService.mediaPlayer.getCurrentPosition());
            seekBar.setMax(musicService.mediaPlayer.getDuration());
            //获取其时长
            if (bean != null) {
                MusicFragment.
                song.setText(bean.getText_song());
                //Add by yanglin for Music Null string begin
                current_time.setVisibility(View.VISIBLE);
                musictotal.setVisibility(View.VISIBLE);
                //Add by yanglin for Music Null string end
            }else {
                //Add by yanglin for Music Null string begin
                song.setText(STRING_NULL);
                current_time.setVisibility(View.INVISIBLE);
                musictotal.setVisibility(View.INVISIBLE);
                //Add by yanglin for Music Null string end
            }
            current_time.setText(time.format(musicService.mediaPlayer.getCurrentPosition()));
            musictotal.setText("/"+time.format(musicService.mediaPlayer.getDuration()));
            handler.postDelayed(runnable, 200);
        }
    };
    
    //扫描得到歌曲
    public static int currentNumber=0;
    private void scanMusic() {
        int hasWriteContactsPermission = PermissionChecker.checkSelfPermission(getContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            Activity activty=getActivity();//1的话要进行询问，０的话不会询问
            ActivityCompat.requestPermissions(activty,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            return;
        }else {
            initView();
        }
        beans = ScanMusic.getData(getActivity(),MediaActivity.current_source_path);
        Log.i("ccc","加载到了歌曲数据"+beans);
        if (beans.size()>0) {
            bean = beans.get(currentNumber);
            Log.i("ccc","bean"+ beans.size());
            //Add by yanglin for Music Null string begin
            current_time.setVisibility(View.VISIBLE);
            musictotal.setVisibility(View.VISIBLE);
            //Add by yanglin for Music Null string end
        }else {
            Log.i("ccc","没有歌曲");
            //Add by yanglin for Music Null string begin
            //bean=new MusicBean();
            current_time.setVisibility(View.INVISIBLE);
            musictotal.setVisibility(View.INVISIBLE);
            //Add by yanglin for Music Null string end
        }
        bindServiceConnection();
    }
    //  在Activity中调用 bindService 保持与 Service 的通信
    private void bindServiceConnection() {
        Intent intent = new Intent(getActivity(), MusicService.class);
        getActivity().bindService(intent, serviceConnection, getContext().BIND_AUTO_CREATE);
        Log.i("ccc","绑定服务");
        if (musicService!=null) {
            setdata();
        }else {
            getActivity().startService(intent);
        }


    }

    public ServiceConnection getServiceConnection() {
        return serviceConnection;
    }

    public void setServiceConnection(ServiceConnection serviceConnection) {
        this.serviceConnection = serviceConnection;
    }

    //  回调onServiceConnected 函数，通过IBinder 获取 Service对象，实现Activity与 Service的绑定
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicService = ((MusicService.MusicBinder)(service)).getService();
            Log.i("ccc","绑定服务成功");
            solveData();
            //更新ＵＩ线程
            setdata();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("ccc","绑定服务失败");
            musicService = null;

        }
    };

    private void setdata() {
        musictotal.setText("/"+time.format(musicService.mediaPlayer.getDuration()));
        if (bean != null) {
            song.setText(bean.getText_song());
            //Add by yanglin for Music Null string begin
            current_time.setVisibility(View.VISIBLE);
            musictotal.setVisibility(View.VISIBLE);
            //Add by yanglin for Music Null string end
        }else {
            //Add by yanglin for Music Null string begin
            song.setText(STRING_NULL);
            current_time.setVisibility(View.INVISIBLE);
            musictotal.setVisibility(View.INVISIBLE);
            //Add by yanglin for Music Null string end
        }
    }

    private void solveData() {
        if("prev".equals(musiccontrol)){
            Log.i("ccc","prev==="+musiccontrol);
            //上一首
            if (currentNumber>0){
                currentNumber=currentNumber-1;
                changeView();
            }else {
                showToast("已经是第一首");
            }
        }else if("next".equals(musiccontrol)){
            Log.i("ccc","next==="+musiccontrol);
            //下一首
            if(currentNumber<beans.size()-1){
                currentNumber=currentNumber+1;
                changeView();
            }else {
                showToast("已经是最后一首");
            }

        }else if("play_random".equals(musicsetting)){
            //顺序播放
            modeNumber=2;
            playMode.setBackground(getResources().getDrawable(images[modeNumber]));
        }else if("recyle_order".equals(musicsetting)){
            //顺序播放
            modeNumber=1;
            playMode.setBackground(getResources().getDrawable(images[modeNumber]));
        }else if("recyle_single".equals(musicsetting)){
            //单曲播放
            modeNumber=3;
            playMode.setBackground(getResources().getDrawable(images[modeNumber]));
        }else if("pause".equals(musicsetting)){
            //暂停播放
            if(musicService.tag==false){
                return;
            }
            /*SystemProperties.set("service.gr.play","0");*/
            if (music_play.isSelected()) {
                musicService.tag = false;
                music_play.setSelected(false);
            }else {
                musicService.tag = true;
                music_play.setSelected(true);
                if (tag1 == false) {
                    tag1 = true;
                } else {
                    tag1=false;
                }
            }
            //控制刷新当前时间
            if (tag2 == false) {
                handler.post(runnable);
                tag2 = true;
            }
            musicService.playOrPause();
        }else if("play".equals(musicsetting)){
            //开始播放
            if(musicService.tag==true){
                return;
            }
            if (music_play.isSelected()) {
                musicService.tag = false;
                music_play.setSelected(false);
            }else {
                musicService.tag = true;
                music_play.setSelected(true);
                if (tag1 == false) {
                    tag1 = true;
                } else {
                    tag1=false;
                }
            }
            //控制刷新当前时间
            if (tag2 == false) {
                handler.post(runnable);
                tag2 = true;
            }
            musicService.playOrPause();
            Toast.makeText(getActivity(),"开始播放",Toast.LENGTH_SHORT).show();
        }
    }
    private  Toast mToast;
    public void showToast(String text) {
        if(mToast == null) {
            mToast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_SHORT);
            mToast.cancel();
        }
        mToast.show();
    }

    public void receiveMessageAndSetData(){
       /*SystemProperties.set("service.gr.show","1");*/
       //对获得的参数进行修订
       if("prev".equals(MediaActivity.message)){
           //上一首
           if (currentNumber>0){
               currentNumber=currentNumber-1;
               changeView();
           }else {
               Toast.makeText(getActivity(),"已经是第一首",Toast.LENGTH_SHORT).show();
           }
       }else if("next".equals(MediaActivity.message)){
           //下一首
           if(currentNumber<beans.size()-1){
               currentNumber=currentNumber+1;
               changeView();
           }else {
               Toast.makeText(getActivity(), "已经是最后一首", Toast.LENGTH_SHORT).show();
           }

       }else if("play_random".equals(MediaActivity.message)){
           //顺序播放
           modeNumber=2;
           playMode.setBackground(getResources().getDrawable(images[modeNumber]));
       }else if("recyle_order".equals(MediaActivity.message)){
           //顺序播放
           modeNumber=1;
           playMode.setBackground(getResources().getDrawable(images[modeNumber]));
       }else if("recyle_single".equals(MediaActivity.message)){
           //单曲播放
           modeNumber=3;
           playMode.setBackground(getResources().getDrawable(images[modeNumber]));
       }else if("pause".equals(MediaActivity.message)){
           //暂停播放
           if(musicService.tag==false){
               return;
           }
           /*SystemProperties.set("service.gr.play","0");*/
           if (music_play.isSelected()) {
               musicService.tag = false;
               music_play.setSelected(false);
           }else {
               musicService.tag = true;
               music_play.setSelected(true);
               if (tag1 == false) {
                   tag1 = true;
               } else {
                   tag1=false;
               }
           }
           //控制刷新当前时间
           if (tag2 == false) {
               handler.post(runnable);
               tag2 = true;
           }
           musicService.playOrPause();
       }else if("play".equals(MediaActivity.message)){
           //开始播放
           if(musicService.tag==true){
               return;
           }
           if (music_play.isSelected()) {
               musicService.tag = false;
               music_play.setSelected(false);
           }else {
               musicService.tag = true;
               music_play.setSelected(true);
               if (tag1 == false) {
                   tag1 = true;
               } else {
                   tag1=false;
               }
           }
           //控制刷新当前时间
           if (tag2 == false) {
               handler.post(runnable);
               tag2 = true;
           }
           musicService.playOrPause();
           /*SystemProperties.set("service.gr.play","1");*/
           Toast.makeText(getActivity(),"开始播放",Toast.LENGTH_SHORT).show();
       }

   }
    @Override
    public void onResume() {
        super.onResume();
        initView();
        initLrc();
        /*SystemProperties.set("service.gr.show","1");*/
        //对获得的参数进行修订
        MediaActivity.musicFragmentTab=5;
        playMode.setBackground(getResources().getDrawable(images[modeNumber]));

    }

    private void initView() {
        music_words = (LrcView) view.findViewById(R.id.music_words);
        music_lv = view_list.get(ALL_MUSIC_PAGE).findViewById(R.id.music_lv);
        musicAdapter = new MusicAdapter(getContext(), fileInfo);
        music_lv.setAdapter(musicAdapter);
        music_lv.setOnItemClickListener(this);
        music_next = (ImageButton) view.findViewById(R.id.music_next);
        music_next.setOnClickListener(this);
        music_pre = (ImageButton) view.findViewById(R.id.music_pre);
        music_pre.setOnClickListener(this);
        music_play = (ImageButton) view.findViewById(R.id.music_play);
        music_play.setOnClickListener(this);
        if (musicService!=null) {
            if (musicService.mediaPlayer.isPlaying()) {
                musicService.tag = true;
                music_play.setSelected(true);
            } else {
                musicService.tag = false;
                music_play.setSelected(false);
            }
        }
        folder = (ImageButton) view.findViewById(R.id.folder);
        folder.setOnClickListener(this);
        seekBar = (SeekBar) view.findViewById(R.id.seekBar);
       if(musicService!=null){
           if (tag2 == false) {
               handler.post(runnable);
               tag2 = true;
           }
       }
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser==true){
                    musicService.mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        musictotal = (TextView) view.findViewById(R.id.music_time);
        current_time = (TextView) view.findViewById(R.id.current_time);
        //Add by yanglin for Music Null string begin
        current_time.setVisibility(View.INVISIBLE);
        musictotal.setVisibility(View.INVISIBLE);
        song = (TextView) view.findViewById(R.id.song);
        words = (ImageButton) view.findViewById(R.id.words);
        words.setOnClickListener(this);
        setting = (ImageButton) view.findViewById(R.id.setting);
        setting.setOnClickListener(this);
        playMode = (ImageButton) view.findViewById(R.id.playMode);
        playMode.setOnClickListener(this);

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getActivity().unbindService(serviceConnection);
        }catch (Exception e){
            Log.i("ccc","musicFragment"+e);
        }

    }

    public void resourceChanged(){
        data_source.setText(MediaActivity.current_source_name);
        beans = ScanMusic.getData(getActivity(),MediaActivity.current_source_path);
        Log.i("ccc","sdcard____beans"+beans);
        changeDataSource();
    }


    boolean flag = false;
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MusicFragment.currentNumber=position;
        changeView();
        initLrc();
    }
    @Override
    public void onClick(View v) {
        if (musicService==null){
            return;
        }
        switch (v.getId()){
            case R.id.music_pre:
                if(MusicFragment.bean==null||beans.size()==0){
                    return;
                }
                if (currentNumber>0){
                    currentNumber=currentNumber-1;
                }else {
                    currentNumber=beans.size()-1;
                }
                bean=fileInfo.get(MusicFragment.currentNumber);
                initLrc();
                changeView();
                break;
            case R.id.music_next:
                if(MusicFragment.bean==null||beans.size()==0){
                    return;
                }
                if(currentNumber<beans.size()-1){
                currentNumber=currentNumber+1;
               // changeView();
                }else {
                    currentNumber=0;
                    //Toast.makeText(getActivity(), "已经是最后一首", Toast.LENGTH_SHORT).show();
                }
                bean=fileInfo.get(MusicFragment.currentNumber);
                initLrc();
                changeView();
                break;
            case R.id.music_play:
                if(MusicFragment.bean==null||beans.size()==0){
                    return;
                }
                if (music_play.isSelected()) {
                    musicService.tag = false;
                    music_play.setSelected(false);
                }else {
                    musicService.tag = true;
                    music_play.setSelected(true);
                    //开始播放
                    if (MediaActivity.isFirstStart){
                        //发送HUD
                       /* sendHud();*/
                        MediaActivity.isFirstStart=false;
                    }
                    if (tag1 == false) {
                        tag1 = true;
                    } else {
                        tag1=false;
                    }
                }
                //控制刷新当前时间
                if (tag2 == false) {
                    handler.post(runnable);
                    tag2 = true;
                }
                musicService.playOrPause();
                Log.i("ccc",""+musicService.mediaPlayer.isPlaying());
                break;
            case R.id.folder:
                //进入列表播放界面
                if(MusicFragment.bean==null||beans.size()==0){
                    Toast.makeText(getActivity(),"歌曲文件夹为空",Toast.LENGTH_SHORT).show();
                }else {
                    changeToListFragment();
                }


                break;
            case R.id.playMode:
                //播放模式的切换单曲循环，随机播放，顺序播放
                if (modeNumber<2){
                    modeNumber=modeNumber+1;
                }else {
                    modeNumber=0;
                }
                playMode.setBackground(getResources().getDrawable(images[modeNumber]));


                break;
            case R.id.setting:
                //弹出音频调节的框
                Intent intent= new Intent();
                //包名 包名+类名（全路径）
                intent.setClassName("com.archermind.carSettings", "com.archermind.carSettings.activity.CarSettingActivity");
                intent.putExtra("app","media");
                startActivity(intent);
                Toast.makeText(getActivity(),"跳转到设置界面",Toast.LENGTH_SHORT).show();
                //showPopuwindow();
                break;
            case R.id.words:
                changeToWordsFragment();
                break;
            case R.id.ib:
            case R.id.data_source:
                sourceDialog = new AlertDialog.Builder(getActivity()).create();
                LinearLayout source_layout = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.source_layout,null);
                for (String key : MediaActivity.name_path.keySet()){
                    LinearLayout item = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.source_item_layout,null);
                    TextView item_title;
                    item_title = item.findViewById(R.id.source_title);
                    item_title.setText(key);
                    if (key.equals("本地")){
                        Drawable left = getActivity().getDrawable(R.drawable.folder);
                        item_title.setCompoundDrawablesWithIntrinsicBounds(left,null,null,null);
                    } else {
                        Drawable left = getActivity().getDrawable(R.drawable.usb);
                        item_title.setCompoundDrawablesWithIntrinsicBounds(left,null,null,null);
                    }
                    item_title.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (data_source.getText().equals(((TextView)v).getText())){

                            }else {
                                MediaActivity.current_source_name = (String) ((TextView)v).getText();
                                if (MediaActivity.current_source_name.equals("本地")){
                                    Log.i(TAG,"-----------sourceDialog-----本地---");
                                    MediaActivity.current_source_path = (String) MediaActivity.name_path.get(MediaActivity.current_source_name);
                                    data_source.setText(MediaActivity.current_source_name);
                                    beans = ScanMusic.getData(getActivity(),MediaActivity.current_source_path);
                                    changeDataSource();
                                }else {
                                    MediaActivity.current_source_path = (String) MediaActivity.name_path.get(MediaActivity.current_source_name);
                                    data_source.setText(MediaActivity.current_source_name);
                                    beans = ScanMusic.getData(getActivity(),MediaActivity.current_source_path);
                                    Log.i("ccc","................"+beans);
                                    changeDataSource();
                                }

                            }
                            sourceDialog.dismiss();
                        }
                    });
                    source_layout.addView(item);
                }
                LinearLayout item = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.source_item_layout,null);
                TextView item_title;
                item_title = item.findViewById(R.id.source_title);
                item_title.setText("蓝牙音乐");
                Drawable left = getActivity().getDrawable(R.drawable.usb);
                item_title.setCompoundDrawablesWithIntrinsicBounds(left,null,null,null);
                item_title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (data_source.getText().equals(((TextView)v).getText())){

                        }else {
                            data_source.setText("蓝牙音乐");
                            changToBtMusic(getActivity());
                        }
                        sourceDialog.dismiss();
                    }
                });
                source_layout.addView(item);
                sourceDialog.setView(source_layout);
                sourceDialog.show();
                sourceDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        sourceDialogAutoHide.removeMessages(sourceDialogId);
                    }
                });
                Window dialogWindow1 = sourceDialog.getWindow();
                WindowManager.LayoutParams lp1 = dialogWindow1.getAttributes();
                lp1.height = 400;
                lp1.width = 942;
                lp1.gravity = Gravity.TOP|Gravity.START;
                lp1.x = 68;
                lp1.y = 126;
                dialogWindow1.setAttributes(lp1);
                Message message = new Message();
                message.what = sourceDialogId;
                sourceDialogAutoHide.sendMessageDelayed(message,8000);
                break;

        }

    }

    public void changToBtMusic(Context context){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null
                && bluetoothAdapter.isEnabled()
                && bluetoothAdapter.getProfileConnectionState(11) == BluetoothProfile.STATE_CONNECTED) {
//                                        data_source.setText(MediaActivity.current_source_name);
            changeBtMusicFragment("蓝牙音乐",context);
            musicService.playPause();
            MediaActivity.isBT=true;
            data_source.setText("蓝牙音乐");
        } else {
            data_source.setText(MediaActivity.current_source_name);
            Toast.makeText(context, "请先连接蓝牙", Toast.LENGTH_SHORT).show();

        }
    }

    private int sourceDialogId = 1;
    private Handler sourceDialogAutoHide = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == sourceDialogId) {
                if (sourceDialog != null && sourceDialog.isShowing()) {
                    sourceDialog.dismiss();
                }
            }
        }
    };
/**
 * 展示的是设置的
 * */
private PopupWindow pop;
    private void showPopuwindow() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.popuwindow_setting, null);
        pop = new PopupWindow(myview, 920, 500);
        myview.findViewById(R.id.bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(MainActivity2.this, "点击了确定", Toast.LENGTH_SHORT).show();
                pop.dismiss();
            }
        });
        pop.showAsDropDown(folder);     //第一种方法以v为锚点在他下面弹出框。
        pop.showAtLocation(folder, Gravity.CENTER, 100, 200);   //第二种方法，100 200是偏移量，在v的哪个位置，Gravity.CENTER是以V的中心为中心
    }

    //1是随机２是循环３是单曲循环
    public static int[] images={R.mipmap.play_mode1,R.mipmap.play_mode2,R.mipmap.play_mode3};
    public static int modeNumber=0;

    private void changeDataSource() {
        currentNumber = 0;
        if (beans.size() > 0) {
            bean = beans.get(currentNumber);
            musicService.dataSourceChanged();
            musicService.tag = true;
            music_play.setSelected(true);
            musictotal.setText("/" + time.format(musicService.mediaPlayer.getDuration()));
            song.setText(bean.getText_song());
            if (tag2 == false) {
                handler.post(runnable);
                tag2 = true;
            }

        }else if (beans.size() == 0){
            music_play.setSelected(false);
            song.setText(STRING_NULL);
            current_time.setText(STRING_NULL);
            musictotal.setText(STRING_NULL);
        }
    }

    private void changeBtMusicFragment(String titleName,Context context){
        myFagment = null;
        myFagment = new BtMusicFragment();
        FragmentTransaction transaction= ((MediaActivity)context).getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("titleName",titleName);
        myFagment.setArguments(bundle);
        transaction.replace(R.id.fragment_container,myFagment);
        transaction.commit();
    }

    public static void changeView() {
        bean = beans.get(currentNumber);
        Log.i("ccc","beans"+beans.size()+"   currentnumber"+currentNumber+"musicService"+musicService);
        if(musicService==null){
            return;
        }
        musicAdapter.notifyDataSetChanged();
        musicService.lastOrnext();
        musicService.tag = true;
        music_play.setSelected(true);
        musictotal.setText("/"+time.format(musicService.mediaPlayer.getDuration()));
        song.setText(bean.getText_song());
        if (tag2 == false) {
            handler.post(runnable);
            tag2 = true;
        }
    }

    //进入到音乐列表界面
    private Fragment myFagment;
    private void changeToListFragment() {
        myFagment=null;
        myFagment = new MusicListFragment();
        FragmentTransaction transaction= getActivity().getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putSerializable("fileInfo", beans);
        myFagment.setArguments(bundle);
        transaction.replace(R.id.fragment_container,myFagment);
        transaction.commit();
    }


    //进入到歌词模式
    private Fragment wordsFagment;
    private void changeToWordsFragment() {
        if (beans.size()==0){
            return;
        }
        wordsFagment=null;
        wordsFagment = new MusicwordsFragment();
        FragmentTransaction transaction= getActivity().getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putSerializable("fileInfo", beans);
        wordsFagment.setArguments(bundle);
        transaction.replace(R.id.fragment_container,wordsFagment);
        transaction.commit();
    }
    /*处理歌词begin*/
    private void initLrc() {
        mlrcProcess = new LrcProcess();
        mlrcProcess.readLRC(MusicFragment.bean.getPath());
        //传回处理后的歌词
        lrcList = mlrcProcess.getLrcList();
        music_words.setmLrcList(lrcList);
        Log.d("hct",""+lrcList.size());
        refreshLrcHandler.post(mRunnable);

    }
    public Handler refreshLrcHandler = new Handler();
    //刷新歌词
    Runnable mRunnable=new Runnable() {
        @Override
        public void run() {
            music_words.setIndex(lrcIndex());
            music_words.invalidate();
            refreshLrcHandler.postDelayed(mRunnable,100);
        }
    };
    /**
     * 根据时间获取歌词显示的索引值
     * */
    private int index=0;
    private int duration;

    public int lrcIndex(){

        if (musicService!=null&&musicService.mediaPlayer.isPlaying()){
            currentTime = musicService.mediaPlayer.getCurrentPosition();
            duration = musicService.mediaPlayer.getDuration();
        }if (currentTime<duration){
            for (int i = 0; i <lrcList.size() ; i++) {
                if(i<lrcList.size()-1){
                    if (currentTime<lrcList.get(i).getLrcTime()&&i==0){
                        index=i;
                    }
                    if (currentTime>lrcList.get(i).getLrcTime()&&currentTime<lrcList.get(i+1).getLrcTime()){
                        index=i;
                    }
                    if (i==lrcList.size()-1){
                        index=i;
                    }
                }
            }
        }
        return  index;
    }
    /*处理歌词end*/
}
