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
    private String[] tracks_;
    private Integer track_count_;
    private String [] track_length_;
    private String [] track_number_;

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

    public String[] tracks(){
        return tracks_;
    }

    public String[] track_length(){
        return track_length_;
    }

    public String[] track_number(){
        return track_number_;
    }

    public Integer track_count(){
        return track_count_;
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

    public void set_tracks(String[] tracks_in){
        tracks_ = tracks_in;
    }

    public void set_track_count(Integer track_count_in){
        track_count_ = track_count_in;
    }

    public void set_track_lengths(String[] track_lenghts_in){
        track_length_ = track_lenghts_in;
    }

    public void set_track_numbers(String[] track_number_in){
        track_number_ = track_number_in;
    }

    // methods
    public void Display(){
        Log.i("DiscoItem: ", title_ + " | " + year_ + " | " + format_ + " | " + rating_ + " | " + reviewers_);
        for(int i = 0; i< tracks().length; i++){
            Log.i("DiscoItem: ", tracks()[i] + " | " + year_ + " | " + format_ + " | " + rating_ + " | " + reviewers_);

        }
    }

}
