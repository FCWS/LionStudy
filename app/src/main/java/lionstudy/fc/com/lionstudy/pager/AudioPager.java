package lionstudy.fc.com.lionstudy.pager;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import lionstudy.fc.com.lionstudy.base.BasePager;
import lionstudy.fc.com.lionstudy.utils.LogUtil;

/**
 * Created by Administrator on 2018/2/7.
 */

public class AudioPager extends BasePager {

    private TextView textView;
    public AudioPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        LogUtil.e("本地音乐页面被初始化了");
        textView = new TextView(context);
        textView.setTextSize(25);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.RED);
        return textView;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("本地音乐页面的数据被初始化了");
        textView.setText("本地音乐页面");
    }
}
