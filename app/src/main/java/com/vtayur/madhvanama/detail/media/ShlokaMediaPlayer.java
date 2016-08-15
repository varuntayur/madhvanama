package com.vtayur.madhvanama.detail.media;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.util.Log;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by vtayur on 9/30/2014.
 */
public class ShlokaMediaPlayer {

    private static MediaPlayer mediaPlayer;
    private static final String TAG = "ShlokaMediaPlayer";

    private static AtomicInteger playCounter = new AtomicInteger();

    public static void setLoopCounter(int value) {
        playCounter.set(value);
    }

    public static void pause() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.pause();
            } catch (IllegalStateException ex) {
                Log.e(TAG, "Exception while trying to pause mediaplayer");
            }
        }
    }

    public static void release() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    public static String play(Activity activity, int resId) {

        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                return "Media player is already playing another track. Please try again after that completes.";
            }
        } catch (IllegalStateException ex) {
            Log.e(TAG, "Exception while trying to fetch mediaplayer status.");
        }
        mediaPlayer = MediaPlayer.create(activity, resId);
        mediaPlayer.setWakeMode(activity.getBaseContext(), PowerManager.SCREEN_DIM_WAKE_LOCK);
        mediaPlayer.start();

        mediaPlayer.setOnCompletionListener(new PlaybackCompleteListener(activity, resId));

        return "";
    }

    private static class PlaybackCompleteListener implements MediaPlayer.OnCompletionListener {

        private final Activity activity;
        private final int resId;

        public PlaybackCompleteListener(Activity activity, int resId) {
            this.activity = activity;
            this.resId = resId;
        }

        @Override
        public void onCompletion(MediaPlayer mp) {

            Log.d(TAG, "onComplete track, release mediaPlayer");
            if (mediaPlayer != null) {
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            Log.d(TAG, "onComplete track, many more to go ? " + playCounter.get());

            if (playCounter.decrementAndGet() > 0) {
                mediaPlayer = MediaPlayer.create(activity, resId);
                mediaPlayer.setWakeMode(activity.getBaseContext(), PowerManager.SCREEN_DIM_WAKE_LOCK);
                mediaPlayer.start();

                mediaPlayer.setOnCompletionListener(new PlaybackCompleteListener(activity, resId));

                Log.d(TAG, "onComplete restarting playback, how many more to go ? " + playCounter.get());
            }
        }
    }

}
