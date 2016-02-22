package com.tachyonlabs.tweety.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tachyonlabs.tweety.R;
import com.tachyonlabs.tweety.activities.TimelineActivity;

public class ComposeFragment extends android.support.v4.app.DialogFragment {
    private String profileImageUrl;
    private EditText etComposeTweet;

    public ComposeFragment() {
        // Required empty public constructor
    }

    public static ComposeFragment newInstance(String profileImageUrl) {
        // get the user profile image URL to display
        ComposeFragment composeFragment = new ComposeFragment();
        Bundle args = new Bundle();
        args.putString("profileImageUrl", profileImageUrl);
        composeFragment.setArguments(args);
        return composeFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Button btnTweet;
        ImageView ivClose;
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        // Get fields from view
        etComposeTweet = (EditText) view.findViewById(R.id.etComposeTweet);
        ImageView ivMyProfileImage = (ImageView) view.findViewById(R.id.ivMyProfileImage);
        // Fetch arguments from bundle
        // display the logged-in user's profile image
        profileImageUrl = getArguments().getString("profileImageUrl");
        Picasso.with(view.getContext()).load(profileImageUrl).fit().centerCrop().into(ivMyProfileImage);

        // Show soft keyboard automatically and request focus to field
        etComposeTweet.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        // set up listener for posting a tweet
        btnTweet = (Button) view.findViewById(R.id.btnTweet);
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etComposeTweet = (EditText) getDialog().findViewById(R.id.etComposeTweet);
                String myTweet = etComposeTweet.getText().toString();
                TimelineActivity timelineActivity = (TimelineActivity) getActivity();
                timelineActivity.onTweetButtonClicked(myTweet);
                getDialog().dismiss();
            }
        });

        // or you can tap the X in the top-left corner to close the fragment
        ivClose = (ImageView) view.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        // display a counter showing the number of characters left for the tweet
        final TextView tvCharsRemaining = (TextView) view.findViewById(R.id.tvCharsRemaining);
        EditText etComposeTweet = (EditText) view.findViewById(R.id.etComposeTweet);
        etComposeTweet.addTextChangedListener(new TextWatcher() {
            int charsRemaining;
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Fires right as the text is being changed (even supplies the range of text)
                // I'm not using this, but it's required to be implemented anyway
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // Fires right before text is changing
                // I'm not using this, but it's required to be implemented anyway
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Fires right after the text has changed
                // update the charsRemaining TextView with how many of the 140 chars they have left
                charsRemaining = 140 - s.length();
                tvCharsRemaining.setText(String.valueOf(charsRemaining));
                tvCharsRemaining.setTextColor(charsRemaining < 0? Color.RED : 0xFF55ACEE);
            }
        });
    }
}
