package com.tachyonlabs.tweety.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tachyonlabs.tweety.R;
import com.tachyonlabs.tweety.activities.TimelineActivity;

public class ComposeFragment extends android.support.v4.app.DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private EditText etComposeTweet;

    public ComposeFragment() {
        // Required empty public constructor
    }

    public static ComposeFragment newInstance(String title) {
        ComposeFragment frag = new ComposeFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
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
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        etComposeTweet = (EditText) view.findViewById(R.id.etComposeTweet);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);

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
