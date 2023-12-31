package com.mining.superbull.util;

import android.content.Context;
import android.media.MediaPlayer;

public class MusicServer {
    private static MediaPlayer mp = null;

    public static void play(Context context, int resource) {
        stop();
        mp = MediaPlayer.create(context, resource);
        mp.setLooping(true);
        mp.start();
    }

    public static void stop() {
        // TODO Auto-generated method stub
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }

}