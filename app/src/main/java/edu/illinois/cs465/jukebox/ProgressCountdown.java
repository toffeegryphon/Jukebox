package edu.illinois.cs465.jukebox;

import android.os.CountDownTimer;
import android.widget.ProgressBar;
import android.widget.ViewSwitcher;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

public class ProgressCountdown extends CountDownTimer {
    public static String REMAINING_MILLIS = "remainingMillis";

    ProgressBar bar;
    MutableLiveData<Boolean> isActive;
    long maxMillis, remainingMillis;
    private ArrayList<ProgressCountdownListener> mListeners;

    public ProgressCountdown(long currentMillis, long intervalMillis, long maxMillis, ProgressBar bar, MutableLiveData<Boolean> isActive) {
        super(currentMillis, intervalMillis);

        this.isActive = isActive;
        this.isActive.setValue(true);

        this.maxMillis = maxMillis;
        this.bar = bar;
        this.bar.setMax((int) maxMillis);
        this.bar.setProgress((int) currentMillis);

        this.mListeners = new ArrayList<>();
    }

    @Override
    public void onTick(long millisUntilFinished) {
        bar.setProgress((int) millisUntilFinished);
        remainingMillis = millisUntilFinished;
    }

    @Override
    public void onFinish() {
        bar.setProgress(0);
        isActive.setValue(false);

        if (!mListeners.isEmpty()) {
            for (ProgressCountdownListener l : mListeners) {
                l.onCountdownFinish();
                unregisterListener(l);
            }
        }
    }

    public long getRemainingMillis() {
        return remainingMillis;
    }

    public interface ProgressCountdownListener {
        void onCountdownFinish();
    }

    public void registerListener(ProgressCountdownListener listener) {
        if (!mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    public boolean unregisterListener(ProgressCountdownListener listener) {
        return mListeners.remove(listener);
    }
}
