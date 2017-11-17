package com.android.metal_archives;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

import java.io.IOException;

/**
 * Created by BEJ2PLY on 9/23/2017.
 */

public class DiscoFocusActivity extends AppCompatActivity {

    private Document doc;
    private Document review_doc;
    private ExpandableHeightGridView track_list_view;
    private ExpandableHeightGridView review_list_view;
    private Context context;
    private DiscoItem discoItem;
    private PopupWindow lyric_popup;
    private PopupWindow review_popup;
    private TextView lyrics;
    private TextView loading;
    private TrackAdapter trackAdapter;
    private LinearLayout disco_view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.disco_item_view_layout);
        context = this;
        Intent intent = getIntent();
        TextView name = findViewById(R.id.disco_focus_name);
        name.setText(intent.getStringExtra("ITEM_NAME"));
        loading = findViewById(R.id.disco_focus_loading);
        loading.setVisibility(View.VISIBLE);

        discoItem = new DiscoItem();
        String item_url = intent.getStringExtra("ITEM_URL");
        String item_review_url = intent.getStringExtra("ITEM_REVIEW_SRC");

        System.out.println("disco item src: " + item_url);
        System.out.println("disco review src: " + item_review_url);

        if(item_url != null){
            if(!item_url.equals("")){
                new AlbumParserTask().execute(intent.getStringExtra("ITEM_URL"));
            }
        }
        if(intent.getStringExtra("ITEM_REVIEW_SRC") != null){
            if(!item_review_url.equals("")){
                new ReviewParserTask().execute(intent.getStringExtra("ITEM_REVIEW_SRC"));
            }
        }
    }

    private class ReviewParserTask extends AsyncTask<String, Integer, DiscoItem> { //URL input, Integer progress, DiscoItem result
        protected DiscoItem doInBackground(String... params) {
            /* parse doc from URL and populate DiscoItem class */
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }

            String review_url = params[0];

            try {
                review_doc = Jsoup.connect(review_url).get();
                ReviewParser reviewParser = new ReviewParser(review_doc);
                Integer review_count = reviewParser.parseReviews();
                discoItem.set_review_titles(reviewParser.review_titles());
                discoItem.set_review_details(reviewParser.review_details());
                discoItem.set_review_contents(reviewParser.review_contents());
                discoItem.set_review_count(review_count);
            } catch (IOException e){
                System.out.println(e.toString());
            }

            return discoItem;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(DiscoItem result) {
            for(int i = 0; i < result.review_count(); i++){
                System.out.println("adding review: " + result.review_titles()[i]);
            }

            review_list_view = findViewById(R.id.review_list);
            ReviewAdapter reviewAdapter = new ReviewAdapter(context, result.review_count(), result);

            review_list_view.setAdapter(reviewAdapter);

            // add item copy to complete tab
            if (review_list_view != null){
                review_list_view.setExpanded(true);
            }

            review_list_view.setOnItemClickListener(reviewListener);

        }
    }

    private class AlbumParserTask extends AsyncTask<String, Integer, DiscoItem> { //URL input, Integer progress, DiscoItem result
        protected DiscoItem doInBackground(String... params) {
            /* parse doc from URL and populate DiscoItem class */
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }
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
                discoItem.set_track_lyric_urls(trackParser.urls());

                CoverParser coverParser = new CoverParser(doc);
                discoItem.set_cover(coverParser.cover());

            } catch (IOException e) {
                Log.e("DiscoFocusActivity", e.toString());
            }

            return discoItem;
        }

        protected void onProgressUpdate(Integer... progress){

        }

        protected void onPostExecute(DiscoItem result) {
            for(int i = 0; i < result.track_count() - 1; i++){
                System.out.println(result.track_number()[i] + " " + result.tracks()[i] + "    " + result.track_length()[i]);
            }
            System.out.println("Total duration: " + (result.tracks()[result.track_count() -1]));

            track_list_view = findViewById(R.id.track_list);

            trackAdapter = new TrackAdapter(context, result.track_count(), result);
            track_list_view.setAdapter(trackAdapter);

//            // add item copy to complete tab
//            if (track_list_view != null){
//                track_list_view.setExpanded(true);
//            }

            track_list_view.setOnItemClickListener(trackListener);

            // add item copy to complete tab
            if (track_list_view != null){
                track_list_view.setExpanded(true);
            }

            loading.setVisibility(View.GONE);

            ImageView cover = findViewById(R.id.disco_focus_cover);
            cover.setImageBitmap(discoItem.cover());

        }
    }

    private class LyricParser extends AsyncTask<String, Integer, String> { //URL input, Integer progress, Lyric result
        protected String doInBackground(String... params) {
            String lyric_url = params[0];
            System.out.println("lyric id: " + lyric_url);

            if (lyric_url != null && lyric_url.contains("#")) {
                lyric_url = lyric_url.substring(1, lyric_url.length());
            }

            try {
                Document lyric_doc;
                lyric_doc = Jsoup.connect("https://metal-archives.com/release/ajax-view-lyrics/id/" + lyric_url).get();
                lyric_doc.outputSettings(new Document.OutputSettings().prettyPrint(false)); //makes html() preserve linebreaks and spacing
                lyric_doc.select("br").append("\n");
                String lyric_body = lyric_doc.html().replaceAll("\\\\n", "\n");
                System.out.println(Jsoup.clean(lyric_body, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false)));
                discoItem.set_track_lyrics(Jsoup.clean(lyric_body, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false)));
            } catch (IOException e) {
                System.out.println(e.toString());
            }

            return discoItem.track_lyrics();
        }

        protected void onProgressUpdate(Integer... progress){

        }

        protected void onPostExecute(String result) {
            lyrics.setText(result);
        }
    }

    private AdapterView.OnItemClickListener reviewListener = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

            LayoutInflater inflater = (LayoutInflater) DiscoFocusActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View layout = inflater.inflate(R.layout.review_view,
                    (ViewGroup) findViewById(R.id.review_popup_element));

            // create a PopupWindow
            disco_view = findViewById(R.id.disco_focus_main);

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            double dHeight  = (displayMetrics.heightPixels - displayMetrics.heightPixels*0.3);
            int height = (int) dHeight;

            double dWidth = (displayMetrics.widthPixels - displayMetrics.widthPixels*0.1);
            int width = (int) dWidth;

            review_popup = new PopupWindow(layout, width, height, true);
            review_popup.showAtLocation(v, Gravity.CENTER, 0, 0);
            dimBehind(review_popup);

            TextView review_title = layout.findViewById(R.id.review_popup_title);
            review_title.setText(discoItem.review_titles()[position]);

            TextView review_details = layout.findViewById(R.id.review_popup_details);
            review_details.setText(discoItem.review_details()[position]);

            TextView review_content = layout.findViewById(R.id.review_popup_content);
            review_content.setText(discoItem.review_contents()[position]);

            ImageView close_review_view = layout.findViewById(R.id.close_review_view);
            close_review_view.setOnClickListener(closeReviewListener);
        }
    };

    private AdapterView.OnItemClickListener trackListener = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            if(discoItem.track_lyric_urls()[position] != null && !(discoItem.track_lyric_urls()[position]).equals("")) {
                try {
                    new LyricParser().execute(discoItem.track_lyric_urls()[position]);

                    //We need to get the instance of the LayoutInflater, use the context of this activity
                    LayoutInflater inflater = (LayoutInflater) DiscoFocusActivity.this
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    //Inflate the view from a predefined XML layout
                    View layout = inflater.inflate(R.layout.lyric_view,
                            (ViewGroup) findViewById(R.id.lyric_popup_element));
                    // create a PopupWindow
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    int height = displayMetrics.heightPixels;
                    int width = displayMetrics.widthPixels;
                    lyric_popup = new PopupWindow(layout, width, height, true);

                    // display the popup in the center
                    lyric_popup.showAtLocation(v, Gravity.CENTER, 0, 0);

                    TextView title = (TextView) layout.findViewById(R.id.song_title);
                    title.setText(discoItem.tracks()[position]);

                    lyrics = (TextView) layout.findViewById(R.id.lyric_content);

                    ImageView close_lyric_view = (ImageView) layout.findViewById(R.id.close_lyric_view);
                    close_lyric_view.setOnClickListener(closeLyricListener);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    };

    private View.OnClickListener closeLyricListener = new View.OnClickListener() {
        public void onClick(View v) {
            lyric_popup.dismiss();
        }
    };

    private View.OnClickListener closeReviewListener = new View.OnClickListener() {
        public void onClick(View v) {
            review_popup.dismiss();
        }
    };

    private static void dimBehind(PopupWindow popupWindow) {
        View container;
        if (popupWindow.getBackground() == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                container = (View) popupWindow.getContentView().getParent();
            } else {
                container = popupWindow.getContentView();
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                container = (View) popupWindow.getContentView().getParent().getParent();
            } else {
                container = (View) popupWindow.getContentView().getParent();
            }
        }
        Context context = popupWindow.getContentView().getContext();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.7f;
        wm.updateViewLayout(container, p);
    }
}