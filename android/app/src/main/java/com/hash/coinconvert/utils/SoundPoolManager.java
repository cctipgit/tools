package com.hash.coinconvert.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.ToneGenerator;
import android.os.Build;
import android.util.Log;

import com.hash.coinconvert.R;

/**
 * AudioFeedbackManager class
 */
public class SoundPoolManager {

    private static final String TAG = "AudioFeedbackManager";
    //private static ToneGenerator mToneGenerator;
    //private static final int TONE_LENGTH_MS = 150;
    private static SoundPool mSoundPool;
    private static int mSoundId;

    public static void init(final Context context) {
        //mToneGenerator = new ToneGenerator(AudioManager.NUM_STREAMS, 62);
        //mSoundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        mSoundPool = new SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(new AudioAttributes.Builder().setLegacyStreamType(AudioManager.STREAM_MUSIC).build())
                .build();
        mSoundId = mSoundPool.load(context, R.raw.effect_tick, 1);
    }


    public static void performStandardAudioFeedback(int code) {
        //int toneType = ToneGenerator.TONE_DTMF_1;
        /*if(code == 1) {
            toneType = ToneGenerator.TONE_DTMF_1;
        } else if(code == 2) {
            toneType = ToneGenerator.TONE_DTMF_2;
        } else if(code == 3) {
            toneType = ToneGenerator.TONE_DTMF_3;
        } else if(code == 4) {
            toneType = ToneGenerator.TONE_DTMF_4;
        } else if(code == 5) {
            toneType = ToneGenerator.TONE_DTMF_5;
        } else if(code == 6) {
            toneType = ToneGenerator.TONE_DTMF_6;
        } else if(code == 7) {
            toneType = ToneGenerator.TONE_DTMF_7;
        } else if(code == 8) {
            toneType = ToneGenerator.TONE_DTMF_8;
        } else if(code == 9) {
            toneType = ToneGenerator.TONE_DTMF_9;
        } else if(code == 0) {
            toneType = ToneGenerator.TONE_DTMF_0;
        }*/
        /*if (toneType != -1) {
            mToneGenerator.startTone(toneType, TONE_LENGTH_MS);
        }*/
        mSoundPool.play(mSoundId, 1.0f, 1.0f, 0, 0, 1.0f);
    }
}