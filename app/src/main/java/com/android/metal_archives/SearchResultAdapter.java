package com.android.metal_archives;

/**
 * Created by BEJ2PLY on 9/14/2017.
 */
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {
    private List<String> bands;
    private List<String> details;
    private Context mContext;

    public SearchResultAdapter(List<String> NameDataset, List<String> DetailsDataset, Context context) {
        bands = NameDataset;
        details = DetailsDataset;
        this.mContext = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtHeader;
        public TextView txtFooter;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            txtHeader = (TextView) v.findViewById(R.id.name);
            txtFooter = (TextView) v.findViewById(R.id.details);
        }
    }

    public void add(int position, String item) {
        bands.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        bands.remove(position);
        notifyItemRemoved(position);
    }



    @Override
    public SearchResultAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.search_result_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final String name = bands.get(position);
        final String detail = details.get(position);
        holder.txtHeader.setText(name);

        holder.txtHeader.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mContext instanceof SearchableActivity){
                    ((SearchableActivity)mContext).onRecycleViewSelected(position);
                }

            }
        });

        holder.txtFooter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mContext instanceof SearchableActivity){
                    ((SearchableActivity)mContext).onRecycleViewSelected(position);
                }
            }
        });

        holder.txtFooter.setText(detail);
    }

    @Override
    public int getItemCount() {
        return bands.size();
    }


}
