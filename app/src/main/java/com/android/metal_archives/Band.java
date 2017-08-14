package com.android.metal_archives;

import android.util.Log;

/**
 * Author: James Berry
 * Description: Band class interface and implementation
 */

public class Band {
    // constructor
    public Band(){}

    // instance variables
    private String name_;
    private String genre_;
    private String country_;
    private String status_;

    // accessors and mutators
    public String name(){
        return name_;
    }
    public String genre(){
        return genre_;
    }
    public String country(){
        return country_;
    }
    public String status(){
        return status_;
    }
    public void set_name(String in_name){
        name_ = in_name;
    }

    public void set_genre(String in_genre){
        genre_ = in_genre;
    }

    public void set_coutry(String in_country){
        country_ = in_country;
    }

    public void set_status(String in_status){
        status_ = in_status;
    }

    // methods
    public void Display(){
        Log.i("Band.java", name_ + " | " + genre_ + " | " + country_ + " | " + status_);
    }
}
