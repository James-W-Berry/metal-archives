package com.android.metal_archives;

import android.content.Context;
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
    private TextView search_prep;
    private TableLayout band_tile;
    private String country_in;
    private String location_in;
    private String status_in;
    private String formed_in;


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

    private void getWebsite(String band) {
        Log.i("SeachableActivity", "searching for " + band);
        final String band_name = band;
        new Thread(new Runnable() {
            @Override
            public void run() {
                final StringBuilder builder = new StringBuilder();

                try {
                    Document doc = Jsoup.connect("https://www.metal-archives.com/bands/" + band_name).get();
                    String title = doc.title();

                    builder.append(band_name).append("\n\n");

                    Elements band_stats = doc.select("div#band_stats");
                    for (Element spec : band_stats) {
                        Elements dl = spec.select("dl.float_left");
                        Elements dts = dl.select("dt");
                        Elements dds = dl.select("dd");

                        Iterator<Element> dtsIterator = dts.iterator();
                        Iterator<Element> ddsIterator = dds.iterator();

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
                    }
                    //builder.append(band_stats.text());

//                    Elements links = doc.select("a[href]");
//                    for (Element link : links) {
//                        builder.append("\n").append("Link : ").append(link.attr("href"))
//                                .append("\n").append("Text : ").append(link.text());
//                    }

                } catch (IOException e) {
                    builder.append("Error : ").append(e.getMessage()).append("\n");
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        search_prep.setVisibility(View.GONE);
                        band_tile = (TableLayout) findViewById(R.id.band_tile);
                        band_tile.setVisibility(View.VISIBLE);
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
