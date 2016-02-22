package com.tachyonlabs.tweety.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.tachyonlabs.tweety.R;
import com.tachyonlabs.tweety.adapters.TweetsAdapter;
import com.tachyonlabs.tweety.fragments.ComposeFragment;
import com.tachyonlabs.tweety.fragments.TweetDetailFragment;
import com.tachyonlabs.tweety.models.Tweet;
import com.tachyonlabs.tweety.models.User;
import com.tachyonlabs.tweety.utils.TwitterApplication;
import com.tachyonlabs.tweety.utils.TwitterClient;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TimelineActivity extends AppCompatActivity {

    RecyclerView rvTweets;
    ArrayList<Tweet> tweets;
    TweetsAdapter adapter;
    private TwitterClient client;
    private SwipeRefreshLayout swipeContainer;
    User myUserAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(0);
            }
        });

        // Configure the refreshing colors
        // alternate colors between Twitter blue and Tweety yellow :-)
        swipeContainer.setColorSchemeColors(0xFF55acee, 0xFFFFFF00, 0xFF55acee, 0xFFFFFF00);
        rvTweets = (RecyclerView) findViewById(R.id.rvTweets);
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(tweets);
        rvTweets.setAdapter(adapter);
        // Set layout manager to position the items
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.addOnScrollListener(new com.tachyonlabs.tweety.utils.EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                populateTimeline();
            }
        });

        // hook up listener for tweet tap to view tweet detail
        adapter.setOnItemClickListener(new TweetsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showTweetDetailDialog(position);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        //toolbar.setTitleTextAppearance(this, R.style.MyTextAppearance);
        client = TwitterApplication.getRestClient(); // singleton client
        populateTimeline();
        getMyUserJson();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showComposeDialog();
            }
        });
    }

    public void fetchTimelineAsync(int page) {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        long since_id;
        long max_id = 0;
        Tweet newestDisplayedTweet = tweets.get(0);
        since_id = newestDisplayedTweet.getUid();
        client.getHomeTimeline(since_id, max_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                Log.d("DEBUG", json.toString());
                tweets.addAll(0, Tweet.fromJsonArray(json));
                adapter.notifyItemRangeInserted(0, json.length());
                scrollToTop();
                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    public void getMyUserJson() {
        client.getMyUserInfo(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                getMyUserInfo(json);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    public void getMyUserInfo(JSONObject json) {
        myUserAccount = User.fromJSON(json);
    }

    // bring up the dialogfragment for composing a new tweet
    private void showComposeDialog() {
        FragmentManager fm = getSupportFragmentManager();
        String myProfileImageUrl = myUserAccount.getProfileImageUrl();
        Log.d("PROFILE", myProfileImageUrl);

        ComposeFragment composeFragment = ComposeFragment.newInstance(myProfileImageUrl);
        composeFragment.show(fm, "fragment_compose");
    }

    private void showTweetDetailDialog(int position) {
        FragmentManager fm = getSupportFragmentManager();
        String myProfileImageUrl = myUserAccount.getProfileImageUrl();
        Log.d("PROFILE", myProfileImageUrl);

        TweetDetailFragment tweetDetailFragment = TweetDetailFragment.newInstance(myProfileImageUrl, tweets.get(position));
        tweetDetailFragment.show(fm, "fragment_tweet_detail");
    }

    // send an API request to get the timeline JSON
    // fill the listview by creating the tweet objects from the JSON
    private void populateTimeline() {
        final int previousTweetsLength = tweets.size();
        long max_id = 0;
        long since_id = 1;
        if (previousTweetsLength > 0) {
            max_id = tweets.get(previousTweetsLength - 1).getUid() + 1;
        }
        client.getHomeTimeline(since_id, max_id, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                Log.d("DEBUG", json.toString());
                tweets.addAll(Tweet.fromJsonArray(json));
                adapter.notifyItemRangeInserted(previousTweetsLength, json.length());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    public void scrollToTop() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(TimelineActivity.this);
        linearLayoutManager = (LinearLayoutManager) rvTweets.getLayoutManager();
        linearLayoutManager.scrollToPositionWithOffset(0, 0);
    }

    public void onTweetButtonClicked(String myTweetText) {
        client.postTweet(myTweetText, new JsonHttpResponseHandler() {
            // Success

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                // deserialize JSON
                // create models
                // load the model data into the ListView
                Log.d("DEBUG", json.toString());
                Toast toast = Toast.makeText(TimelineActivity.this, "Tweet posted!", Toast.LENGTH_SHORT);
                View view = toast.getView();
                view.setBackgroundColor(0xC02196F3);
                TextView textView = (TextView) view.findViewById(android.R.id.message);
                textView.setTextColor(0xFFFFFF00);
                toast.show();
                Tweet myNewTweet = Tweet.fromJSON(json);
                tweets.add(0, myNewTweet);
                adapter.notifyItemInserted(0);
                scrollToTop();
            }

            // Failure
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }
}
