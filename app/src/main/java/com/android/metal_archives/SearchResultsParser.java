package com.android.metal_archives;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Author: James Berry
 * Description: Search results parser class
 */

public class SearchResultsParser extends AppCompatActivity{
    // constructor
    public SearchResultsParser(Elements results_in){
        results_table_ = results_in;
        band_results_ = new String[100];
        band_links_ = new String[100];
    }

    // parameters
    private String[] band_results_;
    private String[] band_links_;
    private Elements results_table_;
    private Integer band_number_;

    // accessors and mutators
    public String[] bandResults(){
        return band_results_;
    }

    public String[] bandLinks(){
        return band_links_;
    }

    public Integer bandNumber(){
        return band_number_;
    }

    // methods
    public void fetchBands(){
        band_number_ = 0;

        for (Element item : results_table_.select("li")) {
            band_results_[band_number_] = item.text();
            band_links_[band_number_] = item.select("a").attr("href");
            band_number_++;
        }
    }
}
