package com.android.metal_archives;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
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
import android.widget.TableRow;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: James Berry
 * Description: Fetches user provided band page, parses the
 * relevant data from the html, and displays the information
 */

public class SearchableActivity extends AppCompatActivity implements SearchResultAdapter.OnRecycleViewSelected{
    private EditText search;
    private FrameLayout search_frame;
    private ImageView search_clear;
    private ProgressBar search_proress;
    private ProgressBar flatbar;
    private TextView search_prep;
    private TableLayout band_tile;
    private TextView band_comment;
    private Context context;

    private TableLayout album_table;
    private Integer album_count = 0;

    private DiscoParser discoParser;
    private ImageParser imageParser;
    private InfoParser infoParser;
    private SearchResultsParser searchResultsParser;
    private Document doc;
    private BandPage bandPage;
    private SearchResultPage searchResultPage;
    private ViewPageResult viewPageResult;
    private LinearLayout search_result_table;
    private String band_of_interest;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        context = this;
        initializeSearchView();
    }


    private void initializeSearchView() {
        setContentView(R.layout.activity_search);
        context = this;
        search_prep = (TextView) findViewById(R.id.search_prep);

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
                    //cleanTable(search_result_table);
                    album_count = 0;
                    band_of_interest = search.getText().toString();
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

//    private void initializeSearchResultView(SearchResultPage result) {
//        setContentView(R.layout.activity_search_result);
//        context = this;
//
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        search_result_table = (LinearLayout) findViewById(R.id.search_results);
//
//        cleanLayout(search_result_table);
//
//        toolbar.setTitle("");
//        setSupportActionBar(toolbar);
//
//        // add back arrow to toolbar
//        if (getSupportActionBar() != null){
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
//        }
//
//        search = (EditText) findViewById(R.id.search_edit);
//        search.setVisibility(View.VISIBLE);
//
//        search_clear = (ImageView) findViewById(R.id.clear_search);
//        search_clear.setVisibility(View.VISIBLE);
//        search_clear.setOnClickListener(clearSearchListener);
//        search_frame = (FrameLayout) findViewById(R.id.search_layout);
//        search_frame.setVisibility(View.VISIBLE);
//
//        search.requestFocus(); // open keyboard for search
//        if(search.requestFocus()) {
//            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//        }
//
//        // listen for search command from keyboard
//        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    search.clearFocus(); // close keyboard
//                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
//                    cleanLayout(search_result_table);
//                    album_count = 0;
//                    getWebsite(search.getText().toString());
//                    return true;
//                }
//                return false;
//            }
//        });
//
//        // add back arrow to toolbar
//        if (getSupportActionBar() != null){
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
//        }
//
//        for (int i = 0; i < result.bands().length; i++) {
//            TextView band = new TextView(context);
//
//            /* set values for the view */
//            band.setId(i);
//            band.setText(result.bands()[i]);
//            band.setTextSize(18);
//            band.setClickable(true);
//            band.setBackgroundResource(R.color.suggest);
//            band.setPadding(5, 40, 5, 0);
//            band.setGravity(Gravity.CENTER);
//
//            if (result.bands()[i] != null) {
//                search_result_table.addView(band);
//            }
//
//            band.setOnClickListener(bandClickListener);
//        }
//    }

    private void initializeSearchResultView(SearchResultPage result) {
        setContentView(R.layout.recycle_search_result);
        context = this;

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size
        // of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);

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
                    //cleanLayout(search_result_table);
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

        // populate result views
        List<String> names = new ArrayList<>();
        List<String> details = new ArrayList<>();
        for (int i = 0; i < result.bands().length; i++) {
            if (result.bands()[i] != null){
                String name = result.bands()[i].split("-", 2)[0];
                String detail = result.bands()[i].split("- ", 2)[1];
                names.add(name);
                details.add(detail);
            }
        }
        // define an adapter
        mAdapter = new SearchResultAdapter(names, details);
        recyclerView.setAdapter(mAdapter);

    }

    public void onRecycleViewSelected(int position){
        System.out.println("made it back to SearchableActivity!");
        System.out.println(searchResultPage.bandLinks()[position]);
        System.out.println(searchResultPage.bandLinks()[position].substring(searchResultPage.bandLinks()[position].lastIndexOf('/') - (band_of_interest.length())));
        //getWebsite(searchResultPage.bandLinks()[position].substring(searchResultPage.bandLinks()[position].lastIndexOf('/') - (band_of_interest.length())));
    }


    private View.OnClickListener adapterListener;

    private void initializeSearchingView(){
        setContentView(R.layout.activity_searching);
        flatbar = (ProgressBar) findViewById(R.id.flat_search_progress);
        flatbar.setVisibility(View.VISIBLE);
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


    private class Parser extends AsyncTask<String, Integer, ViewPageResult> { //URL input, Integer progress, BandPage result
        protected ViewPageResult doInBackground(String... params){
            /* parse doc from URL and populate BandPage class */
            if (Looper.myLooper() == null)
            {
                Looper.prepare();
            }
            String band = params[0];
            bandPage = new BandPage();
            searchResultPage = new SearchResultPage();
            viewPageResult = new ViewPageResult();

            try {
                doc = Jsoup.connect("https://www.metal-archives.com/bands/" + band).get();
                //System.out.println(doc);
                Elements search_results = doc.select("div#content_wrapper").select("ul");
                System.out.println(search_results.select("li").first().text());


                if(!(search_results.select("li").first().text().contains("Search on eBay"))) {
                    /* parse search results */
                    System.out.println("parsing search results");
                    searchResultsParser = new SearchResultsParser(search_results);
                    searchResultsParser.fetchBands();

                    searchResultPage.setBands(searchResultsParser.bandResults());
                    searchResultPage.setBandLinks(searchResultsParser.bandLinks());
                    searchResultPage.display();

                    viewPageResult.setSearchResultPage(searchResultPage);

                } else {
                    /* parse general info and comment */
                    System.out.println("parsing general info and comment");
                    infoParser = new InfoParser();
                    infoParser.parseComment(doc.select("div#band_info"));
                    infoParser.parseInfo(doc.select("div#band_stats"));
                    publishProgress(25);

                    /* parse logo and band pic*/
                    System.out.println("parsing logo and band pic");
                    imageParser = new ImageParser(doc.select("div#band_sidebar"));
                    imageParser.fetchLogo();
                    imageParser.fetchBandPic();
                    publishProgress(65);

                    /* parse discography */
                    System.out.println("parsing discography");
                    discoParser = new DiscoParser(doc);
                    album_count = discoParser.fetchComplete();
                    publishProgress(95);

                    /* populate bandPage */
                    bandPage.setName(band_of_interest);
                    bandPage.setComment(infoParser.comment());
                    bandPage.setCountry(infoParser.country());
                    bandPage.setLocation(infoParser.location());
                    bandPage.setStatus(infoParser.status());
                    bandPage.setYearFormed(infoParser.founded());
                    //bandPage.setYearsActive(infoParser.yearsActive()); Not currently parsed
                    bandPage.setGenre(infoParser.genre());
                    bandPage.setLyricalThemes(infoParser.lyrical_themes());
                    bandPage.setLabel(infoParser.label());
                    bandPage.setLogo(imageParser.logo());
                    bandPage.setBandPic(imageParser.band_pic());
                    bandPage.setDiscoItemName(discoParser.names());
                    bandPage.setDiscoItemNameSrc(discoParser.name_srcs());
                    bandPage.setDiscoItemType(discoParser.types());
                    bandPage.setDiscoItemYear(discoParser.years());
                    bandPage.setDiscoItemScore(discoParser.scores());
                    bandPage.setDiscoItemReviewSrc(discoParser.review_srcs());

                    bandPage.display();
                    viewPageResult.setBandPage(bandPage);
                }

            } catch(IOException e){
                System.out.println(e.toString());
            }
            return viewPageResult;
        }

        protected void onProgressUpdate(Integer... progress){
            // option: update progress bar instead of progress circle
            System.out.println(progress[progress.length-1].toString());
            flatbar.setProgress(progress[progress.length-1]);
        }

        protected void onPostExecute(ViewPageResult result){
            // update UI elements
            bandPage = new BandPage();
            bandPage = result.getBandPage();
            searchResultPage = new SearchResultPage();
            searchResultPage = result.getSearchResultsPage();

            if(searchResultPage != null){
                // old
                //initializeSearchResultView(searchResultPage);

                // new
                initializeSearchResultView(searchResultPage);

            } else {

                flatbar.setVisibility(View.GONE);

                initializeSearchView();

                band_tile = (TableLayout) findViewById(R.id.band_tile);
                band_tile.setVisibility(View.VISIBLE);

                band_comment = (TextView) findViewById(R.id.band_comment);
                band_comment.setText(bandPage.comment());
                band_comment.setVisibility(View.VISIBLE);

                TextView name = (TextView) findViewById(R.id.band_name);
                name.setText(bandPage.name());

                TextView country_view = (TextView) findViewById(R.id.band_country);
                country_view.setText(bandPage.country());

                TextView location_view = (TextView) findViewById(R.id.band_location);
                location_view.setText(bandPage.location());

                TextView status_view = (TextView) findViewById(R.id.band_status);
                status_view.setText(bandPage.status());

                TextView formed_view = (TextView) findViewById(R.id.band_formed);
                formed_view.setText(bandPage.yearFormed());

                TextView genre_view = (TextView) findViewById(R.id.band_genre);
                genre_view.setText(bandPage.genre());

                TextView lyrics = (TextView) findViewById(R.id.band_lyrics);
                lyrics.setText(bandPage.lyricalThemes());

                TextView label_view = (TextView) findViewById(R.id.band_label);
                label_view.setText(bandPage.label());

                ImageView logo = (ImageView) findViewById(R.id.logo);
                logo.setImageBitmap(bandPage.logo());

                ImageView band_pic = (ImageView) findViewById(R.id.band_pic);
                band_pic.setImageBitmap(bandPage.bandPic());

                LinearLayout pics_view = (LinearLayout) findViewById(R.id.pic_layout);
                pics_view.setVisibility(View.VISIBLE);

                TextView disco_header = (TextView) findViewById(R.id.discography);
                disco_header.setVisibility(View.VISIBLE);

                album_table.setVisibility(View.VISIBLE);


                for (int i = 0; i < album_count; i++) {
                    // TODO: add horizontal scrolling view for albums, one row with album art and details
                    TableRow album = new TableRow(context);
                    TableRow album_info = new TableRow(context);
//                            ImageView DiscoView = (ImageView) findViewById(R.id.cover_img);
//                            DiscoView.setImageBitmap();
                    TextView name_view = new TextView(context);
                    TextView type_view = new TextView(context);

                            /* set values for the view */
                    name_view.setText(bandPage.discoItemName()[i]);
                    name_view.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    name_view.setTextSize(18);

                    type_view.setText(bandPage.discoItemYear()[i] + " " + bandPage.discoItemType()[i] + " \nReviews: " + bandPage.discoItemScore()[i] + "\n");
                    type_view.setTextSize(14);

                            /* add views to row view */
                    album.addView(name_view);
                    album_info.addView(type_view);

                            /* add row to table */
                    if (bandPage.discoItemName()[i] != null) {
                        album_table.addView(album);
                        album_table.addView(album_info);
                    }
                }
            }
        }

    }

    public void getWebsite(String band) {
        Log.i("SearchableActivity", "searching for " + band);
        initializeSearchingView();
        new Parser().execute(band);
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
