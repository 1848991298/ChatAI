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
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.arixo.arixochat.R;
import com.arixo.arixochat.bean.ChatSession;

public class CustomizedDialog extends Dialog {
    private static final String TAG = CustomizedDialog.class.getSimpleName();
    private EditText mEditText;
    private final ChatSession mChatSession;

    public interface OnItemListener {
        void onItemConfirm(String message, int id);

        void onItemCancel();
    }

    private OnItemListener mListener;

    public void setOnItemListener(OnItemListener onItemListener) {
        this.mListener = onItemListener;
    }

    public CustomizedDialog(@NonNull Context context, ChatSession session) {
        super(context);
        this.mChatSession = session;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_tip);
        // 将弹窗背景色设置成透明
//        getWindow().setBackgroundDrawableResource(R.color.transparent);
        // 弹窗外部蒙层不可取消弹窗
//        setCanceledOnTouchOutside(false);
        setCanceledOnTouchOutside(true);//点击外部Dialog消失

        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.CENTER);//设置dialog显示居中
        //dialogWindow.setWindowAnimations();//设置动画效果

        TextView cancel = findViewById(R.id.tv_cancel);
        TextView confirm = findViewById(R.id.tv_confirm);
        mEditText = findViewById(R.id.et_content);

        mEditText.setText(mChatSession.getTitle());
        cancel.setOnClickListener(view -> dismiss());
        confirm.setOnClickListener(view -> {
            if (mListener != null && mEditText.getText().length() > 0) {
                mListener.onItemConfirm(mEditText.getText().toString(), mChatSession.getId());
            } else if (mListener != null && mEditText.getText().length() <= 0) {
                mListener.onItemCancel();
            }
        });

    }

}
