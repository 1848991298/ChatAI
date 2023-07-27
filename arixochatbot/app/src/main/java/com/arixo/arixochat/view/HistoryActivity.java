package com.arixo.arixochat.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.arixo.arixochat.MainActivity;
import com.arixo.arixochat.R;
import com.arixo.arixochat.adapter.HistoryAdapter;
import com.arixo.arixochat.bean.ChatSession;
import com.arixo.arixochat.utils.DBHelper;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistoryActivity extends AppCompatActivity {
    private static final String TAG = HistoryActivity.class.getSimpleName();
    private final List<ChatSession> mChatSessions = new ArrayList<>();
    private DBHelper mDBHelper;
    private final ExecutorService mServiceExecutor = Executors.newSingleThreadExecutor();
    private SwipeMenuListView mListView;
    private HistoryAdapter mHistoryAdapter;
    private CustomizedDialog mCustomizedDialog;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        setStatusBar();
        mListView = findViewById(R.id.lv_history);
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.history_back);
        layout.setOnClickListener(view -> finish());
        mDBHelper = new DBHelper(this);
        initListView();

        ImageView delete = (ImageView) findViewById(R.id.search_delete);
        delete.setVisibility(View.GONE);

        EditText searchBox = (EditText) findViewById(R.id.edt_search);
        searchBox.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                String search = searchBox.getText().toString();
                Log.d(TAG, "searchByTitle :" + search);
                mServiceExecutor.execute(() -> {
                    List<ChatSession> list = mDBHelper.searchByTitle(search);
                    mChatSessions.clear();
                    if (list.size() > 0) {
                        mChatSessions.addAll(list);
                    }
                });
                mHistoryAdapter.refresh(mChatSessions);
            }
            return true;
        });

        searchBox.setOnFocusChangeListener((view, b) -> {
            if (b) {
                delete.setVisibility(View.VISIBLE);
            } else {
                delete.setVisibility(View.GONE);
            }
        });

        delete.setOnClickListener(view -> {
            searchBox.setText("");
            initListView();
        });

        SwipeMenuCreator creator = menu -> {
            //创建弹窗按钮
            SwipeMenuItem openItem = new SwipeMenuItem(getApplicationContext());
            openItem.setBackground(new ColorDrawable(Color.rgb(70, 148, 255))); //设置背景
            openItem.setWidth(dp2px(80)); //设置宽度
            openItem.setIcon(R.drawable.change24); //设置图标
            menu.addMenuItem(openItem); //添加到菜单

            //创建删除按钮
            SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
            deleteItem.setBackground(new ColorDrawable(Color.rgb(255, 106, 105))); //设置背景
            deleteItem.setWidth(dp2px(80)); //设置宽度
            deleteItem.setIcon(R.drawable.delete24); //设置图标
            menu.addMenuItem(deleteItem); //添加到菜单
        };

        mListView.setMenuCreator(creator);

        mListView.setOnMenuItemClickListener((position, menu, index) -> {
            switch (index) {
                case 0:
                    Toast.makeText(getApplicationContext(), "update", Toast.LENGTH_SHORT).show();
                    mCustomizedDialog = new CustomizedDialog(this, mChatSessions.get(position));
                    mCustomizedDialog.setOnItemListener(new CustomizedDialog.OnItemListener() {
                        @Override
                        public void onItemConfirm(String message, int id) {
                            mServiceExecutor.execute(() -> {
                                mDBHelper.updateTitle(id, message);
                                List<ChatSession> history = mDBHelper.getChatSessions();
                                if (history.size() > 0) {
                                    mChatSessions.clear();
                                    mChatSessions.addAll(history);
                                }
                            });
                            mHistoryAdapter.refresh(mChatSessions);
                            mCustomizedDialog.dismiss();
                        }

                        @Override
                        public void onItemCancel() {
                            Toast.makeText(getApplicationContext(), "内容不得为空", Toast.LENGTH_SHORT).show();
                        }
                    });
                    mCustomizedDialog.show();
                    break;
                case 1:
                    Toast.makeText(getApplicationContext(), "delete", Toast.LENGTH_SHORT).show();
                    mServiceExecutor.execute(() -> {
                        mDBHelper.deleteSessionById(mChatSessions.get(position).getId());
                        List<ChatSession> history = mDBHelper.getChatSessions();
                        if (history.size() > 0) {
                            mChatSessions.clear();
                            mChatSessions.addAll(history);
                        }
                    });
                    new Handler().postDelayed(() -> mHistoryAdapter.refresh(mChatSessions), 500);
                    Log.d(TAG, "delete session: " + mChatSessions.get(position));
                    break;
            }
            return false;
        });

        mListView.setOnItemClickListener((parent, view, position, id) -> {
            if (position == mListView.getLastVisiblePosition()) {
                layout.setVisibility(View.GONE);
            }
            TextView textView = (TextView) view.findViewById(R.id.tv_ID);
            int text = Integer.parseInt(textView.getText().toString());
            Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
            intent.putExtra("session", text);
            startActivity(intent);
        });

        mListView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // 获取当前触摸点的位置
                float x = event.getX();
                float y = event.getY();
                // 获取 ListView 最后一个可见的 item 的位置
                int lastVisiblePosition = mListView.getLastVisiblePosition();

                // 如果触摸的位置是 ListView 中的最后一个可见 item
                if (isTouchPointInView(mListView.getChildAt(mListView.getChildCount() - 1), (int) x, (int) y)) {
                    layout.setVisibility(View.GONE);
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                layout.setVisibility(View.VISIBLE);
            }
            mListView.performClick();
            return false;
        });

    }

    public boolean isTouchPointInView(View view, int x, int y) {
        if (view == null) {
            return false;
        }
        int[] loc = new int[2];
        view.getLocationOnScreen(loc);
        int left = loc[0];
        int top = loc[1];
        int right = left + view.getWidth();
        int bottom = top + view.getHeight();
        return (y >= top) && (y <= bottom) && (x >= left) && (x <= right);
    }

    private void initListView() {
        mServiceExecutor.execute(() -> {
            List<ChatSession> history = mDBHelper.getChatSessions();
            if (history.size() > 0) {
                mChatSessions.clear();
                mChatSessions.addAll(history);
            }
        });
        mHistoryAdapter = new HistoryAdapter(this, mChatSessions);
        mListView.setAdapter(mHistoryAdapter);
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

    public int dp2px(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }


}