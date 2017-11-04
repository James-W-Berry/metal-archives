package com.android.metal_archives;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by BEJ2PLY on 9/11/2017.
 */

public class ViewPageResult extends AppCompatActivity {
    public ViewPageResult(){}

    private BandPage band_page_;
    private SearchResultPage search_result_page_;

    public BandPage getBandPage(){
        return band_page_;
    }

    public SearchResultPage getSearchResultsPage(){
        return search_result_page_;
    }

    public void setBandPage(BandPage band_page_in){
        band_page_ = band_page_in;
    }

    public void setSearchResultPage(SearchResultPage search_result_page_in){
        search_result_page_ = search_result_page_in;
    }



}
