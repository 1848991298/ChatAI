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
package com.arixo.arixochat.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    @SuppressLint("ConstantLocale")
    private static final SimpleDateFormat formatToday = new SimpleDateFormat("hh:mm a", Locale.getDefault());
    @SuppressLint("ConstantLocale")
    private static final SimpleDateFormat formatOtherDay = new SimpleDateFormat("MM-dd hh:mm a", Locale.getDefault());
    @SuppressLint("ConstantLocale")
    private static final SimpleDateFormat formatThisWeek = new SimpleDateFormat("E hh:mm a", Locale.getDefault());
    @SuppressLint("ConstantLocale")
    private static final SimpleDateFormat formatOtherYear = new SimpleDateFormat("yy-MM-dd hh:mm a", Locale.getDefault());

    public static String formatToday(Date date) {
        return formatToday.format(date);
    }

    public static String formatOtherDay(Date date) {
        return formatOtherDay.format(date);
    }

    public static String formatThisWeek(Date date) {
        return formatThisWeek.format(date);
    }

    public static String formatOtherYear(Date date) {
        return formatOtherYear.format(date);
    }

}
