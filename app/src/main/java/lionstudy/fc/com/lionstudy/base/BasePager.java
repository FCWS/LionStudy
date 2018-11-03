package lionstudy.fc.com.lionstudy.base;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;

/**
 * Created by Administrator on 2018/2/7.
 */

public abstract class BasePager {

    public final Context context;
    public View rootview;
    public boolean isInitData;

    public BasePager(Context context){
        this.context = context;
        rootview = initView();
    }

    /**
     * 强制孩子实现 实现特定的效果
     * @return
     */
    public abstract View initView();

    /*
    初始化数据 联网获取网络数据 或者绑定数据时要重写该方法
     */
    public void initData(){

    }
}
