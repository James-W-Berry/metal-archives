package com.android.metal_archives;

import android.support.v7.app.AppCompatActivity;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Iterator;

/**
 * Author: James Berry
 * Description: Band information parser class
 */

public class InfoParser extends AppCompatActivity {

    // constructor
    public InfoParser(){

    }

    // parameters
    private String name_;
    private String comment_;
    private String country_;
    private String location_;
    private String status_;
    private String founded_;
    private String genre_;
    private String lyrical_themes_;
    private String label_;


    // accessors and mutators
    public String name(){
        return name_;
    }
    public String comment(){
        return comment_;
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

    public String founded(){
        return founded_;
    }

    public String genre(){
        return genre_;
    }

    public String lyrical_themes(){
        return lyrical_themes_;
    }

    public String label(){
        return label_;
    }

    // methods
    public void parseComment(Elements comment_in){
        for (Element item : comment_in) {
            Elements comment = item.select("div.band_comment");
            comment_ = comment.text();
            //Elements more_comment = item.select("div.tool_strip");
        }

    }

    public void parseName(Elements name_in){
        for (Element item : name_in){
            Elements name = item.select("h1.band_name");
            name_ = name.text();
        }

    }

    public void parseInfo(Elements info_in){
        for (Element item : info_in) {
            Iterator<Element> left_key_iterator = item.select("dl.float_left").select("dt").iterator();
            Iterator<Element> left_value_iterator = item.select("dl.float_left").select("dd").iterator();
            Iterator<Element> right_key_iterator = item.select("dl.float_right").select("dt").iterator();
            Iterator<Element> right_value_iterator = item.select("dl.float_right").select("dd").iterator();

            while (left_key_iterator.hasNext() && left_value_iterator.hasNext()) {
                Element key = left_key_iterator.next();
                Element value = left_value_iterator.next();

                switch (key.text()) {
                    case "Country of origin:":
                        country_ = key.text() + " " + value.text().replaceAll("\\s{2,}", " ").trim();
                        break;
                    case "Location:":
                        location_ = key.text() + " " + value.text().replaceAll("\\s{2,}", " ").trim();
                        break;
                    case "Status:":
                        status_ = key.text() + " " + value.text().replaceAll("\\s{2,}", " ").trim();
                        break;
                    case "Formed in:":
                        founded_ = key.text() + " " + value.text().replaceAll("\\s{2,}", " ").trim();
                        break;
                    default:
                        break;
                }

            }

            while (right_key_iterator.hasNext() && right_value_iterator.hasNext()) {
                Element key = right_key_iterator.next();
                Element value = right_value_iterator.next();

                switch (key.text()) {
                    case "Genre:":
                        genre_ = key.text() + " " + value.text().replaceAll("\\s{2,}", " ").trim();
                        break;
                    case "Lyrical themes:":
                        lyrical_themes_ = key.text() + " " + value.text().replaceAll("\\s{2,}", " ").trim();
                        break;
                    case "Current label:":
                        label_ = key.text() + " " + value.text().replaceAll("\\s{2,}", " ").trim();
                        break;
                    default:
                        break;
                }

            }
        }
    }
}
