package com.cheng.gu.gc_collapser;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.cheng.gu.gc_collapser.girls.FullscreenActivity;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void onClick(View v){
        int id = v.getId();
        switch (id){
            case R.id.id_btn_expand_text:
                Intent intent = new Intent(LauncherActivity.this,MainActivity.class);
                startActivity(intent);
                break;
            case R.id.id_btn_expand_plus:
                Intent intent1 = new Intent(LauncherActivity.this,FullscreenActivity.class);
                startActivity(intent1);
                break;
        }
    }

}
