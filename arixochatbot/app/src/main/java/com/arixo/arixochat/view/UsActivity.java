package com.arixo.arixochat.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.arixo.arixochat.databinding.ActivityUsBinding;

public class UsActivity extends AppCompatActivity {

    private ActivityUsBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();

        mBinding = ActivityUsBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        mBinding.usBack.setOnClickListener(view -> finish());
        Intent intent = new Intent(UsActivity.this, WebActivity.class);
        mBinding.usOne.setOnClickListener(view -> {
            intent.putExtra("web", 1);
            intent.putExtra("name", "隐私政策摘要");
        });
        mBinding.usTwo.setOnClickListener(view -> {
            Intent intent1 = new Intent(UsActivity.this, PolicyActivity.class);
            intent1.putExtra("name", "服务协议&隐私政策");
            startActivity(intent1);
        });
        mBinding.usThree.setOnClickListener(view -> {
            intent.putExtra("web", 8);
            intent.putExtra("name", "个人信息收集清单");
            startActivity(intent);
        });

        mBinding.usFour.setOnClickListener(view -> {
            intent.putExtra("web", 2);
            intent.putExtra("name", "第三方共享清单及SDK目录");
            startActivity(intent);
        });

        mBinding.usUpdate.setOnClickListener(view -> {
            Toast.makeText(this, "新版本敬请期待", Toast.LENGTH_SHORT).show();
        });
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