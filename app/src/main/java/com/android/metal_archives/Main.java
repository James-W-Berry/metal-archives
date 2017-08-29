package com.android.metal_archives;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

/**
 * Author: James Berry
 * Description: Homepage activity
 */

public class Main extends AppCompatActivity {
    public Toolbar toolbar;
    public TextView search_text;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        // toolbar to replace default toolbar
        toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
        }
        search_text = (TextView) findViewById(R.id.search_text);
        search_text.setOnClickListener(new View.OnClickListener() {
            Intent intent;
            @Override
            public void onClick(View v) {
                //launch search activity
                intent = new Intent(context, SearchableActivity.class);
                startActivity(intent);
            }
        });
    }
}
