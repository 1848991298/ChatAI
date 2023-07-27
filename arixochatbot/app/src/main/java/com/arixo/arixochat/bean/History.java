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
     brief discription

----------------------------------------------------------------------------------*/
package com.arixo.arixochat.bean;

public class History {
    private int session;
    private Message message;

    public int getSession() {
        return session;
    }

    public void setSession(int session) {
        this.session = session;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
