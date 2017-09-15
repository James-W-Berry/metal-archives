package com.android.metal_archives;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by BEJ2PLY on 9/11/2017.
 */

public class SearchResultPage extends AppCompatActivity {

    // constructor
    public SearchResultPage(){}

    // parameters
    private String[] bands_;
    private String[] band_links_;

    // accessors and mutators

    // methods
    public String[] bands(){
        return bands_;
    }

    public String[] bandLinks(){
        return band_links_;
    }

    public void setBands(String[] bands_in){
        bands_ = bands_in;
    }

    public void setBandLinks(String[] band_links_in){
        band_links_ = band_links_in;
    }

    public void display(){
        // test for search result data -> print to console
        for(Integer i = 0; i<bands_.length; i++){
            System.out.println(bands_[i]);
            System.out.println(band_links_[i] + "\n");
        }
    }

}
