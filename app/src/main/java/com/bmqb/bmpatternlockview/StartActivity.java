package com.bmqb.bmpatternlockview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class StartActivity extends AppCompatActivity {

    private Context mCtx;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mCtx = this;

        findViewById(R.id.btn_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mCtx, MainActivity.class);
                intent.putExtra("ACTION", MainActivity.CREATE_PATTERN);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_check).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mCtx, MainActivity.class);
                intent.putExtra("ACTION", MainActivity.CHECK_PATTERN);
                intent.putExtra("PATTERN_KEY", SPUtils.getPattern(mCtx));
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_fix).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mCtx, MainActivity.class);
                intent.putExtra("ACTION", MainActivity.FIX_PATTERN);
                intent.putExtra("PATTERN_KEY", SPUtils.getPattern(mCtx));
                startActivity(intent);
            }
        });

    }
}
