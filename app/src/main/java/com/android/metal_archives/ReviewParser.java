package com.android.metal_archives;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by BEJ2PLY on 10/1/2017.
 */

public class ReviewParser {

    // members
    private Document review_doc;
    private Integer review_count;
    private String[] review_titles_;
    private String[] review_details_;
    private String[] review_contents_;
    private Integer part_counter;

    // constructor
    public ReviewParser(Document review_doc_in){
        this.review_doc = review_doc_in;
        this.review_count = 0;
        this.part_counter = 0;
        this.review_titles_ = new String[100];
        this.review_details_ = new String[100];
        this.review_contents_ = new String[100];
    }

    // accessors and mutators
    public String[] review_titles(){
        return review_titles_;
    }

    public String[] review_details(){
        return review_details_;
    }

    public String[] review_contents(){
        return review_contents_;
    }

    // methods
    public Integer parseReviews() {
        Elements review_boxes = review_doc.select("div.reviewBox");

        for(Element review_box : review_boxes) {
            review_titles_[review_count] = review_box.select("h3.reviewTitle").text();
            review_details_[review_count] = review_box.select("a.profileMenu").text();
            review_contents_[review_count] = review_box.select("div.reviewContent").select("p").text();
            review_count ++;
        }

        return review_count;
    }
}
