package com.android.metal_archives;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by BEJ2PLY on 9/23/2017.
 */

public class DiscoFocusActivity extends AppCompatActivity {

    private Document doc;
    private AlbumParser albumParser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.disco_item_view_layout);
        Intent intent = getIntent();
        TextView name = (TextView) findViewById(R.id.disco_focus_name);
        name.setText(intent.getStringExtra("ITEM_NAME"));
        new AlbumParser().execute(intent.getStringExtra("ITEM_URL"));
    }

    private class AlbumParser extends AsyncTask<String, Integer, DiscoItem> { //URL input, Integer progress, BandPage result
        protected DiscoItem doInBackground(String... params) {
            /* parse doc from URL and populate BandPage class */
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }
            DiscoItem discoItem = new DiscoItem();
            String disco_url = params[0];

            try {
                doc = Jsoup.connect(disco_url).get();
                //System.out.println(doc);
                TrackParser trackParser = new TrackParser(doc);
                Integer track_count = trackParser.parseTracks();
                discoItem.set_tracks(trackParser.tracks());
                discoItem.set_track_count(track_count);
                discoItem.set_track_lengths(trackParser.track_lengths());
                discoItem.set_track_numbers(trackParser.track_numbers());
            } catch (IOException e) {
                Log.e("DiscoFocusActivity", e.toString());
            }


            return discoItem;
        }

        protected void onProgressUpdate(Integer... progress){

        }

        protected void onPostExecute(DiscoItem result) {
            for(int i = 0; i < result.track_count() - 1; i++){
                //TODO: replicate view adapter process found in SearchableActivity
                System.out.println(result.track_number()[i] + " " + result.tracks()[i] + "    " + result.track_length()[i] + "    Show lyrics");
            }
            System.out.println("Total duration: " + (result.tracks()[result.track_count() -1]));
        }
    }
}
