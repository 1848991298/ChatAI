package com.arixo.arixochat.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.arixo.arixochat.R;

public class WebActivity extends AppCompatActivity {
    private WebView mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        setStatusBar();
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.web_back);
        layout.setOnClickListener(view -> finish());
        mView = (WebView) findViewById(R.id.wv_web);
        TextView title = (TextView) findViewById(R.id.tv_web);
        WebSettings webSettings = mView.getSettings();

        Intent intent = getIntent();
        int web = intent.getIntExtra("web", 0);
        String name = intent.getStringExtra("name");
        title.setText(name);
        switch (web) {
            case 1:
                mView.loadUrl("https://h5.matrixz.cn/policy/yszczy.html");
                break;
            case 2:
                mView.loadUrl("https://h5.matrixz.cn/policy/dsfxxgxqd.html");
                break;
            case 3:
                mView.loadUrl("https://h5.matrixz.cn/policy/yhtygz.html");
                break;
            case 4:
                mView.loadUrl("https://h5.matrixz.cn/policy/yszc.html");
                break;
            case 5:
                mView.loadUrl("https://h5.matrixz.cn/policy/kfptyhfwxy.html");
                break;
            case 6:
                mView.loadUrl("https://h5.matrixz.cn/policy/kfptyszc.html");
                break;
            case 7:
//                mView.loadUrl("https://h5.matrixz.cn/policy/dsfxxgxqd.html");
                break;
            default:
                mView.loadUrl("https://www.baidu.com/");
                break;
        }

        //设置不用系统浏览器打开,直接显示在当前view
        mView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });


        //设置WebChromeClient类
        mView.setWebChromeClient(new WebChromeClient() {


            //获取网站标题
            @Override
            public void onReceivedTitle(WebView view, String title) {
                System.out.println("标题在这里");
//                mTitle.setText(title);
            }


            //获取加载进度
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
//                if (newProgress < 100) {
//                    String progress = newProgress + "%";
//                    loading.setText(progress);
//                } else if (newProgress == 100) {
//                    String progress = newProgress + "%";
//                    loading.setText(progress);
//                }
            }
        });


        //设置WebViewClient类
        mView.setWebViewClient(new WebViewClient() {
            //设置加载前的函数
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                System.out.println("开始加载了");
//                beginLoading.setText("开始加载了");

            }

            //设置结束加载函数
            @Override
            public void onPageFinished(WebView view, String url) {
//                endLoading.setText("结束加载了");

            }
        });
    }

    //点击返回上一页面而不是退出浏览器
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mView.canGoBack()) {
            mView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (mView != null) {
            mView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mView.clearHistory();

            ((ViewGroup) mView.getParent()).removeView(mView);
            mView.destroy();
            mView = null;
        }
        super.onDestroy();
    }

    public void setStatusBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.parseColor("#fafafa"));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

}