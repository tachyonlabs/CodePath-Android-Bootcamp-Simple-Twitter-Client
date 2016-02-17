package com.tachyonlabs.tweety.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.tachyonlabs.tweety.R;
import com.tachyonlabs.tweety.adapters.TweetsAdapter;
import com.tachyonlabs.tweety.models.Tweet;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        rvTweets = (RecyclerView) findViewById(R.id.rvTweets);
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(tweets);
        rvTweets.setAdapter(adapter);
        // Set layout manager to position the items
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        client = TwitterApplication.getRestClient(); // singleton client
        populateTimeline();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    // send an API request to get the timeline JSON
    // fill the listview by creating the tweet objects from the JSON
    private void populateTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            // Success

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                // deserialize JSON
                // create models
                // load the model data into the ListView
                Log.d("DEBUG", json.toString());
                int previousTweetsLength = tweets.size();
                tweets.addAll(Tweet.fromJsonArray(json));
                adapter.notifyItemRangeInserted(previousTweetsLength, json.length());
            }

            // Failure
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }
}
