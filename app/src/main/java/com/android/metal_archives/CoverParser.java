package com.android.metal_archives;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by bej2ply on 9/28/2017.
 */

public class CoverParser {

    private final Document document;
    private Bitmap cover_;

    // constructor
    public CoverParser(Document doc){
        this.document = doc;
        this.cover_ = null;
        parseCover();
    }

    // accessors and mutators
    public Bitmap cover(){
        return cover_;
    }

    // methods
    private void parseCover(){
        String cover_source = document.select("a#cover").attr("href");
        try {
            InputStream input = new URL(cover_source).openStream();
            cover_ = BitmapFactory.decodeStream(input);
        } catch (IOException e){
            System.out.println(e.toString());
        }
    }
}
