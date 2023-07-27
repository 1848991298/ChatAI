/*
 * Copyright (c) 2015, 张涛.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.arixo.arixochat.adapter;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.arixo.arixochat.ArixoChatApplication;
import com.arixo.arixochat.MainActivity.OnChatItemClickListener;
import com.arixo.arixochat.R;
import com.arixo.arixochat.bean.Message;
import com.arixo.arixochat.utils.DateUtils;
import com.arixo.arixochat.utils.UrlUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChatAdapter extends BaseAdapter {

    private final Context mContext;
    private List<Message> mDatas;
    private final OnChatItemClickListener mListener;
    private String mMessage = null;
    private PopupWindow mPopupWindow;
    private int downX;
    private int downY;

    public interface OnItemShareListener {
        void onItemShare(String text);

        void onItemDelete(int position, Long id);
    }

    private OnItemShareListener onItemShareListener;

    public void setOnItemShareListener(OnItemShareListener onItemShareListener) {
        this.onItemShareListener = onItemShareListener;
    }

    public ChatAdapter(Context mContext, List<Message> datas, OnChatItemClickListener listener) {
        this.mContext = mContext;
        if (datas == null) {
            datas = new ArrayList<>(0);
        }
        this.mDatas = datas;
        this.mListener = listener;
    }

    public void refresh(List<Message> datas) {
        if (datas == null) {
            datas = new ArrayList<>(0);
        }
        this.mDatas = datas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return mDatas.get(position).getIsSend() ? 1 : 0;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View getView(final int position, View v, ViewGroup parent) {
        final ViewHolder holder;
        final Message data = mDatas.get(position);
        if (v == null) {
            holder = new ViewHolder();
            if (data.getIsSend()) {
                v = View.inflate(mContext, R.layout.chat_item_list_right, null);
            } else {
                v = View.inflate(mContext, R.layout.chat_item_list_left, null);
            }
            holder.layout_content = (RelativeLayout) v.findViewById(R.id.chat_item_layout_content);
//            holder.img_avatar = (ImageView) v.findViewById(R.id.chat_item_avatar);
            holder.img_avatar = (ImageView) v.findViewById(R.id.img_avatar);
            holder.img_chatimage = (ImageView) v.findViewById(R.id.chat_item_content_image);
            holder.img_sendfail = (ImageView) v.findViewById(R.id.chat_item_fail);
            holder.progress = (ProgressBar) v.findViewById(R.id.chat_item_progress);
            holder.tv_chatcontent = (TextView) v.findViewById(R.id.chat_item_content_text);
            holder.tv_date = (TextView) v.findViewById(R.id.chat_item_content_data);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

//        holder.tv_date.setText(StringUtils.friendlyTime(StringUtils.getDataTime("yyyy-MM-dd " +"HH:mm:ss")));
        holder.tv_date.setVisibility(View.VISIBLE);

        Calendar messageCal = Calendar.getInstance();
        messageCal.setTime(data.getTime());
        Calendar today = Calendar.getInstance();


        if (messageCal.get(Calendar.YEAR) == today.get(Calendar.YEAR)) {
            if (messageCal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                holder.tv_date.setText(DateUtils.formatToday(data.getTime()));
            } else if (messageCal.get(Calendar.WEEK_OF_YEAR) == today.get(Calendar.WEEK_OF_YEAR)) {
                if (messageCal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) - 1) {
                    String timeStr = DateUtils.formatToday(data.getTime());
                    holder.tv_date.setText(mContext.getString(R.string.yesterday, timeStr));
                } else {
                    holder.tv_date.setText(DateUtils.formatThisWeek(data.getTime()));
                }
            } else {
                holder.tv_date.setText(DateUtils.formatOtherDay(data.getTime()));
            }
        } else {
            holder.tv_date.setText(DateUtils.formatOtherYear(data.getTime()));
        }

        //如果是文本类型，则隐藏图片，如果是图片则隐藏文本
        if (data.getType() == Message.MSG_TYPE_TEXT) {
            holder.img_chatimage.setVisibility(View.GONE);
            holder.tv_chatcontent.setVisibility(View.VISIBLE);
            if (data.getContent().contains("href")) {
                UrlUtils.handleHtmlText(holder.tv_chatcontent, data.getContent());
            } else {
                UrlUtils.handleText(holder.tv_chatcontent, data.getContent());
            }
        } else {
            holder.tv_chatcontent.setVisibility(View.GONE);
            holder.img_chatimage.setVisibility(View.VISIBLE);
            String url = data.getContent();
//            if (url != null && url.startsWith("http")) {
//                Glide.with(mContext).load(url).into(holder.img_chatimage);
//            } else {
//                Glide.with(mContext).load(new File(url)).into(holder.img_chatimage);
//            }
        }

        //如果是表情或图片，则不显示气泡，如果是图片则显示气泡
        if (data.getType() != Message.MSG_TYPE_TEXT) {
            holder.layout_content.setBackgroundResource(android.R.color.transparent);
        } else {
            if (data.getIsSend()) {
                holder.layout_content.setBackgroundResource(R.drawable.chat_to_bg_selector);
            } else {
                holder.layout_content.setBackgroundResource(R.drawable.chat_from_bg_selector);
            }
        }

        holder.tv_chatcontent.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                // 手指离开屏幕，视为点击行为
                view.performClick();
            }
            downX = (int) motionEvent.getRawX();
            downY = (int) motionEvent.getRawY();
            return false;
        });

        holder.tv_chatcontent.setOnLongClickListener(view -> {
            mMessage = holder.tv_chatcontent.getText().toString();
            showPopupWindow(holder.tv_chatcontent, position, data.getId());
            return false;
        });
        holder.tv_chatcontent.performClick();
//        显示头像
        if (data.getIsSend()) {
//            Glide.with(mContext).load(data.getFromUserAvatar()).placeholder(R.drawable.arixolinkexpert).into(holder.img_avatar);
            Glide.with(mContext)
                    .load(R.drawable.user_avatar)
                    .circleCrop()
                    .into(holder.img_avatar);
        }
//        else {
//            Glide.with(mContext).load(data.getToUserAvatar()).placeholder(R.drawable.default_head).into(holder.img_avatar);
//            Glide.with(mContext)
//                    .load(R.drawable.arixolink_avatar)
//                    .transform(new CircleCrop())
//                    .into(holder.img_avatar);
//        }

        if (mListener != null) {
            holder.tv_chatcontent.setOnClickListener(v1 -> mListener.onTextClick(position));
            holder.img_chatimage.setOnClickListener(v12 -> {
                switch (data.getType()) {
                    case Message.MSG_TYPE_PHOTO:
                        mListener.onPhotoClick(position);
                        break;
                    case Message.MSG_TYPE_FACE:
                        mListener.onFaceClick(position);
                        break;
                }
            });
        }

        //消息发送的状态
        switch (data.getState()) {
            case Message.MSG_STATE_FAIL:
                holder.progress.setVisibility(View.GONE);
                holder.img_sendfail.setVisibility(View.VISIBLE);
                break;
            case Message.MSG_STATE_SUCCESS:
                holder.progress.setVisibility(View.GONE);
                holder.img_sendfail.setVisibility(View.GONE);
                break;
            case Message.MSG_STATE_SENDING:
                holder.progress.setVisibility(View.VISIBLE);
                holder.img_sendfail.setVisibility(View.GONE);
                break;
        }
        return v;
    }

    static class ViewHolder {
        TextView tv_date;
        ImageView img_avatar;
        TextView tv_chatcontent;
        ImageView img_chatimage;
        ImageView img_sendfail;
        ProgressBar progress;
        RelativeLayout layout_content;
    }

    private void showPopupWindow(final View anchorView, int position, Long id) {
        View contentView = LayoutInflater.from(ArixoChatApplication.getAppContext()).inflate(R.layout.popup_content_layout, null);
        int screenHeight = ArixoChatApplication.getAppContext().getResources().getDisplayMetrics().heightPixels;
        int screenWidth = ArixoChatApplication.getAppContext().getResources().getDisplayMetrics().widthPixels;
        View.OnClickListener menuItemOnClickListener = v -> {
            Toast.makeText(v.getContext(), "Click ", Toast.LENGTH_SHORT).show();
            if (mPopupWindow != null) {
                mPopupWindow.dismiss();
            }
        };
        contentView.findViewById(R.id.menu1_item1).setOnClickListener(view -> {
            // 获取剪切板管理器：
            ClipboardManager clipboard = (ClipboardManager) ArixoChatApplication.getAppContext().getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建普通字符型ClipData
            ClipData clipData = ClipData.newPlainText("Label", mMessage);
            // 将ClipData内容放到系统剪贴板里。
            clipboard.setPrimaryClip(clipData);
            Toast.makeText(view.getContext(), "内容已复制", Toast.LENGTH_SHORT).show();
        });
        contentView.findViewById(R.id.menu1_item2).setOnClickListener(view -> {
            if (onItemShareListener != null) {
                onItemShareListener.onItemShare("Arixo share :" + mMessage);
            }
        });
        contentView.findViewById(R.id.menu1_item3).setOnClickListener(view -> {
            if (onItemShareListener != null) {
                onItemShareListener.onItemDelete(position, id);
            }
        });
        contentView.findViewById(R.id.menu1_item4).setOnClickListener(menuItemOnClickListener);

        mPopupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        //计算View位置
        int[] windowPos = new int[2];
        // 获取锚点View在屏幕上的左上角坐标位置
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 计算contentView的高宽
        int popuHeight = contentView.getMeasuredHeight();
        int popuWidth = contentView.getMeasuredWidth();
        // 判断Y坐标
        if (downY > screenHeight / 2) {
            //向上弹出
            windowPos[1] = downY - popuHeight;
        } else {
            //向下弹出
            windowPos[1] = downY;
        }
        // 判断X坐标
        if (downX > screenWidth / 2) {
            //向左弹出
            windowPos[0] = downX - popuWidth;
        } else {
            //向右弹出
            windowPos[0] = downX;
        }
        int xOff = -20; // 调整偏移
        windowPos[0] -= xOff;
        mPopupWindow.showAtLocation(anchorView, Gravity.TOP | Gravity.START, windowPos[0], windowPos[1]);
    }

}
