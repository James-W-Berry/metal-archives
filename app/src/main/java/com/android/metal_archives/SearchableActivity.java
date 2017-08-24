package com.android.metal_archives;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

/**
 * Created by bej2ply on 8/18/2017.
 */

public class SearchableActivity extends AppCompatActivity {
    private EditText search;
    private FrameLayout search_frame;
    private ImageView search_clear;
    private ProgressBar search_proress;
    private TextView search_prep;
    private TableLayout band_tile;
    private TextView band_comment;
    private String comment_in;
    private String country_in;
    private String location_in;
    private String status_in;
    private String formed_in;
    private String genre_in;
    private String lyrical_in;
    private String label_in;
    private Bitmap logo_bmp;
    private Bitmap band_pic_bmp;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        search_prep = (TextView) findViewById(R.id.search_prep);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        search = (EditText) findViewById(R.id.search_edit);
        search.setVisibility(View.VISIBLE);
        search_clear = (ImageView) findViewById(R.id.clear_search);
        search_clear.setVisibility(View.VISIBLE);
        search_clear.setOnClickListener(clearSearchListener);
        search_frame = (FrameLayout) findViewById(R.id.search_layout);
        search_frame.setVisibility(View.VISIBLE);

        search.requestFocus(); // open keyboard for search
        if(search.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }

        // listen for search command from keyboard
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search.clearFocus(); // close keyboard
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
                    getWebsite(search.getText().toString());
                    return true;
                }
                return false;
            }
        });

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

    }

    private View.OnClickListener clearSearchListener = new View.OnClickListener() {
        public void onClick(View v) {
            search.setText("");
        }
    };

    private void getWebsite(String band) {
        Log.i("SeachableActivity", "searching for " + band);
        final String band_name = band;
        search_proress = (ProgressBar) findViewById(R.id.search_progress);
        search_proress.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final StringBuilder builder = new StringBuilder();

                try {
                    Document doc = Jsoup.connect("https://www.metal-archives.com/bands/" + band_name).get();
                    String title = doc.title();

                    builder.append(band_name).append("\n\n");

                    Elements band_stats = doc.select("div#band_stats");
                    Elements band_info = doc.select("div#band_info");
                    Elements band_images = doc.select("div#band_sidebar");

                    System.out.println("getting logo");

                    for (Element spec : band_images){
                        System.out.println("getting logo link");
                        Elements logo = spec.select("div.band_name_img");
                        Elements img = logo.select("a");
                        String imgSrc = img.attr("href");
                        System.out.println(imgSrc);
                        InputStream input = new java.net.URL(imgSrc).openStream();
                        logo_bmp = BitmapFactory.decodeStream(input);

                        System.out.println("getting band pic link");
                        Elements band_pic = spec.select("div.band_img");
                        Elements band_img = band_pic.select("a");
                        String bandImgSrc = band_img.attr("href");
                        System.out.println(bandImgSrc);
                        InputStream bandimgInput = new java.net.URL(bandImgSrc).openStream();
                        band_pic_bmp = BitmapFactory.decodeStream(bandimgInput);
                    }

                    for (Element spec : band_info){
                        Elements comment = spec.select("div.band_comment");
                        comment_in = comment.text();
                        System.out.println(comment_in);
                    }

                    for (Element spec : band_stats) {
                        Elements dl = spec.select("dl.float_left");
                        Elements dr = spec.select("dl.float_right");
                        Elements dts = dl.select("dt");
                        Elements dds = dl.select("dd");
                        Elements dts_r = dr.select("dt");
                        Elements dds_r = dr.select("dd");

                        Iterator<Element> dtsIterator = dts.iterator();
                        Iterator<Element> ddsIterator = dds.iterator();
                        Iterator<Element> dts_rIterator = dts_r.iterator();
                        Iterator<Element> dds_rIterator = dds_r.iterator();

                        while (dtsIterator.hasNext() && ddsIterator.hasNext()) {
                            Element dt = (Element) dtsIterator.next();
                            Element dd = (Element) ddsIterator.next();

                            switch (dt.text()){
                                case "Country of origin:":
                                    country_in = dt.text() + " " + dd.text().replaceAll("\\s{2,}", " ").trim();
                                    break;
                                case "Location:":
                                    location_in = dt.text() + " " + dd.text().replaceAll("\\s{2,}", " ").trim();
                                    break;
                                case "Status:":
                                    status_in = dt.text() + " " + dd.text().replaceAll("\\s{2,}", " ").trim();
                                    break;
                                case "Formed in:":
                                    formed_in = dt.text() + " " + dd.text().replaceAll("\\s{2,}", " ").trim();
                                    break;
                                default:
                                    break;
                            }

                            builder.append(dt.text() + " " + dd.text() + "\n");
                            System.out.println(dt.text() + dd.text());
                        }

                        while (dts_rIterator.hasNext() && dds_rIterator.hasNext()) {
                            Element dt_r = (Element) dts_rIterator.next();
                            Element dd_r = (Element) dds_rIterator.next();

                            switch (dt_r.text()){
                                case "Genre:":
                                    genre_in = dt_r.text() + " " + dd_r.text().replaceAll("\\s{2,}", " ").trim();
                                    break;
                                case "Lyrical themes:":
                                    lyrical_in = dt_r.text() + " " + dd_r.text().replaceAll("\\s{2,}", " ").trim();
                                    break;
                                case "Current label:":
                                    label_in = dt_r.text() + " " + dd_r.text().replaceAll("\\s{2,}", " ").trim();
                                    break;
                                default:
                                    break;
                            }

                            builder.append(dt_r.text() + " " + dd_r.text() + "\n");
                            System.out.println(dt_r.text() + dd_r.text());
                        }
                    }

                } catch (IOException e) {
                    builder.append("Error : ").append(e.getMessage()).append("\n");
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        search_proress.setVisibility(View.GONE);
                        search_prep.setVisibility(View.GONE);
                        band_tile = (TableLayout) findViewById(R.id.band_tile);
                        band_tile.setVisibility(View.VISIBLE);
                        band_comment = (TextView) findViewById(R.id.band_comment);
                        band_comment.setText(comment_in);
                        band_comment.setVisibility(View.VISIBLE);

                        TextView name = (TextView) findViewById(R.id.band_name);
                        name.setText(band_name);
                        TextView country = (TextView) findViewById(R.id.band_country);
                        country.setText(country_in);
                        TextView location = (TextView) findViewById(R.id.band_location);
                        location.setText(location_in);
                        TextView status = (TextView) findViewById(R.id.band_status);
                        status.setText(status_in);
                        TextView formed = (TextView) findViewById(R.id.band_formed);
                        formed.setText(formed_in);
                        TextView genre = (TextView) findViewById(R.id.band_genre);
                        genre.setText(genre_in);
                        TextView lyrics = (TextView) findViewById(R.id.band_lyrics);
                        lyrics.setText(lyrical_in);
                        TextView label = (TextView) findViewById(R.id.band_label);
                        label.setText(label_in);

                        ImageView logo = (ImageView) findViewById(R.id.logo);
                        logo.setImageBitmap(logo_bmp);
                        ImageView band_pic = (ImageView) findViewById(R.id.band_pic);
                        band_pic.setImageBitmap(band_pic_bmp);
                        LinearLayout pics_view = (LinearLayout) findViewById(R.id.pic_layout);
                        pics_view.setVisibility(View.VISIBLE);
                    }
                });
            }
        }).start();
    }



    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onSupportNavigateUp() {
        search.setVisibility(View.GONE);
        onBackPressed();
        finish();
        return true;
    }


}
