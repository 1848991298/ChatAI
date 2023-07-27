package com.arixo.arixochat.view;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.arixo.arixochat.R;
import com.arixo.arixochat.utils.DBHelper;

public class CommonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);
        setStatusBar();

        RelativeLayout back  = (RelativeLayout)findViewById(R.id.set_back);
        back.setOnClickListener(view -> finish());

        RelativeLayout date  = (RelativeLayout)findViewById(R.id.set_date);
        date.setOnClickListener(view ->{
            DBHelper dbHelper = new DBHelper(getApplicationContext());
            dbHelper.deleteAllDataFromTable();
        });


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

}