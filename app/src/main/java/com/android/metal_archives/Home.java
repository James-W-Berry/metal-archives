package com.android.metal_archives;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * Author: James Berry
 * Description: Homepage activity where user searches for bands of interest
 */

public class Home extends AppCompatActivity {
    ImageView search_action;
    EditText search_band_text;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        search_band_text = findViewById(R.id.search_band_edittext) ;
        search_action = findViewById(R.id.search_action);

        search_action.setOnClickListener(new View.OnClickListener() {
            Intent search_intent;
            @Override
            public void onClick(View v) {
                search_intent = new Intent(context, SearchableActivity.class);
                search_intent.putExtra("BAND", search_band_text.getText().toString() );
                startActivity(search_intent);
            }
        });
    }
}
