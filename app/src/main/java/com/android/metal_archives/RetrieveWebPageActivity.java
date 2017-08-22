package com.android.metal_archives;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by BEJ2PLY on 8/21/2017.
 */

class RetrieveWebPageActivity extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... urls) {
        try {
            URL url_page = new URL(urls[0]);
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) url_page.openConnection();
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    return readStream(in);
                } finally {
                    urlConnection.disconnect();
                }
            } catch (IOException e){
                Log.e("SearchableActivity", ""+e);
            }
        } catch (MalformedURLException e){
            Log.e("SearchableActivity", ""+e);
        }
        return "error";
    }

    @Override
    protected void onPostExecute(String result) {
        SearchableActivity searchableActivity = new SearchableActivity();
        searchableActivity.setContent(result);
    }

    private String readStream(InputStream in_stream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in_stream));
        StringBuilder result = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException e) {
            Log.e("SearchableActivity", "" + e);
        }
        System.out.println(result.toString());
        return result.toString();
    }
}
