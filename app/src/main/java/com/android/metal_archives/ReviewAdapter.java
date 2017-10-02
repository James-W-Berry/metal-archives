package com.android.metal_archives;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by BEJ2PLY on 10/1/2017.
 */

public class ReviewAdapter extends BaseAdapter {
    private final Context mContext;
    private final Integer reviews;
    private final DiscoItem discoItem;


    public ReviewAdapter(Context context, Integer reviews_in, DiscoItem disco_item_in) {
        this.mContext = context;
        this.reviews = reviews_in;
        this.discoItem = disco_item_in;

    }

    @Override
    public int getCount() {
        return reviews;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(!(convertView instanceof View)) {
            convertView = inflater.inflate(R.layout.review_item, null, false);
            TextView review_title = (TextView) convertView.findViewById(R.id.review_title);
            TextView review_details = (TextView) convertView.findViewById(R.id.review_details);
            TextView review_content = (TextView) convertView.findViewById(R.id.review_content);
            review_title.setText(discoItem.review_titles()[position]);
            review_details.setText(discoItem.review_details()[position]);
            review_content.setText(discoItem.review_contents()[position]);
        }

        return convertView;
    }

}
