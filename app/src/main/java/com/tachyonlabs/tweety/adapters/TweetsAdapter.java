package com.tachyonlabs.tweety.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tachyonlabs.tweety.R;
import com.tachyonlabs.tweety.models.Tweet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

// based on http://guides.codepath.com/android/Using-the-RecyclerView

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class TweetsAdapter extends
        RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    /*****
     * Creating OnItemClickListener
     *****/
    // Define listener member variable
    private static OnItemClickListener listener;

    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public ImageView ivProfileImage;
        public TextView tvUserName;
        public TextView tvScreenName;
        public TextView tvRelativeTime;
        public TextView tvBody;
        private Context context;

        public ViewHolder(final View itemView) {
            super(itemView);
            this.tvUserName = (TextView) itemView.findViewById(R.id.tvUserName);
            this.tvScreenName = (TextView) itemView.findViewById(R.id.tvScreenName);
            this.tvRelativeTime = (TextView) itemView.findViewById(R.id.tvRelativeTIme);
            this.tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            this.ivProfileImage = (ImageView) itemView.findViewById(R.id.ivMyProfileImage);
            // Setup the click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Triggers click upwards to the adapter on click
                    if (listener != null)
                        listener.onItemClick(itemView, getLayoutPosition());
                }
            });
        }
    }

    // Store a member variable for the tweets
    private List<Tweet> mTweets;

    // Pass in the tweet array into the constructor
    public TweetsAdapter(List<Tweet> tweets) {
        mTweets = tweets;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public TweetsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Tweet tweet = mTweets.get(position);

        // Set item views based on the data model
        TextView tvUserName = viewHolder.tvUserName;
        tvUserName.setText(tweet.getUser().getName() + " ");
        TextView tvScreenName = viewHolder.tvScreenName;
        tvScreenName.setText("@" + tweet.getUser().getScreenName());
        TextView tvRelativeTime = viewHolder.tvRelativeTime;
        tvRelativeTime.setText(relativeDate(tweet.getCreatedAt()));
        TextView tvBody = viewHolder.tvBody;
        tvBody.setText(tweet.getBody());

        // populate the thumbnail image
        // remotely download, or use the placeholder if there is no thumbnail
        ImageView ivProfileImage = viewHolder.ivProfileImage;
        Picasso.with(viewHolder.ivProfileImage.getContext()).load(tweet.getUser().getProfileImageUrl()).fit().centerCrop().into(ivProfileImage);
    }

    private String relativeDate(String createdAt) {
        // get a Twitter-app-style string of how long ago the tweet was tweeted
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);
        String dateString;
        long msDate = 0;
        try {
            msDate = sf.parse(createdAt).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // the Math.max is because occasionally I see a newest tweet from Twitter with a value like
        // negative three seconds
        long relativeDate = Math.max(System.currentTimeMillis() - msDate, 0);
        if (relativeDate > 604800000L) {
            dateString = (relativeDate / 604800000L) + "w";
        } else if (relativeDate > 86400000L) {
            dateString = (relativeDate / 86400000L) + "d";
        } else if (relativeDate > 3600000L) {
            dateString = (relativeDate / 3600000L) + "h";
        } else if (relativeDate > 60000) {
            dateString = (relativeDate / 60000) + "m";
        } else {
            dateString = (relativeDate / 1000) + "s";
        }

        return dateString;
    }

    // Return the total count of items
    @Override
    public int getItemCount() {
        return mTweets.size();
    }

}