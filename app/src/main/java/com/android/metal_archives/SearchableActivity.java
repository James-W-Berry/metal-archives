package com.android.metal_archives;

import android.app.Activity;
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
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.awt.font.TextAttribute;
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
 * Author: James Berry
 * Description: Fetches user provided band page, parses the
 * relevant data from the html, and displays the information
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
    private Context context;

    private TableLayout album_table;
    private String[] names ;
    private String[] name_links;
    private String[] types;
    private String[] years;
    private String[] review_scores;
    private String[] review_links;
    private Integer album_count = 0;

    
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        context = this;
        search_prep = (TextView) findViewById(R.id.search_prep);

        names = new String[100];
        name_links = new String[100];
        types = new String[100];
        years = new String[100];
        review_scores = new String[100];
        review_links = new String[100];

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        album_table = (TableLayout) findViewById(R.id.album_table);


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
                    cleanTable(album_table);
                    album_count = 0;
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

    private void cleanTable(TableLayout table) {
        int childCount = table.getChildCount();
        if (childCount > 0) {
            table.removeViews(0, childCount);
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
                    Elements band_disco = doc.select("div#band_disco");

                    /* parse HTML for discography */
                    Elements disc = band_disco.select("ul");
                    Element complete = disc.select("li").first();
                    Elements complete_ = complete.select("a");
                    String completeSrc = complete_.attr("href");
                    System.out.println(" "+completeSrc);

                    try {
                        System.out.println("fetching discography data");
                        Document discography = Jsoup.connect(completeSrc).get();
                        Elements disco_table = discography.select("table");
                        Elements disco_list = disco_table.select("tr");
                        for (Element row : disco_list){
                            album_count++;
                            Elements columns = row.select("td");
                            Integer column_index = 0;
                            for (Element column : columns){
                                switch (column_index){
                                    case 0:
                                        Elements album = column.select("a");
                                        names[album_count - 1] = album.text();
                                        name_links[album_count - 1] = album.attr("href");
                                        break;
                                    case 1:
                                        types[album_count - 1] = column.text();
                                        break;
                                    case 2:
                                        years[album_count - 1] = column.text();
                                        break;
                                    case 3:
                                        Elements album_reviews = column.select("a");
                                        review_scores[album_count - 1] = album_reviews.text();
                                        review_links[album_count - 1] = album_reviews.attr("href");
                                        break;
                                    default:
                                        break;
                                }
                                column_index ++;
                            }
                        }
                    } catch (IOException e) {
                        builder.append("Error : ").append(e.getMessage()).append("\n");
                    }




                    /* parse HTML for logo and band picture */
                    for (Element spec : band_images){
                        Elements logo = spec.select("div.band_name_img");
                        Elements img = logo.select("a");
                        String imgSrc = img.attr("href");
                        InputStream input = new URL(imgSrc).openStream();
                        logo_bmp = BitmapFactory.decodeStream(input);

                        Elements band_pic = spec.select("div.band_img");
                        Elements band_img = band_pic.select("a");
                        String bandImgSrc = band_img.attr("href");
                        InputStream bandimgInput = new URL(bandImgSrc).openStream();
                        band_pic_bmp = BitmapFactory.decodeStream(bandimgInput);
                    }

                    /* parse HTML for band comment */
                    for (Element spec : band_info){
                        Elements comment = spec.select("div.band_comment");
                        comment_in = comment.text();
                        System.out.println(comment_in);
                    }

                    /* parse HTML for band info */
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

                        TextView disco_header = (TextView) findViewById(R.id.discography);
                        disco_header.setVisibility(View.VISIBLE);

                        album_table.setVisibility(View.VISIBLE);



                        for(int i = 0; i < album_count; i++) {
                            TableRow album = new TableRow(context);
                            TableRow album_info = new TableRow(context);

                            TextView name_view = new TextView(context);
                            TextView type_view = new TextView(context);

                            /* set values for the view */
                            name_view.setId(100 + i);
                            name_view.setText(names[i]);
                            name_view.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                            name_view.setTextSize(18);

                            type_view.setId(200 + i);
                            type_view.setText(years[i] + " " + types[i] + " \nReviews: " + review_scores[i] +"\n");
                            type_view.setTextSize(14);

                            /* add views to row view */
                            album.addView(name_view);
                            album_info.addView(type_view);

                            /* add row to table */
                            if (names[i] != null) {
                                album_table.addView(album);
                                album_table.addView(album_info);
                            }
                        }
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
