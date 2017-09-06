package com.android.metal_archives;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Author: James Berry
 * Description: Band/Logo image parser class
 */

public class ImageParser extends AppCompatActivity {
    // parameters
    private Elements sidebar_;
    private Bitmap logo_;
    private Bitmap band_pic_;

    // constructor
    public ImageParser(Elements sidebar_in){
        sidebar_ = sidebar_in;
        logo_ = null; // TODO: load default image for bands without logo
        band_pic_ = null; // TODO: load default image for bands without band picture
    }

    // accessors and mutators
    public Bitmap logo(){
        return logo_;
    }

    public Bitmap band_pic(){
        return band_pic_;
    }

    // methods
    public void fetchLogo(){
        for (Element spec : sidebar_) {
            Elements logo = spec.select("div.band_name_img");
            String img_source = logo.select("a").attr("href");
            try {
                InputStream input = new URL(img_source).openStream();
                logo_ = BitmapFactory.decodeStream(input);
            } catch (IOException e){
                System.out.println(e.toString());
            }
        }
    }

    public void fetchBandPic(){
        for (Element spec : sidebar_) {
            Elements band_pic = spec.select("div.band_img");
            String img_source = band_pic.select("a").attr("href");
            try {
                InputStream input = new URL(img_source).openStream();
                band_pic_ = BitmapFactory.decodeStream(input);
            } catch (IOException e){
                System.out.println(e.toString());
            }
        }
    }
}
