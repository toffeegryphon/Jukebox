package edu.illinois.cs465.jukebox;

import android.os.CountDownTimer;
import android.widget.ProgressBar;

import androidx.lifecycle.MutableLiveData;

public class ProgressCountdown extends CountDownTimer {
    ProgressBar bar;
    MutableLiveData<Boolean> isActive;
    long maxMillis;

    public ProgressCountdown(long currentMillis, long intervalMillis, long maxMillis, ProgressBar bar, MutableLiveData<Boolean> isActive) {
        super(currentMillis, intervalMillis);

        this.isActive = isActive;
        this.isActive.setValue(true);

        this.maxMillis = maxMillis;
        this.bar = bar;
        this.bar.setProgress((int) (currentMillis * bar.getMax() / maxMillis));
    }

    @Override
    public void onTick(long millisUntilFinished) {
        bar.setProgress((int) (millisUntilFinished * bar.getMax() / maxMillis));
    }

    @Override
    public void onFinish() {
        bar.setProgress(0);
        isActive.setValue(false);
    }
}
