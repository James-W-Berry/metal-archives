package com.android.metal_archives;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;

import org.jsoup.nodes.Document;

import java.util.Arrays;

/**
 * Author: James Berry
 * Description:
 */

public class BandPage extends AppCompatActivity{

    // constructor
    public BandPage(){
        disco_item_cover_ = new Bitmap[100];
    }

    // parameters
    private String name_;
    private String country_;
    private String location_;
    private String status_;
    private String year_formed_;
    private String years_active_;
    private String genre_;
    private String lyrical_themes_;
    private String label_;
    private String comment_;
    private String more_comment_;
    private Bitmap logo_;
    private Bitmap band_pic_;
    private String[] disco_item_name_;
    private String[] disco_item_name_src_;
    private String[] disco_item_type_;
    private String[] disco_item_year_;
    private String[] disco_item_score_;
    private String[] disco_item_review_src_;
    private Bitmap[] disco_item_cover_;

    // accessors and mutators
    public String name(){
        return name_;
    }

    public String country(){
        return country_;
    }

    public String location(){
        return location_;
    }

    public String status(){
        return status_;
    }

    public String yearFormed(){
        return year_formed_;
    }

    public String yearsActive(){
        return years_active_;
    }

    public String genre(){
        return genre_;
    }

    public String lyricalThemes(){
        return lyrical_themes_;
    }

    public String label(){
        return label_;
    }

    public String comment(){
        return comment_;
    }

    public String more_comment(){
        return more_comment_;
    }

    public Bitmap logo(){
        return logo_;
    }

    public Bitmap bandPic(){
        return band_pic_;
    }

    public String[] discoItemName(){
        return disco_item_name_;
    }

    public String[] discoItemNameSrc(){
        return disco_item_name_src_;
    }

    public String[] discoItemType(){
        return disco_item_type_;
    }

    public String[] discoItemYear(){
        return disco_item_year_;
    }

    public String[] discoItemScore(){
        return disco_item_score_;
    }

    public String[] discoItemScoreSrc(){
        return disco_item_review_src_;
    }

    public Bitmap[] discoItemCover(){
        return disco_item_cover_;
    }

    public void setName(String name_in){
        name_ = name_in;
    }

    public void setCountry(String country_in){
        country_ = country_in;
    }

    public void setLocation(String location_in){
        location_ = location_in;
    }

    public void setStatus(String status_in){
        status_ = status_in;
    }

    public void setYearFormed(String year_formed_in){
        year_formed_ = year_formed_in;
    }

    public void setYearsActive(String years_active_in){
        years_active_ = years_active_in;
    }

    public void setGenre(String genre_in){
        genre_ = genre_in;
    }

    public void setLyricalThemes(String lyrical_themes_in){
        lyrical_themes_ = lyrical_themes_in;
    }

    public void setLabel(String label_in){
        label_ = label_in;
    }

    public void setComment(String comment_in){
        comment_ = comment_in;
    }

    public void setMoreComment(String more_comment_in){
        more_comment_ = more_comment_in;
    }

    public void setLogo(Bitmap logo_in){
        logo_ = logo_in;
    }

    public void setBandPic(Bitmap band_pic_in){
        band_pic_ = band_pic_in;
    }

    public void setDiscoItemName(String[] disco_item_name_in){
        disco_item_name_ = disco_item_name_in;
    }

    public void setDiscoItemNameSrc(String[] disco_item_name_src_in){
        disco_item_name_src_ = disco_item_name_src_in;
    }

    public void setDiscoItemType(String[] disco_item_type_in){
        disco_item_type_ = disco_item_type_in;
    }

    public void setDiscoItemYear(String[] disco_item_year_in){
        disco_item_year_ = disco_item_year_in;
    }

    public void setDiscoItemScore(String[] disco_item_score_in){
        disco_item_score_ = disco_item_score_in;
    }

    public void setDiscoItemReviewSrc(String[] disco_item_review_src_in){
        disco_item_review_src_ = disco_item_review_src_in;
    }

    public void setDiscoItemCover(Bitmap cover_in, Integer index_in){
        disco_item_cover_[index_in] = cover_in;
    }

    // methods
    public void display(){
        System.out.println(name_);
        System.out.println(country());
        System.out.println(location_);
        System.out.println(status_);
        System.out.println(year_formed_);
        System.out.println(years_active_);
        System.out.println(genre_);
        System.out.println(lyrical_themes_);
        System.out.println(label_);
        System.out.println(comment_);
        System.out.println(more_comment_);
        System.out.println(Arrays.toString(disco_item_name_));
    }
}