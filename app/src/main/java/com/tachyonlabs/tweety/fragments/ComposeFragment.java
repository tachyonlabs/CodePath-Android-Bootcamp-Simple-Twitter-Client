package com.tachyonlabs.tweety.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "title";
    private static final String ARG_PARAM2 = "profileImageUrl";
    // TODO: Rename and change types of parameters
    private String title;
    private String profileImageUrl;

    private EditText etComposeTweet;

    public ComposeFragment() {
        // Required empty public constructor
    }

    public static ComposeFragment newInstance(String title, String profileImageUrl) {
        Log.d("DEBUG", profileImageUrl);
        ComposeFragment frag = new ComposeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, title);
        args.putString(ARG_PARAM2, profileImageUrl);
        frag.setArguments(args);
        return frag;
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
        // Get field from view
        etComposeTweet = (EditText) view.findViewById(R.id.etComposeTweet);
        // Fetch arguments from bundle and set title
        title = getArguments().getString(ARG_PARAM1);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        profileImageUrl = getArguments().getString(ARG_PARAM2);
        etComposeTweet.setHint(title);
        ImageView ivProfileImage = (ImageView) view.findViewById(R.id.ivProfileImage);
        Picasso.with(view.getContext()).load(profileImageUrl).fit().centerCrop().into(ivProfileImage);

        // Show soft keyboard automatically and request focus to field
        etComposeTweet.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

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

        ivClose = (ImageView) view.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        final TextView tvCharsRemaining = (TextView) view.findViewById(R.id.tvCharsRemaining);
        EditText etComposeTweet = (EditText) view.findViewById(R.id.etComposeTweet);
        etComposeTweet.addTextChangedListener(new TextWatcher() {
            int charsRemaining;
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Fires right as the text is being changed (even supplies the range of text)
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // Fires right before text is changing
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Fires right after the text has changed
                charsRemaining = 140 - s.length();
                tvCharsRemaining.setText(String.valueOf(charsRemaining));
                tvCharsRemaining.setTextColor(charsRemaining < 0? Color.RED : R.color.twitter_blue);
            }
        });
    }
}
