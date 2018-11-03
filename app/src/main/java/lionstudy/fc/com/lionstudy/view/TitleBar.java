package lionstudy.fc.com.lionstudy.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import lionstudy.fc.com.lionstudy.R;

/**
 * Created by Administrator on 2018/2/11.
 */

public class TitleBar extends LinearLayout implements View.OnClickListener{
    //用view的好处是 不管在布局文件件将控件改变成什么 都能够实例化
    private View tv_seache;
    private View rl_game;
    private View iv_record;

    private Context context;
    /**
     * 在代码中实例化该类时调用
     * @param context
     */
    public TitleBar(Context context) {
        this(context,null);
    }

    /**
     * 当在布局文件使用该类的时候，android系统通过该方法实例化该类
     * @param context
     * @param attrs
     */
    public TitleBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    /**
     * 当需要设置样式的时候 可以使用该构造方法实例化该类
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public TitleBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    /**
     * 当布局文件加载完成时调用
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //得到孩子的实例
        tv_seache = getChildAt(1);
        rl_game = getChildAt(2);
        iv_record = getChildAt(3);

        tv_seache.setOnClickListener(this);
        rl_game.setOnClickListener(this);
        iv_record.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_seache://搜索
                Toast.makeText(context,"搜索",Toast.LENGTH_SHORT).show();
                break;
            case R.id.rl_game://游戏
                Toast.makeText(context,"游戏",Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_record://记录
                Toast.makeText(context,"记录",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
