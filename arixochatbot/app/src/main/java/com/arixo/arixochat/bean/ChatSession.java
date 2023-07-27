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
     2023/7/14
     
     Description 
     ------------------ 
     brief description

----------------------------------------------------------------------------------*/
package com.arixo.arixochat.bean;

import androidx.annotation.NonNull;

public class ChatSession {
    private int id;
    private String title;

    public ChatSession() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @NonNull
    @Override
    public String toString() {
        return "ChatSession{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }
}
