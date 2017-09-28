package com.android.metal_archives;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by bej2ply on 9/27/2017.
 */

public class TrackAdapter extends BaseAdapter {

    private final Context mContext;
    private final Integer tracks;
    private final DiscoItem discoItem;


    public TrackAdapter(Context context, Integer tracks_in, DiscoItem disco_item_in) {
        this.mContext = context;
        this.tracks = tracks_in;
        this.discoItem = disco_item_in;

    }

    @Override
    public int getCount() {
        return tracks;
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

        if(!(convertView instanceof View)){
            convertView = inflater.inflate(R.layout.track_item, null, false);
            TextView title_copy = (TextView) convertView.findViewById(R.id.track_title);
            TextView number_copy = (TextView) convertView.findViewById(R.id.track_number);
            TextView duration_copy = (TextView) convertView.findViewById(R.id.track_duration);
            TextView lyrics_copy = (TextView) convertView.findViewById(R.id.track_lyrics);


            if(discoItem.tracks()[position] == null && !(position == discoItem.track_count() - 1)) {
                // likely a record side or separate disc distinction
                number_copy.setText(discoItem.track_number()[position]);
                duration_copy.setVisibility(View.GONE);
            } else if(position == discoItem.track_count() - 1) {
                // last track row, album duration
                number_copy.setText("Total duration: ");
                title_copy.setText(discoItem.tracks()[position]);
            } else {
                //System.out.println("adding track " + discoItem.tracks()[position]);
                number_copy.setText(discoItem.track_number()[position]);
                title_copy.setText(discoItem.tracks()[position]);
                duration_copy.setText(discoItem.track_length()[position]);
                lyrics_copy.setText("lyrics >");
            }
        } else {
            // track item already added for this position
        }

        return convertView;
    }

}