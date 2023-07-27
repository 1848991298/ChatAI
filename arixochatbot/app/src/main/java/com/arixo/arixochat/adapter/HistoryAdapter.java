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
     2023/7/15
     
     Description 
     ------------------ 
     brief description

----------------------------------------------------------------------------------*/
package com.arixo.arixochat.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.arixo.arixochat.R;
import com.arixo.arixochat.bean.ChatSession;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends BaseAdapter {
    private static final String TAG = HistoryAdapter.class.getSimpleName();
    private List<ChatSession> mChatSessions;
    private final Context mContext;

    public HistoryAdapter(Context context, List<ChatSession> chatSessions) {
        this.mContext = context;
        if (chatSessions == null) {
            chatSessions = new ArrayList<>(0);
        }
        this.mChatSessions = chatSessions;
    }

    public void refresh(List<ChatSession> chatSessions) {
        if (chatSessions == null) {
            chatSessions = new ArrayList<>(0);
        }
        this.mChatSessions = chatSessions;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mChatSessions.size();
    }

    @Override
    public Object getItem(int position) {
        return mChatSessions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = View.inflate(mContext, R.layout.history_item_list, null);
            holder = new ViewHolder();
            holder.tv_history = (TextView) view.findViewById(R.id.tv_history);
            holder.tv_id = (TextView) view.findViewById(R.id.tv_ID);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        ChatSession session = mChatSessions.get(position);
        holder.tv_history.setText(session.getTitle());
        Log.d(TAG, "session" + session.getId());
        holder.tv_id.setText(String.valueOf(session.getId()));
        holder.tv_id.setVisibility(View.GONE);
        return view;
    }

    static class ViewHolder {
        TextView tv_history;
        TextView tv_id;
    }
}
