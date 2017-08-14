package com.android.metal_archives;

import android.util.Log;

/**
 * Author: James Berry
 * Description: User class interface and implementation
 */

public class User {
    // constructor
    public User(){}

    // instance variables
    private String user_name_;
    private String password_;
    private String email_;
    private int ranking_;
    private String title_;

    // accessors and mutators
    public String user_name(){
        return user_name_;
    }
    public String password(){
        return password_;
    }
    public String email(){
        return email_;
    }
    public int ranking(){
        return ranking_;
    }
    public String title(){
        return title_;
    }

    // methods
    public void Promote(int increment){
        ranking_ = ranking_++;
    }
    public void Demote(int decrement){
        ranking_ = ranking_--;
    }
    public void Display(){
        Log.i("User.java", user_name_ + " | " + password_ + " | " + email_ + " | " + ranking_ + " | " + title_);
    }

}
