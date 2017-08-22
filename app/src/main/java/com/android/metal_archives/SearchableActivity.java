package com.android.metal_archives;

import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

/**
 * Created by bej2ply on 8/18/2017.
 */

public class SearchableActivity extends AppCompatActivity {
    private EditText search;
    private TextView content_view;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        content_view = (TextView) findViewById(R.id.test_data);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        search = (EditText) findViewById(R.id.search_edit);
        search.setVisibility(View.VISIBLE);
        search.requestFocus();
        if(search.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        displayBandData("Gojira");
    }

    private class RetrieveWebPageActivity extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url_page = new URL(urls[0]);
                try {
                    HttpURLConnection urlConnection = (HttpURLConnection) url_page.openConnection();
                    try {
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());



                        try {
                            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                                    .parse(new InputSource(new StringReader(readStream(in))));

                            try {
                                XPathExpression xpath = XPathFactory.newInstance()
                                        .newXPath().compile("//td[text()=\"band_name\"]/following-sibling::td[2]");
                                return ((String) xpath.evaluate(doc, XPathConstants.STRING));


                            } catch ( XPathException e){
                                Log.e("SearchableActivity", ""+e);
                            }
                        } catch (ParserConfigurationException | SAXException | IOException e){
                            Log.e("SearchableActivity", ""+e);
                        }




                    } finally {
                        urlConnection.disconnect();
                    }
                } catch (IOException e){
                    Log.e("SearchableActivity", ""+e);
                }
            } catch (MalformedURLException e){
                Log.e("SearchableActivity", ""+e);
            }
            return "error";
        }

        @Override
        protected void onPostExecute(String result) {
            setContent(result);
        }

        private String readStream(InputStream in_stream) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in_stream));
            StringBuilder result = new StringBuilder();
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            } catch (IOException e) {
                Log.e("SearchableActivity", "" + e);
            }
            System.out.println(result.toString());
            return result.toString();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onSupportNavigateUp() {
        search.setVisibility(View.GONE);
        onBackPressed();
        finish();
        return true;
    }

    public void displayBandData(String band_name){
        String url = "https://www.metal-archives.com/bands/";
        url += band_name;
        Log.i("SearchableActivity", ""+url);

        RetrieveWebPageActivity retrieveWebPageActivity = new RetrieveWebPageActivity();
        retrieveWebPageActivity.execute(url);



    }

    public void setContent(String content){
        content_view.setText(content);
    }


}
