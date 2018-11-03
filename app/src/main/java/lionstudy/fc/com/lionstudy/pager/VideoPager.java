package lionstudy.fc.com.lionstudy.pager;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;


import lionstudy.fc.com.lionstudy.Bean.MediaItem;
import lionstudy.fc.com.lionstudy.R;
import lionstudy.fc.com.lionstudy.activity.SystemVideoPalyer;
import lionstudy.fc.com.lionstudy.adapter.VideoPagerAdapter;
import lionstudy.fc.com.lionstudy.base.BasePager;
import lionstudy.fc.com.lionstudy.utils.LogUtil;


/**
 * Created by Administrator on 2018/2/7.
 */

public class VideoPager extends BasePager {

    private ListView listView;
    private TextView nomidea;
    private ProgressBar pbloading;


    //数据集合
    private ArrayList<MediaItem> mediaItems;

    public VideoPager(Context context) {
        super(context);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mediaItems != null && mediaItems.size()>0){
                //设置适配器
                VideoPagerAdapter videoPagerAdapter = new VideoPagerAdapter(context,mediaItems);
                listView.setAdapter(videoPagerAdapter);
                //文本隐藏
                nomidea.setVisibility(View.GONE);
            }else{
                //把文本显示
                nomidea.setVisibility(View.VISIBLE);
            }
            pbloading.setVisibility(View.GONE);
        }
    };

    @Override
    public View initView() {
        LogUtil.e("本地视频页面被初始化了");
        View view = View.inflate(context, R.layout.video_pager,null);

        listView = (ListView) view.findViewById(R.id.listview);
        nomidea = (TextView) view.findViewById(R.id.nomidea);
        pbloading = (ProgressBar) view.findViewById(R.id.pbloading);

        listView.setOnItemClickListener(new MyOnItemClickListener());

        return view;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("本地视频页面的数据被初始化了");
        //加载本地视频数据
        getDataFromLocal();
    }

    /**
     * 从本地sd卡拿到数据
     * 1、遍历sdcard 后缀名（用不多）
     * 2、从内容提供者获取
     * 3、如果是6.0的系统 需要加上动态获取读取sd卡的权限
     */
    public void getDataFromLocal() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //SystemClock.sleep(500);
                mediaItems = new ArrayList<MediaItem>();
                //1、根据上下文拿到内容解析着
                ContentResolver resolver = context.getContentResolver();
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {
                        MediaStore.Video.Media.DISPLAY_NAME,//视频文件的sd卡的名称
                        MediaStore.Video.Media.DURATION,//时长
                        MediaStore.Video.Media.SIZE,//视频大小
                        MediaStore.Video.Media.DATA,//绝对地址
                        MediaStore.Video.Media.ARTIST//歌曲的演唱者

                };
                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if (cursor != null){
                    while (cursor.moveToNext()){

                        MediaItem mediaItem = new MediaItem();

                        String name =  cursor.getString(0);
                        mediaItem.setName(name);

                        long duration = cursor.getLong(1);
                        mediaItem.setDuration(duration);

                        long size = cursor.getLong(2);
                        mediaItem.setSize(size);

                        String data = cursor.getString(3);
                        mediaItem.setData(data);

                        String artist = cursor.getString(4);
                        mediaItem.setArtist(artist);

                        mediaItems.add(mediaItem);
                    }
                    cursor.close();
                }
                //发消息
                handler.sendEmptyMessage(10);

            }
        }){}.start();
    }

    class MyOnItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MediaItem mediaItem = mediaItems.get(position);

            // //调用自己写的播放器 显示意图 一个播放地址
          /*  Intent intent = new Intent(context,SystemVideoPalyer.class);
            intent.setDataAndType(Uri.parse(mediaItem.getData()),"video*//*");
            //intent.setDataAndType(Uri.parse("http://www.yimimao.com/xjp/sangebangfeiqitiaoxin/player-0-0.html"),"video*//*");
            context.startActivity(intent);*/

            //传递播放列表数据
            Intent intent = new Intent(context,SystemVideoPalyer.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("vediolist",mediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position",position);
            context.startActivity(intent);

        }
    }

}
