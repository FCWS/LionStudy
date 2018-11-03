package lionstudy.fc.com.lionstudy.activity;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

import lionstudy.fc.com.lionstudy.R;
import lionstudy.fc.com.lionstudy.base.BasePager;
import lionstudy.fc.com.lionstudy.base.ReplaceFragment;
import lionstudy.fc.com.lionstudy.pager.AudioPager;
import lionstudy.fc.com.lionstudy.pager.NetAudioPager;
import lionstudy.fc.com.lionstudy.pager.NetVideoPager;
import lionstudy.fc.com.lionstudy.pager.VideoPager;

/**
 * Created by Administrator on 2018/2/6.
 */

public class MainActivity extends FragmentActivity {
    private FrameLayout fl_main_content;
    private RadioGroup rg_bottom_tag;
    public List<BasePager> basePagers;
    private int position;
    private BasePager basePager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fl_main_content = (FrameLayout) findViewById(R.id.fl_main_content);
        rg_bottom_tag = (RadioGroup) findViewById(R.id.rg_bottom_tag);
        basePagers = new ArrayList<>();
        basePagers.add(new VideoPager(this));
        basePagers.add(new AudioPager(this));
        basePagers.add(new NetVideoPager(this));
        basePagers.add(new NetAudioPager(this));
        rg_bottom_tag.setOnCheckedChangeListener(new MyOnOnCheckedChangeListener());
        rg_bottom_tag.check(R.id.rb_video);
    }


    class MyOnOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
            switch (checkedId){
                default:
                    position = 0;
                    break;
                case R.id.rb_audio:
                    position = 1;
                    break;
                case R.id.rb_net_video:
                    position = 2;
                    break;
                case R.id.rb_net_audio:
                    position = 3;
                    break;
            }
            setFragment();
        }
    }

    //把页面添加到Fragment中
    private void setFragment() {
        //1、得到一个管理者
        FragmentManager manager = getSupportFragmentManager();
        //2、开启事物
        FragmentTransaction ft = manager.beginTransaction();
        //3、替换内容
        ft.replace(R.id.fl_main_content,new ReplaceFragment(getBasePager()));
        //4、提交事物
        ft.commit();
    }
    //根据位置得到对应的页面  上传GitHub测试 hello  22
    public BasePager getBasePager() {
        BasePager basePager =basePagers.get(position);
        if (basePager != null && !basePager.isInitData){
            basePager.initData();//联网请求
            basePager.isInitData = true;
        }
        return basePager;
    }

}
