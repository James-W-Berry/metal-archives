package com.android.metal_archives;

import android.support.v7.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Author: James Berry
 * Description: Discography parser class
 */

public class DiscoParser extends AppCompatActivity {

    // constructor
    public DiscoParser(Document document){
        full_document = document;
        name_ = new String[100];
        name_src_ = new String[100];
        type_ = new String[100];
        year_ = new String[100];
        score_ = new String[100];
        review_src_ = new String[100];

    }

    // parameters
    private Document full_document;
    private String[] name_;
    private String[] name_src_;
    private String[] type_;
    private String[] year_;
    private String[] score_;
    private String[] review_src_;

    // accessors and mutators
    public String[] names(){
        return name_;
    }

    public String[] name_srcs(){
        return name_src_;
    }

    public String[] types(){
        return type_;
    }

    public String[] years(){
        return year_;
    }

    public String[] scores(){
        return score_;
    }

    public String[] review_srcs(){
        return review_src_;
    }

    // methods
    public Integer fetchComplete(){
        String src = getCompleteDiscoSource();
        Integer album_count = 0;

        try {
            Document discography = Jsoup.connect(src).get();
            Elements disco_list = discography.select("table").select("tr");

            Integer row_tracker = 0;
            for (Element row : disco_list) {
                if(row_tracker == 0){
                    row_tracker++;
                } else {
                    album_count++;
                    Elements columns = row.select("td");
                    Integer column_index = 0;

                    for (Element column : columns) {
                        switch (column_index) {
                            case 0:
                                Elements album = column.select("a");
                                name_[album_count - 1] = album.text();
                                name_src_[album_count - 1] = album.attr("href");
                                break;
                            case 1:
                                type_[album_count - 1] = column.text();
                                break;
                            case 2:
                                year_[album_count - 1] = column.text();
                                break;
                            case 3:
                                Elements album_reviews = column.select("a");
                                score_[album_count - 1] = album_reviews.text();
                                review_src_[album_count - 1] = album_reviews.attr("href");
                                break;
                            default:
                                break;
                        }
                        column_index++;
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Error : " + e.getMessage() + "\n");
        }
        System.out.println(album_count.toString() + " albums");
        return album_count;
    }

    private String getCompleteDiscoSource(){
        Elements disco_options = full_document.select("div#band_disco").select("ul");
        Elements disco_complete = disco_options.select("li").first().select("a");
        return(disco_complete.attr("href"));
    }
}
