package com.android.metal_archives;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Author: James Berry
 * Description: Homepage activity where user searches for bands of interest
 */

public class Home extends AppCompatActivity {
    ImageView search_action;
    EditText search_band_text;
    Context context;
    ImageView search_clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        search_band_text = findViewById(R.id.search_band_edittext) ;
        search_action = findViewById(R.id.search_action);
        search_clear = findViewById(R.id.search_clear);

        search_band_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(search_band_text.length() > 0){
                    search_clear.setVisibility(View.VISIBLE);
                } else{
                    search_clear.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                // TODO Auto-generated method stub
            }
        });

        search_action.setOnClickListener(new View.OnClickListener() {
            Intent search_intent;
            @Override
            public void onClick(View v) {
                search_intent = new Intent(context, SearchableActivity.class);
                search_intent.putExtra("BAND", search_band_text.getText().toString() );
                startActivity(search_intent);
            }
        });

        search_band_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });

        search_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                search_band_text.setText("");
            }
        });
    }

    protected void performSearch(){
        Intent search_intent;
        search_intent = new Intent(context, SearchableActivity.class);
        search_intent.putExtra("BAND", search_band_text.getText().toString() );
        startActivity(search_intent);
    }
}
