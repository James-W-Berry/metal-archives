package com.android.metal_archives;

/**
 * Created by bej2ply on 9/18/2017.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DiscographyAdapter extends BaseAdapter {

    private final Context mContext;
    private final BandPage bandPage;
    private final Integer disco_items;
    private final Integer relevant_items;
    private final Integer[] filter_index;


    public DiscographyAdapter(Context context, BandPage bandPage_in, Integer disco_items_in, Integer relevant_items_in, Integer[] filter_index_in) {
        this.mContext = context;
        this.bandPage = bandPage_in;
        this.disco_items = disco_items_in;
        this.filter_index = filter_index_in;
        this.relevant_items = relevant_items_in;
    }

    @Override
    public int getCount() {
        return relevant_items;
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
        View disco_item = inflater.inflate(R.layout.band_disco_item, null, false);

        if(bandPage.discoItemType()[position] != null) {

            TextView title_copy = (TextView) disco_item.findViewById(R.id.disco_item_title);
            TextView year_copy = (TextView) disco_item.findViewById(R.id.disco_item_year);
            TextView type_copy = (TextView) disco_item.findViewById(R.id.disco_item_type);
            TextView score_copy = (TextView) disco_item.findViewById(R.id.disco_item_score);
            TextView reviews_copy = (TextView) disco_item.findViewById(R.id.disco_item_reviews);

            for (int i = 0; i < disco_items; i++) {
                if (filter_index[i] == 1) {
                    System.out.println("found relevant disco item: "+ bandPage.discoItemName()[position]
                            +" at index "+ Integer.toString(i));
                    title_copy.setText(bandPage.discoItemName()[position]);
                    year_copy.setText(bandPage.discoItemYear()[position]);
                    type_copy.setText(bandPage.discoItemType()[position]);
                    score_copy.setText(bandPage.discoItemScore()[position]);
                    reviews_copy.setText("Reviews");
                    filter_index[i] = 0; // clear the index
                    break;
                }
            }
        }

        return disco_item;

    }

}