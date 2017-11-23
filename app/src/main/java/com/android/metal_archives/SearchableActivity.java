package com.android.metal_archives;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Author: James Berry
 * Description: Fetches user provided band page, parses the
 * relevant data from the html, and displays the information
 */

public class SearchableActivity extends AppCompatActivity {

    private EditText search;
    private FrameLayout search_frame;
    private ImageView search_clear;
    private TextView search_prep;
    private TextView band_comment;
    private Context context;
    private TextView flatbar_status;

    private TableLayout album_table;
    private Integer album_count = 0;
    private String band_id;
    private TextView comment_content;
    private PopupWindow comment_popup;
    private String read_more_check;

    private DiscoParser discoParser;
    private CoverParser coverParser;
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
    private CoordinatorLayout main_view;

    public static final int OPEN_NEW_ACTIVITY = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        context = this;
        band_of_interest = getIntent().getStringExtra("BAND");
        getBand( band_of_interest );
    }

    public void getBand(String band) {
        Log.i("SearchableActivity", "searching for " + band);
        setContentView(R.layout.activity_search);
        new BandParser().execute(band);
    }

    private class BandParser extends AsyncTask<String, Integer, ViewPageResult> { //URL input, Integer progress, BandPage result

        protected ViewPageResult doInBackground(String... params){
            if (Looper.myLooper() == null) { Looper.prepare(); }

            bandPage = new BandPage();
            searchResultPage = new SearchResultPage();
            viewPageResult = new ViewPageResult();

            flatbar_status = findViewById(R.id.search_progress_status);
            publishProgress(0);

            try {
                doc = Jsoup.connect("https://www.metal-archives.com/bands/" + params[0]).get();
                String page_url = doc.select("h1.band_name").select("a").attr("href");
                band_id = page_url.substring(page_url.lastIndexOf("/") + 1);
                Elements search_results = doc.select("div#content_wrapper").select("ul");
                read_more_check = doc.select("div.tool_strip.bottom.right").select("a").text();

                if(!(search_results.select("li").first().text().contains("Search on eBay"))) {
                    /* parse search results if multiple bands exist with the same name */
                    searchResultsParser = new SearchResultsParser(search_results);
                    searchResultsParser.fetchBands();
                    searchResultPage.setBands(searchResultsParser.bandResults());
                    searchResultPage.setBandLinks(searchResultsParser.bandLinks());

                    viewPageResult.setSearchResultPage(searchResultPage);
                } else if(doc.select("div#content_wrapper").select("h4").text().equals("Band not found")){
                    System.out.println("error: band not found");

                } else {
                    /* parse general info and comment */
                    publishProgress(25);
                    infoParser = new InfoParser();
                    infoParser.parseName(doc.select("div#band_info"));
                    infoParser.parseComment(doc.select("div#band_info"));
                    infoParser.parseInfo(doc.select("div#band_stats"));

                    /* parse logo and band pic*/
                    publishProgress(50);
                    imageParser = new ImageParser(doc.select("div#band_sidebar"));
                    imageParser.fetchLogo();
                    imageParser.fetchBandPic();

                    /* parse discography */
                    publishProgress(75);
                    discoParser = new DiscoParser(doc);
                    album_count = discoParser.fetchComplete();

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

                    viewPageResult.setBandPage(bandPage);
                }

            } catch(IOException e){
                System.out.println(e.toString());
            }

            return viewPageResult;
        }

        protected void onProgressUpdate(Integer... progress){

            switch (progress[progress.length-1]){
                case 0:
                    flatbar_status.setText("looking for " + band_of_interest + "...");
                    break;
                case 25:
                    flatbar_status.setText("gathering band information...");
                    break;
                case 50:
                    flatbar_status.setText("fetching band picture...");
                    break;
                case 75:
                    flatbar_status.setText("loading discography and wrapping up...");
                    break;
                default:
                    break;
            }
        }

        protected void onPostExecute(ViewPageResult result){

            bandPage = new BandPage();
            bandPage = result.getBandPage();

            if(result.getSearchResultsPage() != null){
                initializeSearchResultView(result.getSearchResultsPage());
            } else if(bandPage != null) {
                initializeBandView();

                ImageView band_pic = findViewById(R.id.band_pic);
                band_pic.setImageBitmap(bandPage.bandPic());
                band_pic.setOnClickListener(spotifyListener);

                TextView name = findViewById(R.id.band_name);
                name.setText(bandPage.name());
                ImageView logo = findViewById(R.id.logo);
                logo.setImageBitmap(bandPage.logo());
                TextView country = findViewById(R.id.band_country);
                country.setText(bandPage.country());
                TextView genre_view = findViewById(R.id.band_genre);
                genre_view.setText(bandPage.genre());
                TextView location_view = findViewById(R.id.band_location);
                location_view.setText(bandPage.location());
                TextView status_view = findViewById(R.id.band_status);
                status_view.setText(bandPage.status());
                TextView lyrics = findViewById(R.id.band_lyrics);
                lyrics.setText(bandPage.lyricalThemes());
                TextView formed_view = findViewById(R.id.band_formed);
                formed_view.setText(bandPage.yearFormed());
                TextView label_view = findViewById(R.id.band_label);
                label_view.setText(bandPage.label());
                band_comment = findViewById(R.id.band_comment);
                band_comment.setText(bandPage.comment());

                if(read_more_check.equals("Read more")) {
                    band_comment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                               // new CommentParser().execute(band_id);

                                LayoutInflater inflater = (LayoutInflater) SearchableActivity.this
                                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                                View layout = inflater.inflate(R.layout.more_comment_view,
                                        (ViewGroup) findViewById(R.id.comment_popup_element));

                                // create a PopupWindow and dim background
                                main_view = findViewById(R.id.main_content);

                                DisplayMetrics displayMetrics = new DisplayMetrics();
                                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                                double dHeight  = (displayMetrics.heightPixels - displayMetrics.heightPixels*0.3);
                                int height = (int) dHeight;

                                double dWidth = (displayMetrics.widthPixels - displayMetrics.widthPixels*0.1);
                                int width = (int) dWidth;

                                comment_popup = new PopupWindow(layout, width, height, true);
                                comment_popup.showAtLocation(v, Gravity.CENTER, 0, 0);
                                dimBehind(comment_popup);

                                TextView band_name = layout.findViewById(R.id.band_name);
                                band_name.setText(bandPage.name());
                                comment_content = layout.findViewById(R.id.comment_content);
                                comment_content.setText(bandPage.more_comment());
                                ImageView close_comment_view = layout.findViewById(R.id.close_comment_view);
                                close_comment_view.setOnClickListener(closeCommentListener);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }


                TabHost host = findViewById(R.id.disco_selector);
                host.setup();

                //TODO: keep tab headings, replace tab contents with one horizontal scrollview
                // with the tab headings hiding/showing relevant discography items

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

//                System.out.println("complete index: " + Arrays.toString(complete_index));
//                System.out.println("main index: " + Arrays.toString(mains_index));
//                System.out.println("lives index: " + Arrays.toString(lives_index));
//                System.out.println("demos index: " + Arrays.toString(demos_index));
//                System.out.println("misc index: " + Arrays.toString(misc_index));
//
//                System.out.println(Integer.toString(main_count) + " albums, " + Integer.toString(lives_count) + " lives, "+Integer.toString(demos_count) +
//                        " demos, " +Integer.toString(misc_count) +" misc items");


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

                new CommentParser().execute(band_id);

                /* setup threads to populate discography item covers */
                populateCovers();


            } else {
                initializeSearchView();
                Toast toast = Toast.makeText(context,"band not found! please try again", Toast.LENGTH_SHORT);
                toast.show();
            }
        }

    }

    private class CoverParserParams {
        String url;
        Integer index;
        Bitmap cover;

        CoverParserParams(String url_in, Integer index_in){
            this.url = url_in;
            this.index = index_in;
            this.cover = null;
        }
    }

    private void populateCovers(){
        for(int i = 0; i < discoParser.name_srcs().length; i ++){
            if(discoParser.name_srcs()[i] != null){
                System.out.println("parsing cover for " + discoParser.name_srcs()[i]);
                CoverParserParams coverParserParams = new CoverParserParams(discoParser.name_srcs()[i], i);
                new DiscoCoversParser().execute(coverParserParams);
            }
        }
    }

    private class DiscoCoversParser extends AsyncTask<CoverParserParams, Integer, CoverParserParams> {
        protected CoverParserParams doInBackground(CoverParserParams... params) {
            if (Looper.myLooper() == null) { Looper.prepare(); }

            try {
                System.out.println(params[0].url);
                doc = Jsoup.connect(params[0].url).get();
                coverParser = new CoverParser(doc);
                //bandPage.setDiscoItemCover(coverParser.cover(), params[0].index);
                params[0].cover = coverParser.cover();
            } catch (IOException e){
                System.out.println(e.toString());
            }

            return params[0];
        }

        protected void onProgressUpdate(Integer... values) {

        }

        protected void onPostExecute(CoverParserParams coverParserParams_finished) {
            //System.out.println("fetched cover for " + coverParserParams_finished.url);
            ImageView complete_covers = disco_complete_view.getChildAt(coverParserParams_finished.index).findViewById(R.id.album_cover);
            complete_covers.setImageBitmap(coverParserParams_finished.cover);

        }
    }


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

    private void initializeBandView() {
        setContentView(R.layout.band_page_layout);
        context = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        //toolbar.setTitle("");
        setSupportActionBar(toolbar);

        disco_complete_view = findViewById(R.id.disco_complete_view);
        disco_main_view = findViewById(R.id.disco_main_view);
        disco_lives_view = findViewById(R.id.disco_lives_view);
        disco_demos_view = findViewById(R.id.disco_demos_view);
        disco_misc_view = findViewById(R.id.disco_misc_view);

        disco_complete_view.setOnItemClickListener(discoListener);
        disco_main_view.setOnItemClickListener(discoListener);
        disco_lives_view.setOnItemClickListener(discoListener);
        disco_demos_view.setOnItemClickListener(discoListener);
        disco_misc_view.setOnItemClickListener(discoListener);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        search = findViewById(R.id.search_edit);
        search_clear = findViewById(R.id.clear_search);
        search_clear.setOnClickListener(clearSearchListener);

        // listen for search command from keyboard
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search.clearFocus(); // close keyboard
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
                    album_count = 0;
                    band_of_interest = search.getText().toString();
                    getBand(search.getText().toString());
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

    private AdapterView.OnItemClickListener discoListener = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View v,
                                int position, long id) {
            Intent disco_intent = new Intent(context, DiscoFocusActivity.class);
            TextView title = v.findViewById(R.id.disco_item_title);
            disco_intent.putExtra("ITEM_NAME", title.getText()); //this is complete array

            TextView disco_item_src = v.findViewById(R.id.disco_item_src);
            disco_intent.putExtra("ITEM_URL", disco_item_src.getText());
            TextView disco_review_src = v.findViewById(R.id.disco_item_reviews);
            disco_intent.putExtra("ITEM_REVIEW_SRC", disco_review_src.getText());

            startActivityForResult(disco_intent, OPEN_NEW_ACTIVITY);
        }
    };


    private void initializeSearchView() {
        setContentView(R.layout.activity_search);
        context = this;
    }

    private void initializeSearchResultView(SearchResultPage result) {
        setContentView(R.layout.recycle_search_result);
        context = this;

        recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);

        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        search = findViewById(R.id.search_edit);

//        earch.requestFocus(); // open keyboard for search
//        if(search.requestFocus()) {
//            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//        }s

        // listen for search command from keyboard
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search.clearFocus(); // close keyboard
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
                    album_count = 0;
                    band_of_interest = search.getText().toString();
                    getBand(search.getText().toString());
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
        //System.out.println(searchResultPage.bandLinks()[position]);
        //System.out.println(searchResultPage.bandLinks()[position].substring(searchResultPage.bandLinks()[position].lastIndexOf('/') - (band_of_interest.length())));
        getBand(searchResultPage.bandLinks()[position].substring(searchResultPage.bandLinks()[position].lastIndexOf('/') - (band_of_interest.length())));
    }

    private View.OnClickListener clearSearchListener = new View.OnClickListener() {
        public void onClick(View v) {
            search.setText("");
        }
    };

    private View.OnClickListener spotifyListener = new View.OnClickListener(){
        public void onClick(View v) {
//            Intent spotify_intent = new Intent(context, SpotifyActivity.class);
//            startActivity(spotify_intent);
        }
    };


    private View.OnClickListener closeCommentListener = new View.OnClickListener() {
        public void onClick(View v) {
            comment_popup.dismiss();
            main_view.setAlpha(1.0F);
        }
    };


    private class CommentParser extends AsyncTask<String, Integer, String> { //URL input, Integer progress, Lyric result
        protected String doInBackground(String... params) {
            String band_id = params[0];

            try {
                Document comment_doc;
                comment_doc = Jsoup.connect("https://metal-archives.com/band/read-more/id/" + band_id).get();
                comment_doc.outputSettings(new Document.OutputSettings().prettyPrint(false)); //makes html() preserve linebreaks and spacing
                comment_doc.select("br").append("\n");
                String comment_body = comment_doc.html().replaceAll("\\\\n", "\n");
                System.out.println(Jsoup.clean(comment_body, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false)));
                bandPage.setMoreComment(Jsoup.clean(comment_body, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false)));
            } catch (IOException e) {
                System.out.println(e.toString());
            }

            return bandPage.more_comment();
        }

        protected void onProgressUpdate(Integer... progress){

        }

        protected void onPostExecute(String result) {
           // comment_content.setText(bandPage.more_comment());
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == OPEN_NEW_ACTIVITY) {
//            if (search != null && search.requestFocus()) {
//                search.clearFocus();
//                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
//                imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
//            }
//        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();
        return true;
    }


}