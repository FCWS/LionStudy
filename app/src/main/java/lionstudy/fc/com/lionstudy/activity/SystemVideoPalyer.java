package lionstudy.fc.com.lionstudy.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import lionstudy.fc.com.lionstudy.Bean.MediaItem;
import lionstudy.fc.com.lionstudy.R;
import lionstudy.fc.com.lionstudy.utils.Utils;
import lionstudy.fc.com.lionstudy.view.VideoView;

/**系统播放器
 * Created by Administrator on 2018/2/11.
 */

public class SystemVideoPalyer extends Activity implements View.OnClickListener{

    private boolean isUserSystem = true;

    //视频更新进度
    private static final int PROGRESS = 1;

    //隐藏控制面板
    private static final int HIDE_MIDIACONTROLLER = 2;

    //显示网速
    private static final int SHOW_SPEED = 3;

    //全屏
    private static final int FULL_SCREEN = 1;

    //默认
    private static final int DEFAULT_SCREEN = 2;
    private VideoView videoView;
    private Uri uri;
    private Utils utils;
    private MyReceiver receiver;//监听电量变化的广播

    private LinearLayout llTop;
    private TextView tvName;
    private ImageView ivBattery;
    private Button btnVoice;
    private SeekBar seekbarVoice;
    private Button btnSwitchPlayer;
    private LinearLayout llBottomm;
    private RelativeLayout media_controller;
    private TextView tvCurrentTime;
    private TextView tv_system_time;
    private SeekBar seekbarVideo;
    private TextView tvDuration;
    private Button btnExit;
    private Button btnVideoPre;
    private Button btnVideoStartPause;
    private Button btnVideoNext;
    private Button btnVideoSwitchScreen;
    private TextView tv_buffer_netspeed;
    private LinearLayout ll_buffer;
    private TextView tv_loading_netspeed;
    private LinearLayout ll_loading;


    private int systemTime;

    //传入的视频列表
    private ArrayList<MediaItem> mediaItems;

    //要播放列表的位置
    private int position;

    //定义手势识别器
    private GestureDetector detector;

    //是否显示控制面板
    private boolean isshowMediaController = false;

    //是否全屏
    private boolean isFullScreen = false;

    //屏幕高
    private int screenWidth = 0;

    //屏幕高
    private int screenHeight = 0;

    /**
     * 真实视频的宽和高
     */
    private int mVideoWidth;
    private int mVideoHeight;

    //调节声音
    private AudioManager am;

    //当前音量
    private int currentVoice;

    //最大音量0-15
    private int maxVoice;

    //是否是静音
    private boolean isMute = false;

    //是否是网络uri
    private boolean isNetUri;

    //上一次播放进度
    private int precurrentposition ;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-02-13 13:00:14 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        videoView = (VideoView) findViewById(R.id.videoview);
        llTop = (LinearLayout)findViewById( R.id.ll_top );
        tvName = (TextView)findViewById( R.id.tv_name );
        ivBattery = (ImageView)findViewById( R.id.iv_battery );
        btnVoice = (Button)findViewById( R.id.btn_voice );
        seekbarVoice = (SeekBar)findViewById( R.id.seekbar_voice );
        btnSwitchPlayer = (Button)findViewById( R.id.btn_switch_player );
        llBottomm = (LinearLayout)findViewById( R.id.ll_bottomm );
        media_controller = (RelativeLayout) findViewById( R.id.media_controller );
        tvCurrentTime = (TextView)findViewById( R.id.tv_current_time );
        tv_system_time = (TextView)findViewById( R.id.tv_system_time );
        seekbarVideo = (SeekBar)findViewById( R.id.seekbar_video );
        tvDuration = (TextView)findViewById( R.id.tv_duration );
        btnExit = (Button)findViewById( R.id.btn_exit );
        btnVideoPre = (Button)findViewById( R.id.btn_video_pre );
        btnVideoStartPause = (Button)findViewById( R.id.btn_video_start_pause );
        btnVideoNext = (Button)findViewById( R.id.btn_video_next );
        btnVideoSwitchScreen = (Button)findViewById( R.id.btn_video_switch_screen );
        ll_buffer = findViewById(R.id.ll_buffer);
        tv_buffer_netspeed = findViewById(R.id.tv_buffer_netspeed);
        tv_loading_netspeed = findViewById(R.id.tv_loading_netspeed);
        ll_loading = findViewById(R.id.ll_loading);


        btnVoice.setOnClickListener( this );
        btnSwitchPlayer.setOnClickListener( this );
        btnExit.setOnClickListener( this );
        btnVideoPre.setOnClickListener( this );
        btnVideoStartPause.setOnClickListener( this );
        btnVideoNext.setOnClickListener( this );
        btnVideoSwitchScreen.setOnClickListener( this );

        //设置最大音量  和seekBar关联
        seekbarVoice.setMax(maxVoice);

        //设置当前进度（音量）
        seekbarVoice.setProgress(currentVoice);

        //开始更新网速
        handler.sendEmptyMessage(SHOW_SPEED);

    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2018-02-13 13:00:14 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if ( v == btnVoice ) {
            isMute = !isMute;
            // Handle clicks for btnVoice
            updataVoice(currentVoice,isMute);
        } else if ( v == btnSwitchPlayer ) {
            // Handle clicks for btnSwitchPlayer
            showSwichPlayerDialog();
        } else if ( v == btnExit ) {
            // Handle clicks for btnExit
            finish();
        } else if ( v == btnVideoPre ) {
            // Handle clicks for btnVideoPre
            playPreVideo();
        } else if ( v == btnVideoStartPause ) {
            // Handle clicks for btnVideoStartPause
            startAndPause();
        } else if ( v == btnVideoNext ) {
            // Handle clicks for btnVideoNext
            playNextVideo();
        } else if ( v == btnVideoSwitchScreen ) {
            // Handle clicks for btnVideoSwitchScreen
            setFullScreenAndDefault();
        }

        handler.removeMessages(HIDE_MIDIACONTROLLER);
        handler.sendEmptyMessageDelayed(HIDE_MIDIACONTROLLER,5000);

    }


    private void showSwichPlayerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示");
        builder.setMessage("当您播放视频，有声音没有画面时，请切换万能播放器播放");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startVitamioPlayer();
            }
        });
        builder.setNegativeButton("取消",null);
        builder.show();
    }

    private void startAndPause() {
        if (videoView.isPlaying()){
            //视频播放时 需要设置为暂停
            videoView.pause();
            //按钮状态设置为设置为播放
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_start_selector);
        }else{
            videoView.start();
            btnVideoStartPause.setBackgroundResource(R.drawable.btn_video_pause_selector);
        }
    }

    /**
     * 播放上一个视频
     */
    private void playPreVideo() {
        if (mediaItems != null && mediaItems.size() > 0){
            //播放上一个
            position --;
            if (position >= 0){

                ll_loading.setVisibility(View.VISIBLE);

                MediaItem mediaItem = mediaItems.get(position);
                tvName.setText(mediaItem.getName());
                isNetUri = utils.isNetUri(mediaItem.getData());
                videoView.setVideoPath(mediaItem.getData());

                //设置按钮状态
                setButtonState();
            }
        }else if(uri != null){
            //上一个 下一个按钮设置灰色并且不可以点击吧

        }
    }

    /**
     * 播放下一个视频
     */
    private void playNextVideo() {
        if (mediaItems != null && mediaItems.size() > 0){
            //播放下一个
            position ++;
            if (position < mediaItems.size()){

                ll_loading.setVisibility(View.VISIBLE);

                MediaItem mediaItem = mediaItems.get(position);
                tvName.setText(mediaItem.getName());
                isNetUri = utils.isNetUri(mediaItem.getData());
                videoView.setVideoPath(mediaItem.getData());

                //设置按钮状态
                setButtonState();
            }
        }else if(uri != null){
            //上一个 下一个按钮设置灰色并且不可以点击吧

        }
    }

    private void setButtonState() {
        if (mediaItems != null && mediaItems.size() > 0){
            if (mediaItems.size() == 1){
                setEnable(false);
            }else if (mediaItems.size() == 2){
                if (position == 0){
                    btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                    btnVideoPre.setEnabled(false);
                    btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
                    btnVideoNext.setEnabled(true);
                }else  if (position == mediaItems.size()-1){
                    btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                    btnVideoNext.setEnabled(false);
                    btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
                    btnVideoPre.setEnabled(true);
                }
            }else {
                if (position == 0){
                    btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
                    btnVideoPre.setEnabled(false);
                }else  if (position == mediaItems.size()-1){
                    btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
                    btnVideoNext.setEnabled(false);
                }else {
                    setEnable(true);
                }
            }
        }else  if(uri != null){
            //两个按钮设置灰色
            setEnable(false);
        }
    }

    private void setEnable(boolean isEnable) {
        if (isEnable){
            btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
            btnVideoPre.setEnabled(true);
            btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
            btnVideoNext.setEnabled(true);
        }else {
            btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
            btnVideoPre.setEnabled(false);
            btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
            btnVideoNext.setEnabled(false);
        }

    }


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_SPEED://显示网速
                    //的到网速
                    String netSpeed = utils.getNetSpeed(SystemVideoPalyer.this);

                    //显示网络速度
                    tv_loading_netspeed.setText("玩命加载中..."+netSpeed);
                    tv_buffer_netspeed.setText("缓冲中..."+netSpeed);

                    //两秒调用一次
                    handler.removeMessages(SHOW_SPEED);
                    handler.sendEmptyMessageDelayed(SHOW_SPEED, 2000);
                    break;
                case PROGRESS:
                    //1、得到当前视频的播放进度
                    int currentposition = videoView.getCurrentPosition();
                    //2、seekbar.setprogress(当前进度)
                    seekbarVideo.setProgress(currentposition);
                    //更新播放进度
                    tvCurrentTime.setText(utils.stringForTime(currentposition));
                    //设置系统时间
                    tv_system_time.setText(getSystemTime());

                    //缓存进度的更新
                    if (isNetUri){
                        //只有网络视频才有缓冲
                        int buffer = videoView.getBufferPercentage();//缓冲值0-100  看源码
                        int totalBuffer = buffer * seekbarVideo.getMax();
                        int setSecondaryProgress = totalBuffer / 100;
                        seekbarVideo.setSecondaryProgress(setSecondaryProgress);
                    }else {
                        //本地视频没有缓冲
                        seekbarVideo.setSecondaryProgress(0);
                    }

                    //监听卡
                    if (!isUserSystem && videoView.isPlaying()){
                        if (videoView.isPlaying()){
                            int buffer = currentposition - precurrentposition;
                            if (buffer < 500){
                                //视频卡顿
                                ll_buffer.setVisibility(View.VISIBLE);
                            }else {
                                //视频不卡
                                ll_buffer.setVisibility(View.GONE);
                            }
                        }else {
                            ll_buffer.setVisibility(View.GONE);
                        }
                    }

                    precurrentposition = currentposition;

                    //3、每秒更新一次
                    handler.removeMessages(PROGRESS);
                    sendEmptyMessageDelayed(PROGRESS, 1000);
                    break;
                case HIDE_MIDIACONTROLLER:
                    hideMediaController();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_video_player);

        initData();

        findViews();

        setListener();

        getData();

        setData();

       /* //设置控制面板
        videoView.setMediaController(new MediaController(this));*/
    }

    private void getData(){
        //得到播放地址
        uri = getIntent().getData();
        mediaItems = (ArrayList<MediaItem>) getIntent().getSerializableExtra("vediolist");
        position = getIntent().getIntExtra("position",0);

    }

    private void setData(){

        if (mediaItems != null && mediaItems.size() > 0){
            MediaItem mediaItem = mediaItems.get(position);
            tvName.setText(mediaItem.getName());//设置名称
            isNetUri = utils.isNetUri(mediaItem.getData());
            videoView.setVideoPath(mediaItem.getData());
        }else if (uri != null){
            tvName.setText(uri.toString());//设置名称
            isNetUri = utils.isNetUri(uri.toString());
            videoView.setVideoURI(uri);
        }else {
            Toast.makeText(this, "没有传递数据", Toast.LENGTH_SHORT).show();
        }

        setButtonState();
    }

    private void initData(){
        utils = new Utils();
        //注册点亮广播
        receiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        //当电量变化时 发出广播
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver,intentFilter);

        //实例化手势识别器  并且重载双击 单击  长按
        detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){

            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                startAndPause();
                //Toast.makeText(SystemVideoPalyer.this, "我被长按了", Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
               // Toast.makeText(SystemVideoPalyer.this, "我被双击了", Toast.LENGTH_SHORT).show();
                setFullScreenAndDefault();
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
//                Toast.makeText(SystemVideoPalyer.this, "我被单击了", Toast.LENGTH_SHORT).show();
                if (isshowMediaController){
                    //隐藏
                    hideMediaController();
                    //把隐藏的消息移除
                    handler.removeMessages(HIDE_MIDIACONTROLLER);
                }else {
                    //显示
                    showMediaController();
                    //发消息隐藏
                    handler.sendEmptyMessageDelayed(HIDE_MIDIACONTROLLER,4000);
                }
                return super.onSingleTapConfirmed(e);
            }
        });

        //得到屏幕宽高

        //过时的方法
       /* screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        screenHeight = getWindowManager().getDefaultDisplay().getHeight();*/

       //新方法
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;


        //得到音量
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVoice = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVoice = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

    }

    private void setFullScreenAndDefault() {
        if (isFullScreen){
            //默认
            setVideoType(DEFAULT_SCREEN);
        }else{
            //全屏
            setVideoType(FULL_SCREEN);
        }
    }

    private void setVideoType(int defaultScreen) {
        switch (defaultScreen){
            case FULL_SCREEN://全屏
                //1.设置屏幕画面的大小   屏幕有多大就是多大
                videoView.setVideosize(screenWidth,screenHeight);
                //2.设置按钮的状态--- 默认
                btnVideoSwitchScreen.setBackgroundResource(R.drawable.btn_video_switch_screen_default_selector);
                isFullScreen = true;
                break;
            case DEFAULT_SCREEN://默认
                //1.设置屏幕画面的大小
                int width = screenWidth;
                int height = screenHeight;
                // for compatibility, we adjust size based on aspect ratio
                if ( mVideoWidth * height  < width * mVideoHeight ) {
                    //Log.i("@@@", "image too wide, correcting");
                    width = height * mVideoWidth / mVideoHeight;
                } else if ( mVideoWidth * height  > width * mVideoHeight ) {
                    //Log.i("@@@", "image too tall, correcting");
                    height = width * mVideoHeight / mVideoWidth;
                }
                //2.设置按钮的状态--- 全屏
                videoView.setVideosize(width,height);
                btnVideoSwitchScreen.setBackgroundResource(R.drawable.btn_video_switch_screen_full_selector);
                isFullScreen = false;
                break;
        }
    }

    public void setBattery(int level) {
        if (level<=0){
            ivBattery.setImageResource(R.drawable.ic_battery_0);
        }else if (level <= 10){
            ivBattery.setImageResource(R.drawable.ic_battery_10);
        }else if (level <= 20){
            ivBattery.setImageResource(R.drawable.ic_battery_20);
        }else if (level <= 40){
            ivBattery.setImageResource(R.drawable.ic_battery_40);
        }else if (level <= 60){
            ivBattery.setImageResource(R.drawable.ic_battery_60);
        }else if (level <= 80){
            ivBattery.setImageResource(R.drawable.ic_battery_80);
        }else if (level <= 100){
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }else {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }
    }

    /**
     * 得到系统时间
     * @return
     */
    public String getSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }

    class MyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level",0);//0--100
            setBattery(level);
        }
    }
    private void setListener(){
        //准备好的监听
        videoView.setOnPreparedListener(new MyOnPreparedListener());

        //播放出错的监听
        videoView.setOnErrorListener(new MyOnErrorListener());

        //播放完成监听
        videoView.setOnCompletionListener(new MyOnCompletionListener());

        //设置seekbar状态变化的监听
        seekbarVideo.setOnSeekBarChangeListener(new VideoOnSeekBarchangeListener());

        //监听声音
        seekbarVoice.setOnSeekBarChangeListener(new VoiceOnSeekBarchangeListener());

        if (isUserSystem){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
                //监听视频播放卡顿
                videoView.setOnInfoListener(new MyOnInfoListener());
            }
        }
    }

    class MyOnInfoListener implements MediaPlayer.OnInfoListener{

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            switch (what){
                case MediaPlayer.MEDIA_INFO_BUFFERING_START://视频卡了 拖动卡
                    ll_buffer.setVisibility(View.VISIBLE);
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END://视频卡了 拖动卡
                    ll_buffer.setVisibility(View.GONE);
                    break;
            }
            return true;
        }
    }

    class VoiceOnSeekBarchangeListener implements SeekBar.OnSeekBarChangeListener{

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser){
                if (progress > 0){
                    isMute = false;
                }else {
                    isMute = true;
                }
                updataVoice(progress,false);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(HIDE_MIDIACONTROLLER);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(HIDE_MIDIACONTROLLER,5000);
        }
    }

    //设置音量的大小
    private void updataVoice(int progress, boolean isMute) {
        if (isMute){
            am.setStreamVolume(AudioManager.STREAM_MUSIC,0,0);//第三个参数为1 调用系统音量进度条   为0 不调用
            seekbarVoice.setProgress(0);
        }else {
            am.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);//第三个参数为1 调用系统音量进度条   为0 不调用
            seekbarVoice.setProgress(progress);
            currentVoice = progress;
        }
    }

    class VideoOnSeekBarchangeListener implements SeekBar.OnSeekBarChangeListener{

        /**
         * 当手指滑动的时候会引起seekbar进度的变化 回调该方法
         * @param seekBar
         * @param progress
         * @param fromUser 如果是用户引起的为true 不是用户引起的为false
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser == true){
                videoView.seekTo(progress);
            }
        }

        /**
         * 当手指触碰的时候回调
         * @param seekBar
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(HIDE_MIDIACONTROLLER);
        }

        /**
         * 当手指离开的时候回调
         * @param seekBar
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(HIDE_MIDIACONTROLLER,5000);
        }
    }

    class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mp) {

            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();

            videoView.start();//开始播放
            int duration = videoView.getDuration();//视频的总时长
            seekbarVideo.setMax(duration);
            tvDuration.setText(utils.stringForTime(duration));

            hideMediaController();//隐藏面板
            handler.sendEmptyMessage(PROGRESS);
            //videoView.setVideosize(mp.getVideoWidth(),mp.getVideoHeight());

            //屏幕的默认播放
            setVideoType(DEFAULT_SCREEN);

            /*mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    //此方法可以用来监听用户拖动操作 可以用来检测视频的欢迎度
                }
            });*/

            //吧加载页面消失
            ll_loading.setVisibility(View.GONE);
        }
    }
    class MyOnErrorListener implements MediaPlayer.OnErrorListener{

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            //1.播放视频格式不支持 --- 跳转到万能播放器
            startVitamioPlayer();
            //2.播放网络视频 网络中断 --- 1.如果网络确实断了可以提示 ；2.网络断断续续 重新播放
            //3.播放的时候中间有空白 ----- 重新下载

            Toast.makeText(SystemVideoPalyer.this,"播放出错了",Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    /**
     * 1.把数据按照原来传入万能播放器
     * 2.关闭系统播放器
     */
    private void startVitamioPlayer() {
        if (videoView != null){
            videoView.stopPlayback();
        }
        Intent intent = new Intent(SystemVideoPalyer.this,VitamioVideoPlayer.class);
        if (mediaItems != null && mediaItems.size() > 0 ){
            //传递播放列表数据
            Bundle bundle = new Bundle();
            bundle.putSerializable("vediolist",mediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position",position);
        }else if(uri != null){
            intent.setData(uri);
        }
        startActivity(intent);
        finish();//关闭页面
    }

    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener{

        @Override
        public void onCompletion(MediaPlayer mp) {
            playNextVideo();
            Toast.makeText(SystemVideoPalyer.this,"播放完成"+uri,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        //释放资源的时候 先释放子类 再释放父类  就是写在super前面
        if (receiver != null){
            unregisterReceiver(receiver);
        }
        super.onDestroy();

    }


    private float startY;

    /**
     * 屏幕的高
     */
    private float touchRang;

    /**
     * 当按下时的音量  不能直接使用currentVoice
     */
    private int mVol;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //把事件传递给手势识别器
        detector.onTouchEvent(event);

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN://手指按下
                //1.按下记录值
                startY = event.getY();
                mVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                touchRang = Math.min(screenHeight,screenWidth);//取最小值是因为横竖屏切换
                handler.removeMessages(HIDE_MIDIACONTROLLER);
                break;
            case MotionEvent.ACTION_MOVE://手指移动
                //2.移动记录值
                float endY = event.getY();
                float distanceY = startY - endY;
                //改变声音 = (滑动屏幕距离：总距离)*音量最大值
                float dalta = (distanceY/touchRang)*maxVoice;
                //最终声音 = 原来的音量 + 改变声音
                int voice = (int) Math.min(Math.max(mVol+dalta,0),maxVoice);
                if (dalta != 0){
                    isMute = false;
                    updataVoice(voice, false);
                }
                break;
            case MotionEvent.ACTION_UP://手指离开
                handler.sendEmptyMessageDelayed(HIDE_MIDIACONTROLLER,5000);
                break;

        }

        return super.onTouchEvent(event);
    }

    /**
     * 显示控制面板
     */
    private void showMediaController(){
        media_controller.setVisibility(View.VISIBLE);
        isshowMediaController = true;
    }

    /**
     * 隐藏控制面板
     */
    private void hideMediaController(){
        media_controller.setVisibility(View.GONE);
        isshowMediaController = false;
    }

    /**
     * 监听物理键  实现声音的调节大小
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
            currentVoice -- ;
            updataVoice(currentVoice, false);
            handler.removeMessages(HIDE_MIDIACONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_MIDIACONTROLLER,50000);
            return true;   //如果返回false 系统音量进度条也会显示
        }else if(keyCode == KeyEvent.KEYCODE_VOLUME_UP){
            currentVoice ++ ;
            updataVoice(currentVoice, false);
            handler.removeMessages(HIDE_MIDIACONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_MIDIACONTROLLER,50000);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
