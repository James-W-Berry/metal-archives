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
    public DiscoItem(){
    }

    // parameters
    private String title_;
    private Integer year_;
    private String format_;
    private Integer rating_;
    private Integer reviewers_;
    private Bitmap cover_;
    private String[] tracks_;
    private Integer track_count_;
    private String[] track_length_;
    private String[] track_number_;
    private String[] track_lyric_urls_;
    private String track_lyrics_;
    private String[] review_titles_;
    private String[] review_details_;
    private String[] review_contents_;
    private Integer review_count_;

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

    public String[] track_lyric_urls(){
        return track_lyric_urls_;
    }

    public String track_lyrics(){
        return track_lyrics_;
    }

    public String[] review_titles(){
        return review_titles_;
    }

    public String[] review_details(){
        return review_details_;
    }
    public String[] review_contents(){
        return review_contents_;
    }

    public Integer review_count(){
        return review_count_;
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

    public void set_track_lyric_urls(String[] track_lyric_urls_in){
        track_lyric_urls_ = track_lyric_urls_in;
    }

    public void set_track_lyrics(String track_lyrics_in){
        track_lyrics_ = track_lyrics_in;
    }

    public void set_review_titles(String[] review_titles_in){
        review_titles_ = review_titles_in;
    }

    public void set_review_details(String[] review_details_in){
        review_details_ = review_details_in;
    }

    public void set_review_contents(String[] review_contents_in){
        review_contents_ = review_contents_in;
    }

    public void set_review_count(Integer review_count_in){
        review_count_ = review_count_in;
    }

    // methods
    public void Display(){
        Log.i("DiscoItem: ", title_ + " | " + year_ + " | " + format_ + " | " + rating_ + " | " + reviewers_);
        for(int i = 0; i< tracks().length; i++){
            Log.i("DiscoItem: ", tracks()[i] + " | " + year_ + " | " + format_ + " | " + rating_ + " | " + reviewers_);

        }
    }

}
