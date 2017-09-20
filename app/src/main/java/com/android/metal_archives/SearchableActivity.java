package com.android.metal_archives;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.android.metal_archives.R.layout.disco_complete;

/**
 * Author: James Berry
 * Description: Fetches user provided band page, parses the
 * relevant data from the html, and displays the information
 */

public class SearchableActivity extends AppCompatActivity {
    private EditText search;
    private FrameLayout search_frame;
    private ImageView search_clear;
    private ProgressBar flatbar;
    private TextView search_prep;
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
    private String band_of_interest;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private ExpandableHeightGridView disco_complete_view;
    private ExpandableHeightGridView disco_main_view;
    private ExpandableHeightGridView disco_lives_view;
    private ExpandableHeightGridView disco_demos_view;
    private ExpandableHeightGridView disco_misc_view;

    private TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        context = this;
        initializeSearchView();
    }

    private void initializeBandView() {
        setContentView(R.layout.band_page_layout);
        context = this;

        search_prep = (TextView) findViewById(R.id.search_prep);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        disco_complete_view = (ExpandableHeightGridView) findViewById(R.id.disco_complete_view);
        disco_main_view = (ExpandableHeightGridView) findViewById(R.id.disco_main_view);
        disco_lives_view = (ExpandableHeightGridView) findViewById(R.id.disco_lives_view);
        disco_demos_view = (ExpandableHeightGridView) findViewById(R.id.disco_demos_view);
        disco_misc_view = (ExpandableHeightGridView) findViewById(R.id.disco_misc_view);


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
                    //cleanTable(album_table);
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
        SearchResultAdapter mAdapter = new SearchResultAdapter(names, details, context);
        recyclerView.setAdapter(mAdapter);
    }

    public void onRecycleViewSelected(int position){
        System.out.println(searchResultPage.bandLinks()[position]);
        System.out.println(searchResultPage.bandLinks()[position].substring(searchResultPage.bandLinks()[position].lastIndexOf('/') - (band_of_interest.length())));
        getWebsite(searchResultPage.bandLinks()[position].substring(searchResultPage.bandLinks()[position].lastIndexOf('/') - (band_of_interest.length())));
    }


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

                } else if(doc.select("div#content_wrapper").select("h4").text().equals("Band not found")){
                    System.out.println("error: band not found");

                } else {
                    /* parse general info and comment */
                    System.out.println("parsing general info and comment");
                    infoParser = new InfoParser();
                    infoParser.parseName(doc.select("div#band_info"));
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
                    bandPage.setName(infoParser.name());
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

                initializeSearchResultView(searchResultPage);

            } else if(bandPage != null) {

                flatbar.setVisibility(View.GONE);

                //initializeSearchView();
                initializeBandView();

                //band_tile = (TableLayout) findViewById(R.id.band_tile);
                //band_tile.setVisibility(View.VISIBLE);
                ImageView band_pic = (ImageView) findViewById(R.id.band_pic);
                band_pic.setImageBitmap(bandPage.bandPic());
                TextView name = (TextView) findViewById(R.id.band_name);
                name.setText(bandPage.name());
                //ImageView logo = (ImageView) findViewById(R.id.logo);
                //logo.setImageBitmap(bandPage.logo());
                TextView country = (TextView) findViewById(R.id.band_country);
                country.setText(bandPage.country());
                TextView genre_view = (TextView) findViewById(R.id.band_genre);
                genre_view.setText(bandPage.genre());
                TextView location_view = (TextView) findViewById(R.id.band_location);
                location_view.setText(bandPage.location());
                TextView status_view = (TextView) findViewById(R.id.band_status);
                status_view.setText(bandPage.status());
                TextView lyrics = (TextView) findViewById(R.id.band_lyrics);
                lyrics.setText(bandPage.lyricalThemes());
                TextView formed_view = (TextView) findViewById(R.id.band_formed);
                formed_view.setText(bandPage.yearFormed());
                TextView label_view = (TextView) findViewById(R.id.band_label);
                label_view.setText(bandPage.label());
                band_comment = (TextView) findViewById(R.id.band_comment);
                band_comment.setText(bandPage.comment());

//                ImageView logo = (ImageView) findViewById(R.id.logo);
//                logo.setImageBitmap(bandPage.logo());
//
//
//
//                LinearLayout pics_view = (LinearLayout) findViewById(R.id.pic_layout);
//                pics_view.setVisibility(View.VISIBLE);

                TabHost host = (TabHost)findViewById(R.id.disco_selector);
                host.setup();

                //Tab 1
                TabHost.TabSpec spec = host.newTabSpec("Complete");
                spec.setContent(R.id.tab1);
                View complete = getLayoutInflater().inflate(R.layout.disco_complete, null);
                spec.setIndicator(complete);
                host.addTab(spec);

                //Tab 2
                spec = host.newTabSpec("Main");
                spec.setContent(R.id.tab2);
                View main = getLayoutInflater().inflate(R.layout.disco_main, null);
                spec.setIndicator(main);
                host.addTab(spec);

                //Tab 3
                spec = host.newTabSpec("Lives");
                spec.setContent(R.id.tab3);
                View lives = getLayoutInflater().inflate(R.layout.disco_lives, null);
                spec.setIndicator(lives);
                host.addTab(spec);

                //Tab 4
                spec = host.newTabSpec("Demos");
                spec.setContent(R.id.tab4);
                View demos = getLayoutInflater().inflate(R.layout.disco_demos, null);
                spec.setIndicator(demos);
                host.addTab(spec);

                //Tab 5
                spec = host.newTabSpec("Misc.");
                spec.setContent(R.id.tab5);
                View misc = getLayoutInflater().inflate(R.layout.disco_misc, null);
                spec.setIndicator(misc);
                host.addTab(spec);

                ViewGroup.LayoutParams params = host.getTabWidget().getChildAt(0).getLayoutParams();
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                params.width = metrics.densityDpi *10;

                host.getTabWidget().getChildAt(0).setLayoutParams(params);
                host.getTabWidget().getChildAt(1).setLayoutParams(params);
                host.getTabWidget().getChildAt(2).setLayoutParams(params);
                host.getTabWidget().getChildAt(3).setLayoutParams(params);
                host.getTabWidget().getChildAt(4).setLayoutParams(params);


                Integer[] complete_index = new Integer[album_count];
                Arrays.fill(complete_index, 0);
                Integer main_count = 0;
                Integer[] mains_index = new Integer[album_count];
                Arrays.fill(mains_index, 0);
                Integer lives_count = 0;
                Integer[] lives_index = new Integer[album_count];
                Arrays.fill(lives_index, 0);
                Integer demos_count = 0;
                Integer[] demos_index = new Integer[album_count];
                Arrays.fill(demos_index, 0);
                Integer misc_count = 0;
                Integer[] misc_index = new Integer[album_count];
                Arrays.fill(misc_index, 0);


                // parse disco item types for relevant filter, create indexed array, pass modified album_count to relevant_album_count
                for(int i = 0; i < album_count; i++){
                    complete_index[i] = 1;

                    switch(bandPage.discoItemType()[i]){
                        case "Full-length":
                            main_count++;
                            mains_index[i] = 1;
                            break;
                        case "Live album":
                            lives_count++;
                            lives_index[i] = 1;
                            break;
                        case "Demo":
                            demos_count++;
                            demos_index[i] = 1;
                            break;
                        default:
                            misc_count++;
                            misc_index[i] = 1;
                            break;
                    }
                }

                System.out.println("complete index: " + Arrays.toString(complete_index));
                System.out.println("main index: " + Arrays.toString(mains_index));
                System.out.println("lives index: " + Arrays.toString(lives_index));
                System.out.println("demos index: " + Arrays.toString(demos_index));
                System.out.println("misc index: " + Arrays.toString(misc_index));

                System.out.println(Integer.toString(main_count) + " albums, " + Integer.toString(lives_count) + " lives, "+Integer.toString(demos_count) +
                        " demos, " +Integer.toString(misc_count) +" misc items");


                DiscographyAdapter discoCompleteAdapter = new DiscographyAdapter(context, bandPage, album_count, album_count, complete_index, "Complete");
                disco_complete_view.setAdapter(discoCompleteAdapter);

                DiscographyAdapter discoMainAdapter = new DiscographyAdapter(context, bandPage, album_count, main_count, mains_index, "Mains");
                disco_main_view.setAdapter(discoMainAdapter);

                DiscographyAdapter discoLivesAdapter = new DiscographyAdapter(context, bandPage, album_count, lives_count,lives_index, "Lives");
                disco_lives_view.setAdapter(discoLivesAdapter);

                DiscographyAdapter discoDemosAdapter = new DiscographyAdapter(context, bandPage, album_count, demos_count,demos_index, "Demos");
                disco_demos_view.setAdapter(discoDemosAdapter);

                DiscographyAdapter discoMiscAdapter = new DiscographyAdapter(context, bandPage, album_count, misc_count, misc_index, "Misc.");
                disco_misc_view.setAdapter(discoMiscAdapter);

                // add item copy to complete tab
                if (disco_complete_view != null){
                    disco_complete_view.setExpanded(true);
                    disco_main_view.setExpanded(true);
                    disco_lives_view.setExpanded(true);
                    disco_demos_view.setExpanded(true);
                    disco_misc_view.setExpanded(true);
                }

            } else {
                initializeSearchView();
                Toast toast = Toast.makeText(context,"band not found! please try again", Toast.LENGTH_SHORT);
                toast.show();
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
