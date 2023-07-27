package com.arixo.arixochat.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.arixo.arixochat.databinding.ActivityPolicyBinding;


public class PolicyActivity extends AppCompatActivity {

    private ActivityPolicyBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityPolicyBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        mBinding.policyBack.setOnClickListener(view -> finish());
        setStatusBar();
        Intent intent = new Intent(PolicyActivity.this, WebActivity.class);
        mBinding.policyOne.setOnClickListener(view -> {
            intent.putExtra("web", 3);
            intent.putExtra("name", "零矩用户体验规则");
            startActivity(intent);
        });
        mBinding.policyTwo.setOnClickListener(view -> {
            intent.putExtra("web", 4);
            intent.putExtra("name", "零矩隐私政策");
            startActivity(intent);
        });

        mBinding.policyThree.setOnClickListener(view -> {
            intent.putExtra("web", 5);
            intent.putExtra("name", "零矩开放平台用户服务协议");
            startActivity(intent);
        });
        mBinding.policyFour.setOnClickListener(view -> {
            intent.putExtra("web", 6);
            intent.putExtra("name", "零矩开放平台隐私政策");
            startActivity(intent);
        });
        mBinding.policyFive.setOnClickListener(view -> {
            intent.putExtra("web", 7);
            intent.putExtra("name", "儿童隐私政策");
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