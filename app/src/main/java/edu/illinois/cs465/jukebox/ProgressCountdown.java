package edu.illinois.cs465.jukebox;

import android.os.CountDownTimer;
import android.widget.ProgressBar;

import androidx.lifecycle.MutableLiveData;

public class ProgressCountdown extends CountDownTimer {
    public static String REMAINING_MILLIS = "remainingMillis";

    ProgressBar bar;
    MutableLiveData<Boolean> isActive;
    long maxMillis, remainingMillis;

    public ProgressCountdown(long currentMillis, long intervalMillis, long maxMillis, ProgressBar bar, MutableLiveData<Boolean> isActive) {
        super(currentMillis, intervalMillis);

        this.isActive = isActive;
        this.isActive.setValue(true);

        this.maxMillis = maxMillis;
        this.bar = bar;
        this.bar.setMax((int) maxMillis);
        this.bar.setProgress((int) currentMillis);
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
    }

    public long getRemainingMillis() {
        return remainingMillis;
    }
}
