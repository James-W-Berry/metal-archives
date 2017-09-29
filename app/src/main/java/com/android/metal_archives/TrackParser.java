package com.android.metal_archives;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by bej2ply on 9/26/2017.
 */

public class TrackParser {

    // constructor
    public TrackParser(Document doc) {
        full_document = doc;
        //System.out.println(doc);
        tracks_ = new String[100];
        lyric_urls_ = new String [100];
        track_num_ = new String[100];
        track_length_ = new String[100];
    }

    // parameters
    private Document full_document;
    private String[] track_num_;
    private String[] track_length_;
    private String[] tracks_;
    private String[] lyric_urls_;

    // accessors and mutators
    public String[] tracks(){
        return tracks_;
    }

    public String[] track_numbers(){
        return track_num_;
    }

    public String[] track_lengths(){
        return track_length_;
    }

    public String[] urls(){
        return lyric_urls_;
    }

    public void setTracks(String[] tracks_in){
        tracks_ = tracks_in;
    }
    public void setLyricUrls(String[] lyric_urls_in){
        lyric_urls_ = lyric_urls_in;
    }

    // methods
    public Integer parseTracks(){
        Elements track_table = full_document.select("table.display.table_lyrics").select("tr");
        Integer relevant_row_count = 0;

        for(Element row : track_table) {
            Elements tds = row.select("td");
            Integer column_counter = 0;
            if (row.className().equals("displayNone")) {
                // skipping lyric loading row
            } else {
                for (Element td : tds) {
                    switch (column_counter.toString()) {
                        case "0":
                            track_num_[relevant_row_count] = td.text();
                            break;
                        case "1":
                            tracks_[relevant_row_count] = td.text();
                            break;
                        case "2":
                            track_length_[relevant_row_count] = td.text();
                            break;
                        case "3":
                            lyric_urls_[relevant_row_count] = td.select("a").attr("href");
                            break;
                        default:
                            break;
                    }
                    column_counter++;
                }
                relevant_row_count++;
            }
        }

        return relevant_row_count;
    }

}
