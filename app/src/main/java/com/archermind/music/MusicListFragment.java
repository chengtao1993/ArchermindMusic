package com.archermind.music;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.archermind.music.MediaActivity;
import com.archermind.music.R;
import com.archermind.music.adapter.MusicAdapter;
import com.archermind.music.bean.MusicBean;
import com.archermind.music.utils.ScanMusic;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MusicListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MusicListFragment extends Fragment implements ListView.OnItemClickListener ,View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;
    private ListView music_lv;
    public static TextView singer_list;
    public static TextView song;
    private ImageButton mode_list;
    private ImageButton last_list;
    private ImageButton next_list;
    public static ImageButton play_list;
    private SeekBar seekbar_list;
    public static MusicAdapter musicAdapter;

    private static TextView data_source;
    private ImageButton source_switch;
    private AlertDialog sourceDialog;

    public MusicListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MusicListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MusicListFragment newInstance(String param1, String param2) {
        MusicListFragment fragment = new MusicListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public static ArrayList<MusicBean> fileInfo;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_music_list, container, false);
        data_source = view.findViewById(R.id.data_source);
        data_source.setOnClickListener(this);
        data_source.setText(MediaActivity.current_source_name);
        fileInfo = ScanMusic.getData(getActivity(),MediaActivity.current_source_path);
        source_switch = view.findViewById(R.id.ib);
        source_switch.setOnClickListener(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        MediaActivity.musicFragmentTab=6;
        initView();
    }

    private void initView() {
        music_lv = (ListView) view.findViewById(R.id.music_lv);
        musicAdapter = new MusicAdapter(getContext(), fileInfo);
        music_lv.setAdapter(musicAdapter);
        music_lv.setOnItemClickListener(this);
        singer_list = (TextView) view.findViewById(R.id.singer_list);
        singer_list.setText(MusicFragment.bean.getAlbum());
        song = (TextView) view.findViewById(R.id.song_list);
        song.setText(MusicFragment.bean.getText_song());
        mode_list = (ImageButton) view.findViewById(R.id.mode_list);
        mode_list.setOnClickListener(this);
        mode_list.setBackground(getResources().getDrawable(MusicFragment.images[MusicFragment.modeNumber]));
        last_list = (ImageButton) view.findViewById(R.id.last_list);
        last_list.setOnClickListener(this);
        next_list = (ImageButton) view.findViewById(R.id.next_list);
        next_list.setOnClickListener(this);
        play_list = (ImageButton) view.findViewById(R.id.play_list);
        if(MusicFragment.musicService.mediaPlayer.isPlaying()){
            MusicFragment.musicService.tag = true;
            play_list.setSelected(true);
        }else {
            MusicFragment.musicService.tag = false;
            play_list.setSelected(false);
        }
        seekbar_list = (SeekBar) view.findViewById(R.id.seekBar_list);
        seekbar_list.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

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
        singer_list.setText(MusicFragment.bean.getText_singer());
        song.setText(MusicFragment.bean.getText_song());
        play_list.setOnClickListener(this);
        handler.post(mrunnable);
    }
    //  通过 Handler 更新 UI 上的组件状态
    public Handler handler = new Handler();
    public Runnable mrunnable = new Runnable() {
        @Override
        public void run() {
            seekbar_list.setProgress(MusicFragment.musicService.mediaPlayer.getCurrentPosition());
            seekbar_list.setMax(MusicFragment.musicService.mediaPlayer.getDuration());
            //获取其时长
            //song.setText(MusicFragment.bean.getText_song());
            //singer_list.setText(MusicFragment.bean.getText_singer());
            handler.postDelayed(mrunnable, 200);
        }
    };

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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MusicFragment.currentNumber=position;
        changeView();
    }

    public static void changeView() {
        MusicFragment.bean=fileInfo.get(MusicFragment.currentNumber);
        MusicFragment.musicService.lastOrnext();
        MusicFragment.musicService.tag = true;
        play_list.setSelected(true);
        singer_list.setText(MusicFragment.bean.getText_singer());
        song.setText(MusicFragment.bean.getText_song());
        musicAdapter.notifyDataSetChanged();
    }

    public static void usbPullOut(){
        data_source.setText(MediaActivity.current_source_name);
        musicAdapter.setData(fileInfo);
        musicAdapter.notifyDataSetChanged();
        if (fileInfo.size() != 0) {
            MusicFragment.bean = fileInfo.get(0);
            play_list.setSelected(false);
            singer_list.setText(MusicFragment.bean.getText_singer());
            song.setText(MusicFragment.bean.getText_song());
        }else {
            MusicFragment.bean = null;
            play_list.setSelected(false);
            singer_list.setText("null");
            song.setText("null");
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mode_list:
                if (MusicFragment.modeNumber<2){
                    MusicFragment.modeNumber=MusicFragment.modeNumber+1;
                }else {
                    MusicFragment.modeNumber=0;
                }
                mode_list.setBackground(getResources().getDrawable(MusicFragment.images[MusicFragment.modeNumber]));

                break;
            case R.id.last_list:
                if (MusicFragment.currentNumber>0) {
                    MusicFragment.currentNumber = MusicFragment.currentNumber - 1;

                }else {
                   MusicFragment.currentNumber=fileInfo.size()-1;
                }
                changeView();
                break;
            case R.id.next_list:
                if(MusicFragment.currentNumber<fileInfo.size()-1){
                    MusicFragment.currentNumber=MusicFragment.currentNumber+1;
                }else {
                    MusicFragment.currentNumber=0;
                }
                MusicFragment.bean=fileInfo.get(MusicFragment.currentNumber);
                MusicFragment.musicService.lastOrnext();
                changeView();
                break;
            case R.id.play_list:
                if (MusicFragment.musicService.mediaPlayer.isPlaying()){
                    MusicFragment.musicService.tag = false;
                    play_list.setSelected(false);
                }else {
                    MusicFragment.musicService.tag = true;
                    play_list.setSelected(true);
                }
                MusicFragment.musicService.playOrPause();

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
                    }else {
                        Drawable left = getActivity().getDrawable(R.drawable.usb);
                        item_title.setCompoundDrawablesWithIntrinsicBounds(left,null,null,null);
                    }
                    item_title.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (data_source.getText().equals(((TextView)v).getText())){

                            }else {
                                MediaActivity.current_source_name = (String) ((TextView)v).getText();
                                MediaActivity.current_source_path = (String) MediaActivity.name_path.get(MediaActivity.current_source_name);
                                fileInfo = ScanMusic.getData(getActivity(),MediaActivity.current_source_path);
                                Log.i("ccc",""+fileInfo.size());
                                if (fileInfo==null||fileInfo.size()==0){
                                    Toast.makeText(getActivity(),"歌曲列表为空",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                data_source.setText(MediaActivity.current_source_name);
                                if (fileInfo.size()<=MusicFragment.currentNumber){
                                    MusicFragment.currentNumber=0;
                                }
                                changeView();
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
                            ((MediaActivity)getActivity()).musicFragment.changToBtMusic(getActivity());
                        }
                        sourceDialog.dismiss();
                    }
                });
                source_layout.addView(item);
                sourceDialog.setView(source_layout);
                sourceDialog.show();
                Window dialogWindow1 = sourceDialog.getWindow();
                WindowManager.LayoutParams lp1 = dialogWindow1.getAttributes();
                lp1.height = 400;
                lp1.width = 942;
                lp1.gravity = Gravity.TOP|Gravity.START;
                lp1.x = 68;
                lp1.y = 126;
                dialogWindow1.setAttributes(lp1);
                break;
        }

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
