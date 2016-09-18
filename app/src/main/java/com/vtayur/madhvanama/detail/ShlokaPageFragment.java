/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vtayur.madhvanama.detail;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.vtayur.madhvanama.R;
import com.vtayur.madhvanama.detail.media.ShlokaMediaPlayer;
import com.vtayur.madhvanama.data.BundleArgs;
import com.vtayur.madhvanama.data.DataProvider;
import com.vtayur.madhvanama.data.model.Shloka;
import com.vtayur.madhvanama.data.Language;

import java.util.Collections;
import java.util.List;

public class ShlokaPageFragment extends Fragment {
    private static String TAG = "ShlokaPageFragment";

    private int resNameId;

    public ShlokaPageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retain this fragment across configuration changes.
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return intializeView(inflater, container);
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.d(TAG, "************ Attempting to stop media that was initiated with this fragment *********");
        ShlokaMediaPlayer.release();
        Log.d(TAG, "************ Pause media was successful *********");

    }

    public String getSectionName() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            if (bundle.containsKey(BundleArgs.SECTION_NAME))
                return bundle.getString(BundleArgs.SECTION_NAME);
        }
        return "";
    }

    public List<Shloka> getEngShlokas() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            if (bundle.containsKey(BundleArgs.ENG_SHLOKA_LIST))
                return (List<Shloka>) bundle.getSerializable(BundleArgs.ENG_SHLOKA_LIST);
        }
        return Collections.emptyList();
    }

    public List<Shloka> getLocalLangShlokas() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            if (bundle.containsKey(BundleArgs.LOCAL_LANG_SHLOKA_LIST))
                return (List<Shloka>) bundle.getSerializable(BundleArgs.LOCAL_LANG_SHLOKA_LIST);
        }
        return Collections.emptyList();
    }

    public int getPageNumber() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            if (bundle.containsKey(BundleArgs.PAGE_NUMBER))
                return bundle.getInt(BundleArgs.PAGE_NUMBER);
        }
        return 1;
    }


    private Typeface getTypeface() {
        String langPrefs = getSelectedLanguage();

        Log.d(TAG, "Trying to launch activity in selected language :" + langPrefs);

        Language lang = Language.getLanguageEnum(langPrefs);

        Log.d(TAG, "Will get assets for activity in language :" + lang.toString());

        return lang.getTypeface(this.getActivity().getAssets());
    }

    private String getSelectedLanguage() {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(DataProvider.PREFS_NAME, 0);
        return sharedPreferences.getString(DataProvider.SHLOKA_DISP_LANGUAGE, Language.san.toString());
    }

    private ViewGroup intializeView(LayoutInflater inflater, ViewGroup container) {

        final Activity curActivity = this.getActivity();
        Typeface customTypeface = getTypeface();

        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_shloka_slide_page, container, false);

        String displayPageNumber = String.valueOf(getPageNumber() + 1);

        TextView secTitleViewById = (TextView) rootView.findViewById(R.id.sectiontitle);
        secTitleViewById.setText(getSectionName() + " ( " + displayPageNumber + " / " + getEngShlokas().size() + " )");

        final Shloka shloka = getEngShlokas().get(getPageNumber());
        final Shloka localLangShloka = getLocalLangShlokas().get(getPageNumber());

        TextView shlokaText = (TextView) rootView.findViewById(R.id.shlokalocallangtext);
        shlokaText.setTypeface(customTypeface);
        shlokaText.setText(localLangShloka.getText());

        TextView shlokaenText = (TextView) rootView.findViewById(R.id.shlokaentext);
        shlokaenText.setText(shloka.getText());

        WebView shlokaExplanation = (WebView) rootView.findViewById(R.id.shlokaexplanation);
        shlokaExplanation.setBackgroundColor(Color.TRANSPARENT);
        shlokaExplanation.loadData(shloka.getFormattedExplanation(), "text/html", null);

        resNameId = getResourceName(curActivity);

        ImageButton pauseButton = (ImageButton) rootView.findViewById(R.id.imageButtonPause);
        setVisibility(resNameId, pauseButton);

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(curActivity, "Stopping media playback",
                        Toast.LENGTH_SHORT).show();

                ShlokaMediaPlayer.pause();
            }
        });

        ImageButton playButton = (ImageButton) rootView.findViewById(R.id.imageButtonPlay);
        setVisibility(resNameId, playButton);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (resNameId == 0) return;

                ShlokaMediaPlayer.setLoopCounter(getShlokaRepeatCount());

                String playStatus = ShlokaMediaPlayer.play(getActivity(), resNameId);

                if (!playStatus.isEmpty()) {

                    Toast.makeText(curActivity, playStatus,
                            Toast.LENGTH_SHORT).show();

                    return;
                }

                Toast.makeText(curActivity, "Playing media",
                        Toast.LENGTH_SHORT).show();
            }
        });
        return rootView;
    }

    private int getResourceName(Activity curActivity) {
        String displayPageNumber = String.valueOf(getPageNumber());
        String resourceName = getSectionName().toLowerCase().concat(displayPageNumber).replaceAll(" ", "");
        int resNameId = curActivity.getResources().getIdentifier(resourceName, "raw", curActivity.getPackageName());
        Log.d(TAG, "ID fetched for packageName " + curActivity.getPackageName() + " - " + resourceName + " -> " + resNameId);
        return resNameId;
    }

    private void setVisibility(int resNameId, ImageButton pauseButton) {
        if (resNameId == 0)
            pauseButton.setVisibility(View.INVISIBLE);
        else
            pauseButton.setVisibility(View.VISIBLE);
    }

    public int getShlokaRepeatCount() {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(DataProvider.PREFS_NAME, 0);
        String repeatShloka = sharedPreferences.getString(DataProvider.REPEAT_SHLOKA, DataProvider.REPEAT_SHLOKA_DEFAULT);
        return Integer.valueOf(repeatShloka);
    }
}
