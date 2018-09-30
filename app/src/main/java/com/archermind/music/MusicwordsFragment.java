package com.archermind.music;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import com.archermind.music.bean.LrcContent;
import com.archermind.music.bean.MusicBean;
import com.archermind.music.utils.LrcProcess;
import com.archermind.music.view.LrcView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MusicwordsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MusicwordsFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View view;
    private LrcProcess mlrcProcess;
    private List<LrcContent> lrcList;
    private LrcView music_words;
    private int currentTime;
    private TextView song_words;
    private TextView singer_words;
    private SeekBar seekBar_words;
    private ImageButton play_words;
    private ImageButton last_words;
    private ImageButton next_words;
    private ImageButton mode_words;
    private ImageButton folder_words;

    public MusicwordsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MusicwordsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MusicwordsFragment newInstance(String param1, String param2) {
        MusicwordsFragment fragment = new MusicwordsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    private ArrayList<MusicBean> fileInfo;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fileInfo = (ArrayList<MusicBean>) getArguments().getSerializable("fileInfo");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_musicwords, container, false);
        return view;
    }

    private void initView() {
        music_words = (LrcView) view.findViewById(R.id.music_words);
        song_words = (TextView) view.findViewById(R.id.song_words);
        singer_words = (TextView) view.findViewById(R.id.singer_words);
        singer_words.setText(MusicFragment.bean.getText_singer());
        song_words.setText(MusicFragment.bean.getText_song());
        seekBar_words = (SeekBar) view.findViewById(R.id.seekBar_words);
        seekBar_words.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser==true){
                    MusicFragment.musicService.mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        play_words = (ImageButton) view.findViewById(R.id.play_words);
        if(MusicFragment.musicService.mediaPlayer.isPlaying()){
            MusicFragment.musicService.tag = true;
            play_words.setSelected(true);
        }else {
            MusicFragment.musicService.tag = false;
            play_words.setSelected(false);
        }
        play_words.setOnClickListener(this);
        mode_words = (ImageButton) view.findViewById(R.id.mode_words);
        mode_words.setBackground(getResources().getDrawable(MusicFragment.images[MusicFragment.modeNumber]));
        mode_words.setOnClickListener(this);
        next_words = (ImageButton) view.findViewById(R.id.next_words);
        next_words.setOnClickListener(this);
        last_words = (ImageButton) view.findViewById(R.id.last_words);
        last_words.setOnClickListener(this);
        folder_words = (ImageButton) view.findViewById(R.id.folder_words);
        folder_words.setOnClickListener(this);
    }

    private void initLrc() {
        mlrcProcess = new LrcProcess();
        mlrcProcess.readLRC(MusicFragment.bean.getPath());
        //传回处理后的歌词
        lrcList = mlrcProcess.getLrcList();
        music_words.setmLrcList(lrcList);
        Log.d("hct",""+lrcList.size());
        handler.post(mRunnable);

    }
    public Handler handler = new Handler();
    //刷新歌词
    Runnable mRunnable=new Runnable() {
        @Override
        public void run() {
            singer_words.setText(MusicFragment.bean.getText_singer());
            song_words.setText(MusicFragment.bean.getText_song());
            seekBar_words.setMax(MusicFragment.musicService.mediaPlayer.getDuration());
            seekBar_words.setProgress(MusicFragment.musicService.mediaPlayer.getCurrentPosition());
            music_words.setIndex(lrcIndex());
            music_words.invalidate();
            handler.postDelayed(mRunnable,100);
        }
    };
/**
 * 根据时间获取歌词显示的索引值
 * */
private int index=0;
    private int duration;
    
  public int lrcIndex(){
 if (MusicFragment.musicService.mediaPlayer.isPlaying()){
    currentTime = MusicFragment.musicService.mediaPlayer.getCurrentPosition();
    duration = MusicFragment.musicService.mediaPlayer.getDuration();
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.play_words:
                if (MusicFragment.musicService.mediaPlayer.isPlaying()){
                    MusicFragment.musicService.tag = false;
                    play_words.setSelected(false);
                }else {
                    MusicFragment.musicService.tag = true;
                    play_words.setSelected(true);
                }
                MusicFragment.musicService.playOrPause();

                break;
            case R.id.last_words:
                if (MusicFragment.currentNumber>0) {
                    MusicFragment.currentNumber = MusicFragment.currentNumber - 1;

                }else {
                    MusicFragment.currentNumber=fileInfo.size()-1;
                }
                MusicFragment.bean=fileInfo.get(MusicFragment.currentNumber);
                MusicFragment.musicService.lastOrnext();
                changeView();
                initLrc();

                break;
            case R.id.next_words:
                if(MusicFragment.currentNumber<fileInfo.size()-1){
                    MusicFragment.currentNumber=MusicFragment.currentNumber+1;
                }else {
                    MusicFragment.currentNumber=0;
                }
                MusicFragment.bean=fileInfo.get(MusicFragment.currentNumber);
                MusicFragment.musicService.lastOrnext();
                changeView();
                initLrc();
                break;
            case R.id.mode_words:
                if (MusicFragment.modeNumber<2){
                MusicFragment.modeNumber=MusicFragment.modeNumber+1;
            }else {
                MusicFragment.modeNumber=0;
            }
            mode_words.setBackground(getResources().getDrawable(MusicFragment.images[MusicFragment.modeNumber]));
                break;
            case R.id.folder_words:
                changeToListFragment();
                break;
        }

    }
    private void changeView() {
        MusicFragment.musicService.tag = true;
        play_words.setSelected(true);
        singer_words.setText(MusicFragment.bean.getText_singer());
        song_words.setText(MusicFragment.bean.getText_song());
    }
    @Override
    public void onResume() {
        super.onResume();
        initView();
        initLrc();
        MediaActivity.musicFragmentTab=7;

    }
    //进入到音乐列表界面
    private Fragment myFagment;
    private void changeToListFragment() {
        myFagment=null;
        myFagment = new MusicListFragment();
        FragmentTransaction transaction= getActivity().getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putSerializable("fileInfo", fileInfo);
        myFagment.setArguments(bundle);
        transaction.replace(R.id.fragment_container,myFagment);
        transaction.commit();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
