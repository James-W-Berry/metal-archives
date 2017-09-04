package com.android.metal_archives;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Author: James Berry
 * Description: class for all discography items
 */

public class DiscoItem extends AppCompatActivity{
    // constructor
    public DiscoItem(){}

    // parameters
    private String title_;
    private Integer year_;
    private String format_;
    private Integer rating_;
    private Integer reviewers_;
    private Bitmap cover_;

    // accessors and mutators
    public String title(){
        return title_;
    }

    public Integer year(){
        return year_;
    }

    public String format(){
        return format_;
    }

    public Integer rating(){
        return rating_;
    }

    public Integer reviewers(){
        return reviewers_;
    }

    public Bitmap cover(){
        return cover_;
    }

    public void set_title(String title_in){
        title_ = title_in;
    }

    public void set_year(Integer year_in){
        year_ = year_in;
    }

    public void set_format(String format_in){
        format_ = format_in;
    }

    public void set_rating(Integer rating_in){
        rating_ = rating_in;
    }

    public void set_reviewers(Integer reviewers_in){
        reviewers_ = reviewers_in;
    }

    public void set_cover(Bitmap cover_in){
        cover_ = cover_in;
    }

    // methods
    public void Display(){
        Log.i("DiscoItem: ", title_ + " | " + year_ + " | " + format_ + " | " + rating_ + " | " + reviewers_);
    }

}
