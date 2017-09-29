package com.android.metal_archives;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Author: James Berry
 * Description: Homepage activity
 */

public class Main extends AppCompatActivity {
    public Button enter;
    public TextView search_text;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        // toolbar to replace default toolbar
        enter = (Button) findViewById(R.id.home_toolbar);

        enter.setOnClickListener(new View.OnClickListener() {
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
