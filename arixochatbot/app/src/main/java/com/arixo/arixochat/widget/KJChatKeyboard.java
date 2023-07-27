package com.arixo.arixochat.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.arixo.arixochat.OnOperationListener;
import com.arixo.arixochat.R;
import com.arixo.arixochat.SoftKeyboardStateHelper;

/**
 * 控件主界面
 *
 * @author kymjs (http://www.kymjs.com/)
 */
public class KJChatKeyboard extends RelativeLayout implements
        SoftKeyboardStateHelper.SoftKeyboardStateListener {

    private static final String TAG = KJChatKeyboard.class.getSimpleName();

    public static final int LAYOUT_TYPE_HIDE = 0;
    public static final int LAYOUT_TYPE_FACE = 1;
    public static final int LAYOUT_TYPE_MORE = 2;

    /**
     * 最上层输入框
     */
    private EditText mEtMsg;
    public ImageView mBtnMore;
    private Button mBtnSend;

    private Context mContext;
    private OnOperationListener mListener;
    private SoftKeyboardStateHelper mKeyboardHelper;

    public KJChatKeyboard(Context context) {
        super(context);
        init(context);
    }

    public KJChatKeyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public KJChatKeyboard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        View root = View.inflate(context, R.layout.chat_tool_box, null);
        this.addView(root);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initData();
        this.initWidget();
    }

    private void initData() {
        mKeyboardHelper = new SoftKeyboardStateHelper(((Activity) getContext())
                .getWindow().getDecorView());
        mKeyboardHelper.addSoftKeyboardStateListener(this);
    }

    private void initWidget() {
        mEtMsg = findViewById(R.id.toolbox_et_message);
        mBtnSend = findViewById(R.id.toolbox_btn_send);
        mBtnMore = findViewById(R.id.toolbox_btn_more);
        mBtnSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    String content = mEtMsg.getText().toString();
                    mListener.send(content);
                    mEtMsg.setText(null);
                }
            }
        });
        // 点击表情按钮旁边的加号
        mBtnMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: More button");
            }
        });
        // 点击消息输入框
        mEtMsg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    public void onSoftKeyboardOpened(int keyboardHeightInPx) {
    }

    @Override
    public void onSoftKeyboardClosed() {
    }

    public EditText getEditTextBox() {
        return mEtMsg;
    }

    public void showLayout() {
        hideKeyboard(this.mContext);
    }

    public void updateDateText(boolean receiving) {
        if (receiving) {
            mBtnSend.setBackgroundColor(mContext.getColor(R.color.bg_click));
            mBtnSend.setText(mContext.getString(R.string.stop));
        } else {
            mBtnSend.setBackgroundColor(mContext.getColor(R.color.bg_light));
            mBtnSend.setText(mContext.getString(R.string.send));
        }
    }

    /**
     * 隐藏软键盘
     */
    public void hideKeyboard(Context context) {
        Activity activity = (Activity) context;
        if (activity != null) {
            InputMethodManager imm = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isActive() && activity.getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(activity.getCurrentFocus()
                        .getWindowToken(), 0);
            }
        }
    }

    /**
     * 显示软键盘
     */
    public static void showKeyboard(Context context) {
        Activity activity = (Activity) context;
        if (activity != null) {
            InputMethodManager imm = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInputFromInputMethod(activity.getCurrentFocus()
                    .getWindowToken(), 0);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    public OnOperationListener getOnOperationListener() {
        return mListener;
    }

    public void setOnOperationListener(OnOperationListener onOperationListener) {
        this.mListener = onOperationListener;
    }

}
