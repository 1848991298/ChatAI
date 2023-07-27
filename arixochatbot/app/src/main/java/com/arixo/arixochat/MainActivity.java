package com.arixo.arixochat;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.arixo.arixochat.adapter.ChatAdapter;
import com.arixo.arixochat.bean.ChatSession;
import com.arixo.arixochat.bean.Emojicon;
import com.arixo.arixochat.bean.Faceicon;
import com.arixo.arixochat.bean.Message;
import com.arixo.arixochat.utils.DBHelper;
import com.arixo.arixochat.view.HistoryActivity;
import com.arixo.arixochat.view.PolicyDialog;
import com.arixo.arixochat.view.SettingActivity;
import com.arixo.arixochat.widget.KJChatKeyboard;
import com.arixo.arixochatclient.ArixoChatClient;
import com.arixo.arixochatclient.ArixoChatClientBuilder;
import com.arixo.arixochatclient.ChatEngine;
import com.arixo.arixochatclient.bean.ChatMessage;
import com.arixo.arixochatclient.config.ChatEngineConfig;
import com.arixo.arixochatclient.config.ChatLanguage;
import com.qq.e.ads.banner2.UnifiedBannerADListener;
import com.qq.e.ads.banner2.UnifiedBannerView;
import com.qq.e.comm.util.AdError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements UnifiedBannerADListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private final List<Message> mChatData = new ArrayList<>();
    private ChatAdapter mChatAdapter;

    private final ExecutorService mServiceExecutor = Executors.newSingleThreadExecutor();


    private static final String[] PERMISSIONS_STORAGE = {
            "android.permission.INTERNET",
            "android.permission.ACCESS_NETWORK_STATE",
            "android.permission.ACCESS_WIFI_STATE",
            "android.permission.READ_PHONE_STATE",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.REQUEST_INSTALL_PACKAGES",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.ACCESS_COARSE_LOCATION"};


    ViewGroup mBannerContainer;
    ViewGroup mBannerTOPContainer;
    UnifiedBannerView mBannerView;
    String mCurrentPosId;

    private boolean mProcessingData;

    private KJChatKeyboard mChatToolBox;
    private ListView mRealListView;
    private DrawerLayout mDrawerLayout;
    private final Handler mHandler = new Handler();
    private static int sId;
    private DBHelper mDBHelper;
    private int offset = 0;
    private ArixoChatClient mChatClient;
    private Message mCurrentReplyMsg;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setStatusBar();
        verifyStoragePermissions(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (preferences.getBoolean("isFirstRun", true)) {
            PolicyDialog dialog = new PolicyDialog(this);
            dialog.setListener(new PolicyDialog.PolicyDialogListener() {
                @Override
                public void onPolicyCancel() {
                    preferences.edit().putBoolean("isFirstRun", true).apply();
                    dialog.dismiss();
                    MainActivity.this.finishAffinity();
                }

                @Override
                public void onPolicyConfirm() {
                    preferences.edit().putBoolean("isFirstRun", false).apply();
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
        mBannerContainer = (ViewGroup) this.findViewById(R.id.bannerContainer);
        mBannerTOPContainer = (ViewGroup) this.findViewById(R.id.bannerContainerTop);
        mRealListView = (ListView) findViewById(R.id.chat_listview);
        mRealListView.setSelector(android.R.color.transparent);
        mDBHelper = new DBHelper(this);
        initListView();
        mChatToolBox = findViewById(R.id.chat_msg_input_box);
        mChatToolBox.mBtnMore.setOnClickListener(view -> mDrawerLayout.openDrawer(Gravity.LEFT));

        RelativeLayout layoutBack = findViewById(R.id.back);
        layoutBack.setOnClickListener(view -> mDrawerLayout.closeDrawers());

        RelativeLayout layoutNew = findViewById(R.id.user);
        layoutNew.setOnClickListener(view -> {
            mChatData.clear();
            mChatAdapter.refresh(mChatData);
            Toast.makeText(getApplicationContext(), "新建会话", Toast.LENGTH_SHORT).show();
            ChatSession chatSession = new ChatSession();
            mServiceExecutor.execute(() -> {
                sId = (int) mDBHelper.addChatSession(chatSession);
                Log.d(TAG, "chatSession id: " + sId);
            });
        });

        RelativeLayout layoutMessage = findViewById(R.id.message);
        layoutMessage.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);

        });
        mDrawerLayout = findViewById(R.id.drawerLayout);

        RelativeLayout layoutSettings = findViewById(R.id.setting);
        layoutSettings.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
            Toast.makeText(getApplicationContext(), "settings", Toast.LENGTH_SHORT).show();
        });

        initMessageInputToolBox();


        Intent intent1 = getIntent();
        int session = intent1.getIntExtra("session", -1);
        if (session != -1) {
            updateListView(session);
        } else {
            if (preferences.getInt("session", -1) != -1) {
                updateListView(preferences.getInt("session", -1));
            } else {
                updateListView(mDBHelper.getLastInsertId());
            }
        }

        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            // 这里是刷新时应额外处理的代码。你可以在这里加载更多的数据。
            // 一旦数据加载完毕，记得调用 setRefreshing(false) 来关闭刷新的loading动画
            loadMoreData();
            swipeRefreshLayout.setRefreshing(false);
        });

        initChatClient();
    }

    private void initChatClient() {
        Configuration sysConfig = getResources().getConfiguration();
        Locale locale = sysConfig.locale;

        ChatLanguage language;
        switch (locale.getLanguage()) {
            default:
            case "zh":
                language = ChatLanguage.CHINESE;
                break;
            case "en":
                language = ChatLanguage.ENGLISH;
                break;
        }
        ChatEngineConfig config = ChatEngineConfig.getDefaultConfig(ChatEngine.WEN_DA);
        config.setLanguage(language);

        mChatClient = new ArixoChatClientBuilder()
                .withChatEngine(ChatEngine.WEN_DA)
                .withChatEngineConfig(config)
                .build();

        mChatClient.subscribeResponseMessageHandler(message -> mHandler.post(() -> {
            mProcessingData = !message.isMsgSendDone();
            if (mProcessingData) {
                String resMsg = message.getMessage();
                if (null != resMsg && resMsg.endsWith("正在计算")) {
                    mCurrentReplyMsg.setContent("...");
                } else {
                    mCurrentReplyMsg.setContent(resMsg);
                }
                mChatAdapter.refresh(mChatData);
            } else {
                mDBHelper.addChatRecord(mCurrentReplyMsg, sId);
                offset++;
                mChatToolBox.updateDateText(false);
            }
        }));


    }

    private void loadMoreData() {
        offset += 10;
        List<Message> newMessages = mDBHelper.getMessagesBySessionLimit(sId, offset);
        Collections.reverse(newMessages);
        List<Message> oldMessages = new ArrayList<>(mChatData);
        mChatData.clear();
        mChatData.addAll(newMessages);
        mChatData.addAll(oldMessages);
        mChatAdapter.refresh(mChatData);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBanner().loadAD();

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBannerView != null) {
            mBannerView.destroy();
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mBannerView != null) {
            mBannerView.setLayoutParams(getUnifiedBannerLayoutParams());
        }
    }

    protected UnifiedBannerView getBanner() {
        if (mBannerView != null) {
            return mBannerView;
        }

        mBannerView = new UnifiedBannerView(this, Constants.BANNER_TOP_POS_ID, this);
        mCurrentPosId = Constants.BANNER_TOP_POS_ID;
        mBannerTOPContainer.removeAllViews();
        mBannerTOPContainer.addView(mBannerView, getUnifiedBannerLayoutParams());

        // 默认 30 秒轮播，可以不设置
        this.mBannerView.setRefresh(30);
        mBannerView.setNegativeFeedbackListener(() -> Log.d(TAG, "onComplainSuccess"));
        return this.mBannerView;
    }

    public void setStatusBar() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.parseColor("#fafafa"));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private void verifyStoragePermissions(Activity activity) {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.ACCESS_NETWORK_STATE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initMessageInputToolBox() {
        mChatToolBox.setOnOperationListener(new OnOperationListener() {
            @Override
            public void send(String content) {
                if (mProcessingData) {
                    mProcessingData = false;
                    mChatClient.close();
                    return;
                }

                if (content.equals("")) {
                    Toast.makeText(getApplicationContext(), "输入不得为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                Message message = new Message(Message.MSG_TYPE_TEXT, Message.MSG_STATE_SUCCESS,
                        "Tom", "avatar", "Jerry",
                        "avatar", content, true, true, new Date());
                mChatData.add(message);
                mChatAdapter.refresh(mChatData);
                mServiceExecutor.execute(() -> {
                    if (mDBHelper.getChatSessions().size() < 1) {
                        ChatSession chatSession = new ChatSession();
                        sId = (int) mDBHelper.addChatSession(chatSession);
                        Log.d(TAG, "session_id_new: " + sId);
                    }
                    Log.d(TAG, "session_id add : " + sId);
                    mDBHelper.addChatRecord(message, sId);
                    offset++;
                    mDBHelper.updateTitleIfOnlyOneMessage(sId, message.getContent());
                });
                mCurrentReplyMsg = new Message(Message.MSG_TYPE_TEXT, Message.MSG_STATE_SUCCESS,
                        "Tom", "avatar", "Jerry",
                        "avatar", "", false, true, new Date());

                createReplayMsg(mCurrentReplyMsg);

                runOnUiThread(() -> {
                    mChatToolBox.hideKeyboard(MainActivity.this);
                    mChatToolBox.updateDateText(true);
                });

                mProcessingData = true;

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setMessage(content);
                mChatClient.sendMessage(chatMessage);
            }

            @Override
            public void selectedFace(Faceicon content) {
            }

            @Override
            public void selectedEmoji(Emojicon emoji) {
            }

            @Override
            public void selectedBackSpace(Emojicon back) {
            }

            @Override
            public void selectedFunction(int index) {
            }
        });

        mRealListView.setOnTouchListener(getOnTouchListener());
    }

    private void updateListView(int sessionId) {
        mServiceExecutor.execute(() -> {
            List<Message> history = mDBHelper.getMessagesBySessionLimit(sessionId, offset);
            sId = sessionId;
            Collections.reverse(history);
            if (history.size() > 0) {
                mChatData.clear();
                mChatData.addAll(history);
                mRealListView.setSelection(mChatData.size() - 1);
                mChatAdapter.refresh(mChatData);
                offset = 0;
            }
        });
        preferences.edit().putInt("session", sessionId).apply();
    }

    private void initListView() {
        mChatAdapter = new ChatAdapter(this, mChatData, getOnChatItemClickListener());
        mRealListView.setAdapter(mChatAdapter);
        mChatAdapter.setOnItemShareListener(new ChatAdapter.OnItemShareListener() {
            @Override
            public void onItemShare(String text) {
                shareText(text);
            }

            @Override
            public void onItemDelete(int position, Long id) {
                DeleteText(position, id);
            }
        });
    }

    private void DeleteText(int position, Long id) {
        mChatData.remove(position);
        mChatAdapter.refresh(mChatData);
        mDBHelper.deleteDataById(id);
        offset--;
    }

    private void shareText(String text) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        shareIntent.setType("text/plain");

        if (shareIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(shareIntent, "ArixoChat - Share "));
        }
    }

    private void createReplayMsg(Message message) {
        mServiceExecutor.submit(() -> runOnUiThread(() -> {
            mChatData.add(message);
            mChatAdapter.refresh(mChatData);
        }));
    }

    /**
     * 若软键盘或表情键盘弹起，点击上端空白处应该隐藏输入法键盘
     *
     * @return 会隐藏输入法键盘的触摸事件监听器
     */
    private View.OnTouchListener getOnTouchListener() {
        return (v, event) -> {
            mChatToolBox.hideKeyboard(MainActivity.this);
            v.performClick();
            return false;
        };
    }

    /**
     * @return 聊天列表内存点击事件监听器
     */
    private OnChatItemClickListener getOnChatItemClickListener() {
        return new OnChatItemClickListener() {
            @Override
            public void onPhotoClick(int position) {
                Log.d(TAG, mChatData.get(position).getContent() + "点击图片的");
                Toast.makeText(MainActivity.this, mChatData.get(position).getContent() + "点击图片的", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTextClick(int position) {
                Log.d(TAG, "onTextClick: " + position);
            }

            @Override
            public void onFaceClick(int position) {
            }
        };
    }

    /**
     * banner2.0规定banner宽高比应该为6.4:1 , 开发者可自行设置符合规定宽高比的具体宽度和高度值
     */
    private FrameLayout.LayoutParams getUnifiedBannerLayoutParams() {
        float scale = 6.4F;
        Point screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        return new FrameLayout.LayoutParams(screenSize.x, Math.round(screenSize.x / scale));
    }

    @Override
    public void onNoAD(AdError adError) {
        String msg = String.format(Locale.getDefault(), "onNoAD, error code: %d, error msg: %s",
                adError.getErrorCode(), adError.getErrorMsg());
        Log.w(TAG, msg);
    }

    @Override
    public void onADReceive() {
        if (mBannerView != null) {
            Log.i(TAG, "onADReceive" + ", ECPM: " + mBannerView.getECPM() + ", ECPMLevel: "
                    + mBannerView.getECPMLevel() + ", adNetWorkName: " + mBannerView.getAdNetWorkName()
                    + ", testExtraInfo:" + mBannerView.getExtraInfo().get("mp")
                    + ", request_id:" + mBannerView.getExtraInfo().get("request_id"));
        }
    }

    @Override
    public void onADExposure() {
        Log.i(TAG, "onADExposure");
    }

    @Override
    public void onADClosed() {
        Log.i(TAG, "onADClosed");
    }

    @Override
    public void onADClicked() {
        Log.i(TAG, "onADClicked : ");
    }

    @Override
    public void onADLeftApplication() {
        Log.i(TAG, "onADLeftApplication");
    }

    /**
     * 聊天列表中对内容的点击事件监听
     */
    public interface OnChatItemClickListener {
        void onPhotoClick(int position);

        void onTextClick(int position);

        void onFaceClick(int position);
    }

}