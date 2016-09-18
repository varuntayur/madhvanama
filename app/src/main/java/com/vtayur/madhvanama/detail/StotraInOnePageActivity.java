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
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vtayur.madhvanama.R;
import com.vtayur.madhvanama.data.BundleArgs;
import com.vtayur.madhvanama.data.DataProvider;
import com.vtayur.madhvanama.data.model.Shloka;
import com.vtayur.madhvanama.data.Language;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class StotraInOnePageActivity extends FragmentActivity {

    private static String TAG = "StotraInOnePageActivity";

    private List<Integer> mediaResources;
    private Iterator<Integer> mediaResIterator;
    private MediaPlayer mediaPlayer;
    private AtomicInteger playCounter = new AtomicInteger();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_stotra_one_page);

        Log.d(TAG, "-> Starting StotraInOnePageActivity <-");

        Typeface typeface = getTypeface();

        final LinearLayout rootLayout = (LinearLayout) findViewById(R.id.rootLayout);

        Integer menuPosition = getIntent().getIntExtra(BundleArgs.PAGE_NUMBER, 0);
        List<Shloka> engShlokas = (List<Shloka>) getIntent().getSerializableExtra(BundleArgs.ENG_SHLOKA_LIST);
        List<Shloka> localLangShlokas = (List<Shloka>) getIntent().getSerializableExtra(BundleArgs.LOCAL_LANG_SHLOKA_LIST);
        String sectionName = getIntent().getStringExtra(BundleArgs.SECTION_NAME);

        rootLayout.setBackgroundResource(DataProvider.getBackgroundColor(menuPosition));

        TextView tvTitle = (TextView) findViewById(R.id.sectiontitle);
        tvTitle.setText(sectionName);

        Log.d(TAG, "StotraInOnePageActivity needs to render " + localLangShlokas.size() + " shlokas");
        Log.d(TAG, "StotraInOnePageActivity needs to render english " + engShlokas.size() + " shlokas");

        List<Pair<Shloka, Shloka>> lstPairShlokas = getListPairedShlokas(engShlokas, localLangShlokas);

        mediaResources = getAllMediaResources(sectionName, localLangShlokas.size());
        mediaResIterator = mediaResources.iterator();

        for (Pair<Shloka, Shloka> shlokaPair : lstPairShlokas) {
            LinearLayout ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.VERTICAL);

            if(shlokaPair.second.getText() == null){
                WebView webView = new WebView(this);
                ll.addView(webView);
                webView.setBackgroundColor(Color.TRANSPARENT);
                webView.loadData(shlokaPair.second.getFormattedExplanation(),"text/html","utf-8");
            }else{
                TextView localLang = new TextView(this);
                localLang.setTypeface(typeface);
                ll.addView(localLang);
                localLang.setText(shlokaPair.second.getText());
            }

            if(shlokaPair.first.getText().isEmpty()){
                WebView webView = new WebView(this);
                ll.addView(webView);
                webView.setBackgroundColor(Color.TRANSPARENT);
                webView.loadData(shlokaPair.first.getFormattedExplanation(),"text/html","utf-8");
            }else{
                TextView engLang = new TextView(this);
                ll.addView(engLang);
                engLang.setText(shlokaPair.first.getText());
            }


            ll.setPadding(0, 5, 0, 50);
            rootLayout.addView(ll);
        }

        final Activity curActivity = this;
        ImageButton pauseButton = (ImageButton) findViewById(R.id.imageButtonPause);

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "Stopping the stream for media playback");

                Toast.makeText(curActivity, "Pausing sound",
                        Toast.LENGTH_SHORT).show();
                try {
                    mediaPlayer.pause();
                } catch (IllegalStateException ex) {
                    Log.e(TAG, "Exception while trying to stop mediaplayer status.");
                }
                ImageButton playButton = (ImageButton) curActivity.findViewById(R.id.imageButtonPlay);
                playButton.setClickable(true);
            }
        });

        ImageButton playButton = (ImageButton) findViewById(R.id.imageButtonPlay);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaResIterator = mediaResources.iterator();
                playCounter.set(getShlokaRepeatCount());
                playMediaTrack(curActivity);
                v.setClickable(false);

                Toast.makeText(curActivity, "Starting media playback",
                        Toast.LENGTH_SHORT).show();
            }
        });

        Log.d(TAG, "* StotraInOnePageActivity created *");
    }

    private void playMediaTrack(final Activity curActivity) {
        if (!mediaResIterator.hasNext()) {
            Log.d(TAG, "Completed all media resource playback");
            ImageButton playButton = (ImageButton) curActivity.findViewById(R.id.imageButtonPlay);
            playButton.setClickable(true);
            if(mediaPlayer!=null) {
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer=null;
            }

            Log.d(TAG, "playMediaTrack, how many more to go? " + playCounter.get());
            if(playCounter.decrementAndGet() > 0) {
                Log.d(TAG, "playMediaTrack, how many more to go? " + playCounter.get());
                mediaResIterator = mediaResources.iterator();
                playMediaTrack(curActivity);
                return;
            }
            return;
        }

        final Integer nextResource = mediaResIterator.next();
        mediaPlayer = MediaPlayer.create(curActivity, nextResource);
        mediaPlayer.setWakeMode(curActivity.getBaseContext(), PowerManager.SCREEN_DIM_WAKE_LOCK);
        mediaPlayer.start();
        Log.d(TAG, "Starting new stream for media playback " + nextResource );

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d(TAG, "Release before next media stream is played");
                if(mediaPlayer!=null) {
                    mediaPlayer.reset();
                    mediaPlayer.release();
                    mediaPlayer=null;
                }
                Log.d(TAG, "Requesting for continuing next media stream");
                playMediaTrack(curActivity);
            }
        });

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.d(TAG, "Error encountered streams media playback");
                ImageButton playButton = (ImageButton) curActivity.findViewById(R.id.imageButtonPlay);
                playButton.setClickable(true);
                if(mediaPlayer!=null) {
                    mediaPlayer.reset();
                    mediaPlayer.release();
                    mediaPlayer=null;
                }
                return false;
            }
        });
    }


    private List<Integer> getAllMediaResources(String sectionName, int numOfResources) {
        List<Integer> lstRes = new ArrayList<Integer>();
        for (int i = 0; i <= numOfResources; i++) {
            String resourceName = sectionName.toLowerCase().concat(String.valueOf(i)).replaceAll(" ","");
            int resNameId = getResources().getIdentifier(resourceName, "raw", getPackageName());
            if (resNameId > 0)
                lstRes.add(resNameId);
            Log.d(TAG, "ID fetched for packageName " + getPackageName() + " - " + resourceName + " -> " + resNameId);
        }
        return lstRes;
    }

    private List<Pair<Shloka, Shloka>> getListPairedShlokas(List<Shloka> engShlokas, List<Shloka> localLangShlokas) {
        List<Pair<Shloka, Shloka>> lstPairShlokas = new ArrayList<Pair<Shloka, Shloka>>();

        Iterator<Shloka> iterLocalLang = localLangShlokas.iterator();
        for (Shloka shloka : engShlokas) {
            if (iterLocalLang.hasNext()) {
                Pair<Shloka, Shloka> pair = new Pair<Shloka, Shloka>(shloka, iterLocalLang.next());
                lstPairShlokas.add(pair);
            } else {
                Pair<Shloka, Shloka> pair = new Pair<Shloka, Shloka>(shloka, new Shloka());
                lstPairShlokas.add(pair);
            }
        }

        if (engShlokas.size() < localLangShlokas.size()) {
            Log.w(TAG, "getListPairedShlokas found a mismatch in eng vs. local lang.. " + engShlokas.size() + " vs. " + localLangShlokas.size() + " shlokas");
            while (iterLocalLang.hasNext()) {
                Pair<Shloka, Shloka> pair = new Pair<Shloka, Shloka>(new Shloka(), iterLocalLang.next());
                lstPairShlokas.add(pair);
            }

        }

        return lstPairShlokas;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();

        handleMediaStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        handleMediaStop();
    }

    private void handleMediaStop() {
        if (mediaPlayer != null) {
            Log.d(TAG, "************ Attempting to stop media that was initiated with this activity *********");
            mediaPlayer.release();
            ImageButton playButton = (ImageButton) this.findViewById(R.id.imageButtonPlay);
            playButton.setClickable(true);
            Log.d(TAG, "************ Release media player resource was successful *********");
        }
    }

    private Typeface getTypeface() {
        String langPrefs = getSelectedLanguage();

        Log.d(TAG, "Trying to launch activity in selected language :" + langPrefs);

        Language lang = Language.getLanguageEnum(langPrefs);

        Log.d(TAG, "Will get assets for activity in language :" + lang.toString());

        return lang.getTypeface(getAssets());
    }

    private String getSelectedLanguage() {
        SharedPreferences sharedPreferences = getSharedPreferences(DataProvider.PREFS_NAME, 0);
        return sharedPreferences.getString(DataProvider.SHLOKA_DISP_LANGUAGE, Language.san.toString());
    }

    public int getShlokaRepeatCount() {
        SharedPreferences sharedPreferences = getSharedPreferences(DataProvider.PREFS_NAME, 0);
        String repeatShloka = sharedPreferences.getString(DataProvider.REPEAT_SHLOKA, DataProvider.REPEAT_SHLOKA_DEFAULT);
        return Integer.valueOf(repeatShloka);
    }
}
