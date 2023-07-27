package com.arixo.arixochat.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.arixo.arixochat.MainActivity;
import com.arixo.arixochat.databinding.ActivitySettingBinding;


public class SettingActivity extends AppCompatActivity {

    private ActivitySettingBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_setting);
        mBinding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        setStatusBar();
        mBinding.settingBack.setOnClickListener(view -> {
                Intent intent = new Intent(SettingActivity.this, MainActivity.class);
            startActivity(intent);
        });

        mBinding.useOne.setOnClickListener(view ->{
            Intent intent = new Intent(SettingActivity.this, CommonActivity.class);
            startActivity(intent);
        });
        mBinding.useThree.setOnClickListener(view -> {
            Intent intent = new Intent(SettingActivity.this, UsActivity.class);
            startActivity(intent);
        });
        Intent intent = new Intent(SettingActivity.this, WebActivity.class);

        mBinding.policyOne.setOnClickListener(view -> {
            intent.putExtra("web", 1);
            intent.putExtra("name", "隐私政策摘要");
            startActivity(intent);
        });
        mBinding.policyTwo.setOnClickListener(view -> {
            Intent intent1 = new Intent(SettingActivity.this, PolicyActivity.class);
            intent1.putExtra("name", "服务协议&隐私政策");
            startActivity(intent1);
        });
        mBinding.policyThree.setOnClickListener(view -> {
            intent.putExtra("web", 8);
            intent.putExtra("name", "个人信息收集清单");
            startActivity(intent);
        });
        mBinding.policyFour.setOnClickListener(view -> {
            intent.putExtra("web", 2);
            intent.putExtra("name", "第三方共享清单及SDK目录");
            startActivity(intent);
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