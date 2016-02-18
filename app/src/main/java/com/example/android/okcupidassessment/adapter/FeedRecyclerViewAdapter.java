package com.example.android.okcupidassessment.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.okcupidassessment.R;
import com.example.android.okcupidassessment.model.User;
import com.example.android.okcupidassessment.util.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sohail on 2/12/16.
 *
 * Adapter class to populate search feed
 */
public class FeedRecyclerViewAdapter extends RecyclerView.Adapter<FeedRecyclerViewAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private List<User> data = new ArrayList<>();
    private Context context;
    private ImageLoader imageLoader;
    private int imageSize;

    public FeedRecyclerViewAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        imageLoader = new ImageLoader(context);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        return new MyViewHolder(inflater.inflate(R.layout.feed_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // data is empty, stop method right away
        if (data.isEmpty()) return;

        User currentData = data.get(position);
        holder.username.setText(currentData.getUsername());
        holder.age.setText(String.valueOf(currentData.getAge()));
        holder.location.setText(context.getResources().getString(
                R.string.user_location, currentData.getCity_name(), currentData.getState_code()));
        holder.percentMatch.setText(context.getResources().getString(
                R.string.user_match, currentData.getMatch(), "%"));

        /**
         * get image path for large image from the {@link Photo}
         * call {@link ImageLoader} to display image
         */
        String imagePath = currentData.getPhoto().getFull_paths().getLarge();
        imageLoader.displayImage(imagePath, holder.profilePicture);
    }

    /**
     * insert new data
     */
    public void insertData(List<User> newData) {
        int currentLen = data.size();
        data.addAll(newData);
        notifyItemRangeInserted(currentLen, newData.size());
    }

    public void clearData() {
        data.clear();
        notifyDataSetChanged();
    }

    protected class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView profilePicture;
        TextView username;
        TextView age;
        TextView location;
        TextView percentMatch;

        public MyViewHolder(View itemView) {
            super(itemView);

            profilePicture = (ImageView) itemView.findViewById(R.id.profile_picture);
            username = (TextView) itemView.findViewById(R.id.username);
            age = (TextView) itemView.findViewById(R.id.age);
            location = (TextView) itemView.findViewById(R.id.location);
            percentMatch = (TextView) itemView.findViewById(R.id.percent_match);
        }
    }

    public void setImageSize(int imageSize) {
        this.imageSize = imageSize;
    }
}
