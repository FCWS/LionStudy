package lionstudy.fc.com.lionstudy.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.MotionEvent;

import lionstudy.fc.com.lionstudy.R;

/**
 * Created by Administrator on 2018/2/6.
 */

public class SplashActivity extends Activity {
    private Handler handler = new Handler();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        //handle要拿到全局声明 否则会早成漏洞(退出后还会启动)
        //注意该方法是在主线程中执行的
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startMainActivity();
            }
        },3000);
    }

    //因为倒计时和点击都可以启动该方法 就会造成该方法多次执行，所以我们要控制方法只能执行一次
    //可以将MainActivity设置为单例模式 或者通过变量控制方法的执行次数 这里我们利用单利模式
    private void startMainActivity() {
        Intent intent = new Intent(SplashActivity.this,MainActivity.class);
        startActivity(intent);
        finish();//关闭当前页面
    }

    //该方法是在倒计时的时候可以通过点击屏幕快速启动程序
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        startMainActivity();
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);//当参数为空的时候 可以移除所有回调的消息
        super.onDestroy();
    }
}
