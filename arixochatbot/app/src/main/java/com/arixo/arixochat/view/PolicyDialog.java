/* -------------------------------------------------------------------------------
     Copyright (C) 2021, Matrix Zero  CO. LTD. All Rights Reserved

     Revision History:
     
     Bug/Feature ID 
     ------------------
     BugID/FeatureID
     
     Author 
     ------------------
     Xin Zhao
          
     Modification Date 
     ------------------
     2023/7/18
     
     Description 
     ------------------ 
     brief description

----------------------------------------------------------------------------------*/
package com.arixo.arixochat.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.arixo.arixochat.R;

public class PolicyDialog extends Dialog {
    private static final String TAG = PolicyDialog.class.getSimpleName();
    private Context mContext;

    public PolicyDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    public interface PolicyDialogListener {
        void onPolicyCancel();

        void onPolicyConfirm();
    }

    private PolicyDialogListener mListener;

    public void setListener(PolicyDialogListener listener) {
        this.mListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_policy);
        // 将弹窗背景色设置成透明
//        getWindow().setBackgroundDrawableResource(R.color.transparent);
        setCanceledOnTouchOutside(false);
        setCancelable(false);

        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.CENTER);//设置dialog显示居中
        //dialogWindow.setWindowAnimations();//设置动画效果

        TextView cancel = findViewById(R.id.policy_cancel);
        TextView confirm = findViewById(R.id.policy_confirm);
        TextView content = findViewById(R.id.policy_content);

        // 首先创建一个SpannableString对象
        SpannableString spannableString = new SpannableString("新用户将默认自动注册零矩开放平台,并请同意《零矩用户服务协议》和《零矩隐私政策》");

        // 创建一个ClickableSpan
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent intent = new Intent(mContext, WebActivity.class);
                intent.putExtra("web", 5);
                intent.putExtra("name", "零矩开放平台用户服务协议");
                mContext.startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);  // 设置文字下划线
            }
        };
        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent intent = new Intent(mContext, WebActivity.class);
                intent.putExtra("web", 4);
                intent.putExtra("name", "零矩隐私政策");
                mContext.startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);  // 设置文字下划线
            }
        };
        // 设置点击的文字范围
        spannableString.setSpan(clickableSpan, 21, 31, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(clickableSpan1, 32, 40, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        content.setText(spannableString);
        content.setMovementMethod(LinkMovementMethod.getInstance()); // 设置可以点击
        content.setHighlightColor(Color.TRANSPARENT); // 移除点击后的背景色
        cancel.setOnClickListener(view -> mListener.onPolicyCancel());
        confirm.setOnClickListener(view -> mListener.onPolicyConfirm());
    }

}
