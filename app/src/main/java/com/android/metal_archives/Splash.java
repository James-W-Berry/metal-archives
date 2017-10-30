package com.android.metal_archives;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Author: James Berry
 * Description: splash screen while app initializes
 */

public class Splash extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
        finish();
    }
}
