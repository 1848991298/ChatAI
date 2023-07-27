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
     2023/7/12
     
     Description 
     ------------------ 
     brief description

----------------------------------------------------------------------------------*/
package com.arixo.arixochat.utils;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.arixo.arixochat.bean.ChatSession;
import com.arixo.arixochat.bean.Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = DBHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "arixochat.db";
    private static final String MESSAGE_NAME = "messages";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_STATE = "state";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_AVATAR = "avatar";
    private static final String COLUMN_CONTENT = "content";
    private static final String COLUMN_SENDS = "send";
    private static final String COLUMN_SUCCESS = "success";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_SESSION = "sessionid";

    private static final String SESSION_NAME = "session";
    private static final String COLUMN_SESSION_ID = "sessionid";
    private static final String COLUMN_TITLE = "title";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate");

        String SESSION_TABLE = "CREATE TABLE " + SESSION_NAME + "("
                + COLUMN_SESSION_ID + " INTEGER PRIMARY KEY," + COLUMN_TITLE + " TEXT" + ")";

        db.execSQL(SESSION_TABLE);

        String CREATE_TABLE = "CREATE TABLE " + MESSAGE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_TYPE + " INTEGER,"
                + COLUMN_STATE + " INTEGER," + COLUMN_USERNAME + " TEXT,"
                + COLUMN_AVATAR + " TEXT, " + COLUMN_CONTENT + " TEXT , " + COLUMN_SENDS + " INTEGER, "
                + COLUMN_SUCCESS + " INTEGER, " + COLUMN_TIME + " TEXT , " + COLUMN_SESSION + " INTEGER DEFAULT 0, "
                + "FOREIGN KEY(" + COLUMN_SESSION + ") REFERENCES " + SESSION_NAME + "(" + COLUMN_SESSION_ID + "))";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "onUpgrade");
        db.execSQL("DROP TABLE IF EXISTS " + MESSAGE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SESSION_NAME);
        onCreate(db);
    }

    public void addChatRecord(Message message, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + MESSAGE_NAME, null);
        if (cursor.moveToFirst() && cursor.getInt(0) >= 100) {
//            db.execSQL("DELETE FROM " + MESSAGE_NAME + " WHERE " + COLUMN_ID + " IN (SELECT " + COLUMN_ID + " FROM " + MESSAGE_NAME + " ORDER BY " + COLUMN_ID + " ASC LIMIT 1)");
            // Get the session id of the oldest message
            Cursor oldMsgCursor = db.rawQuery("SELECT " + COLUMN_SESSION + " FROM " + MESSAGE_NAME + " ORDER BY " + COLUMN_ID + " ASC LIMIT 1", null);

            if (oldMsgCursor.moveToFirst()) {
                int oldMsgSessionId = oldMsgCursor.getInt(0);
                Log.d(TAG, "oldMsgSessionId: " + oldMsgSessionId);

                // Get the count of messages in the session
                Cursor sessionCursor = db.rawQuery("SELECT COUNT(*) FROM " + MESSAGE_NAME + " WHERE " + COLUMN_SESSION + " = " + oldMsgSessionId, null);
                if (sessionCursor.moveToFirst()) {
                    // Delete the oldest message
                    Log.d(TAG, "Deleting old message " + sessionCursor.getInt(0));
                    if (sessionCursor.getInt(0) == 1) {
                        // If the session only has 1 message, delete the whole session
                        db.execSQL("DELETE FROM " + SESSION_NAME + " WHERE " + COLUMN_SESSION_ID + " = " + oldMsgSessionId);
                    }
                    db.execSQL("DELETE FROM " + MESSAGE_NAME + " WHERE " + COLUMN_ID + " IN (SELECT " + COLUMN_ID + " FROM " + MESSAGE_NAME + " ORDER BY " + COLUMN_ID + " ASC LIMIT 1)");

                }
                sessionCursor.close();
            }
            oldMsgCursor.close();

        }
        ContentValues values = new ContentValues();
        values.put(COLUMN_TYPE, message.getType());
        values.put(COLUMN_STATE, message.getState());
        values.put(COLUMN_USERNAME, message.getFromUserName());
        values.put(COLUMN_AVATAR, message.getFromUserAvatar());
        values.put(COLUMN_CONTENT, message.getContent());
        if (message.getIsSend()) {
            values.put(COLUMN_SENDS, 1);
        } else {
            values.put(COLUMN_SENDS, 0);
        }
        if (message.getSendSucces()) {
            values.put(COLUMN_SUCCESS, 1);
        } else {
            values.put(COLUMN_SUCCESS, 0);
        }
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = dateFormat.format(message.getTime());
        values.put(COLUMN_TIME, dateString);
        values.put(COLUMN_SESSION, id);

        db.insert(MESSAGE_NAME, null, values);
        db.close();
        cursor.close();
    }

    public long addChatSession(ChatSession chatSession) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, chatSession.getTitle());
        long id = db.insert(SESSION_NAME, null, values);
        db.close();
        return id;
    }

    public void updateTitle(int id, String newTitle) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, newTitle);

        // Updating title
        db.update(SESSION_NAME, values, COLUMN_SESSION_ID + " = ?",
                new String[]{String.valueOf(id)});

        db.close();
    }

    public void updateTitleIfOnlyOneMessage(int id, String newTitle) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + MESSAGE_NAME + " WHERE " + COLUMN_SESSION + "=?",
                new String[]{String.valueOf(id)});

        int count = cursor.getCount();
        cursor.close();
        Log.d(TAG, "updateTitleIfOnlyOneMessage  count: " + count);

        if (count == 1) {
            this.updateTitle(id, newTitle);
            Log.d(TAG, "updateTitleIfOnlyOneMessage : " + newTitle);
        }

    }

    public List<Message> getMessagesBySession(int sessionValue) {
        List<Message> messageList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.query(MESSAGE_NAME, new String[] { COLUMN_ID, COLUMN_TYPE, COLUMN_STATE,
//                        COLUMN_USERNAME, COLUMN_AVATAR, COLUMN_CONTENT, COLUMN_SENDS, COLUMN_SUCCESS,
//                        COLUMN_TIME, COLUMN_SESSION }, COLUMN_SESSION + "=?",
//                new String[] { String.valueOf(sessionValue) }, null, null, null, null);

        String selectQuery = "SELECT * FROM " + MESSAGE_NAME +
                " WHERE " + COLUMN_SESSION + " = ?";
        try (Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(sessionValue)})
        ) {
            if (cursor.moveToFirst()) {
                do {
                    Message message = new Message();
                    message.setId(cursor.getLong(0));
                    message.setType(cursor.getInt(1));
                    message.setState(cursor.getInt(2));
                    message.setFromUserName(cursor.getString(3));
                    message.setToUserAvatar(cursor.getString(4));
                    message.setContent(cursor.getString(5));
                    message.setIsSend(cursor.getInt(6) == 1);
                    message.setSendSucces(cursor.getInt(7) == 1);
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String dateString = cursor.getString(8);
                    Date date = dateFormat.parse(dateString);
                    message.setTime(date);
                    messageList.add(message);
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            Log.w(TAG, "getMessagesBySession: Detect exception on process SQL data. ", e);

        }

        return messageList;
    }

    public List<Message> getMessagesBySessionLimit(int sessionValue, int offset) {
        List<Message> messageList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + MESSAGE_NAME +
                " WHERE " + COLUMN_SESSION + " = ?" +
                " ORDER BY " + COLUMN_ID + " DESC" +
                " LIMIT 10 OFFSET " + offset;

/*        String selectQuery = "SELECT * FROM " + MESSAGE_NAME +
                " WHERE " + COLUMN_SESSION + " = ?" +
                " ORDER BY " + COLUMN_ID + " LIMIT 10 OFFSET " + offset;*/
        try (Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(sessionValue)})
        ) {
            if (cursor.moveToFirst()) {
                do {
                    Message message = new Message();
                    message.setId(cursor.getLong(0));
                    message.setType(cursor.getInt(1));
                    message.setState(cursor.getInt(2));
                    message.setFromUserName(cursor.getString(3));
                    message.setToUserAvatar(cursor.getString(4));
                    message.setContent(cursor.getString(5));
                    message.setIsSend(cursor.getInt(6) == 1);
                    message.setSendSucces(cursor.getInt(7) == 1);
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String dateString = cursor.getString(8);
                    Date date = dateFormat.parse(dateString);
                    message.setTime(date);
                    messageList.add(message);
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            Log.w(TAG, "getMessagesBySession: Detect exception on process SQL data. ", e);

        }

        return messageList;
    }

    public List<ChatSession> getChatSessions() {
        List<ChatSession> chatSessions = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + SESSION_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        try (Cursor cursor = db.rawQuery(selectQuery, null)) {
            if (cursor.moveToFirst()) {
                do {
                    ChatSession chatSession = new ChatSession();
                    chatSession.setId(cursor.getInt(0));
                    chatSession.setTitle(cursor.getString(1));
                    chatSessions.add(chatSession);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.w(TAG, "getAllChatRecords: Detect exception on process SQL data.", e);
        }

        return chatSessions;
    }

    public List<Message> getAllChatRecords() {
        List<Message> messages = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + MESSAGE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        try (Cursor cursor = db.rawQuery(selectQuery, null)) {
            if (cursor.moveToFirst()) {
                do {
                    Message message = new Message();
                    message.setType(cursor.getInt(1));
                    message.setState(cursor.getInt(2));
                    message.setFromUserName(cursor.getString(3));
                    message.setToUserAvatar(cursor.getString(4));
                    message.setContent(cursor.getString(5));
                    message.setIsSend(cursor.getInt(6) == 1);
                    message.setSendSucces(cursor.getInt(6) == 1);
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String dateString = cursor.getString(8);
                    Date date = dateFormat.parse(dateString);
                    message.setTime(date);
                    messages.add(message);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.w(TAG, "getAllChatRecords: Detect exception on process SQL data.", e);
        }
        return messages;

    }

    public int getLastInsertId() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + SESSION_NAME + " ORDER BY " + COLUMN_SESSION_ID + " DESC LIMIT 1", null);
        // 检测是否存在结果
        if (cursor != null && cursor.moveToFirst()) {
            int lastId = cursor.getInt(cursor.getColumnIndex(COLUMN_SESSION_ID));
            cursor.close();
            return lastId;
        } else {
            return -1;
        }
    }

    public List<ChatSession> searchByTitle(String title) {
        List<ChatSession> chatSessions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + SESSION_NAME + " WHERE " + COLUMN_TITLE + " LIKE ?";

/*        String SELECT_QUERY = String.format("SELECT %s FROM %s WHERE %s LIKE '%%%s%%'", COLUMN_TITLE, SESSION_NAME, COLUMN_TITLE, title);
        Cursor cursor = db.rawQuery(SELECT_QUERY, null);*/

        try (Cursor cursor = db.rawQuery(selectQuery, new String[]{"%" + title + "%"})) {
            if (cursor.moveToFirst()) {
                do {
                    ChatSession chatSession = new ChatSession();
                    chatSession.setId(cursor.getInt(0));
                    chatSession.setTitle(cursor.getString(1));
                    chatSessions.add(chatSession);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.w(TAG, "searchByTitle: Detect exception on process SQL data.", e);
        }
        db.close();
        return chatSessions;
    }

    public void deleteDataById(Long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(MESSAGE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void deleteDataBySessionId(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(MESSAGE_NAME, COLUMN_SESSION_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void deleteSessionById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(SESSION_NAME, COLUMN_SESSION_ID + " = ?", new String[]{String.valueOf(id)});
        deleteDataBySessionId(id);
    }

    public void deleteAllDataFromTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteTableData = "DELETE FROM " + MESSAGE_NAME;
        db.execSQL(deleteTableData);
        db.close();
    }
}
