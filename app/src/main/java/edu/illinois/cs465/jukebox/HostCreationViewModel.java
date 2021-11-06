package edu.illinois.cs465.jukebox;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Calendar;

public class HostCreationViewModel extends ViewModel {
    private final MutableLiveData<String> userName;
    private final MutableLiveData<String> theme;
    private final MutableLiveData<Calendar> date;
    private final MutableLiveData<Integer> skipThreshold;
    private final MutableLiveData<Integer> skipTimer;
    private final MutableLiveData<Boolean> areSuggestionsAllowed;
    private final MutableLiveData<Integer> suggestionLimit;

    public HostCreationViewModel() {
        userName = new MutableLiveData<>();
        theme = new MutableLiveData<>();
        date = new MutableLiveData<>();

        skipThreshold = new MutableLiveData<>();
        skipTimer = new MutableLiveData<>();
        areSuggestionsAllowed = new MutableLiveData<>();
        suggestionLimit = new MutableLiveData<>();
    }

    public LiveData<String> getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName.setValue(userName);
    }

    public LiveData<String> getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme.setValue(theme);
    }

    public LiveData<Calendar> getDate() { return date; }

    public void setDate(Calendar date) {
        this.date.setValue(date);
    }

    public MutableLiveData<Integer> getSkipThreshold() {
        return skipThreshold;
    }

    public void setSkipThreshold(int skipThreshold) {
        this.skipThreshold.setValue(skipThreshold);
    }

    public MutableLiveData<Integer> getSkipTimer() {
        return skipTimer;
    }

    public void setSkipTimer(int skipTimer) {
        this.skipTimer.setValue(skipTimer);
    }

    public MutableLiveData<Boolean> getAreSuggestionsAllowed() {
        return areSuggestionsAllowed;
    }

    public void setAreSuggestionsAllowed(boolean areSuggestionsAllowed) {
        this.areSuggestionsAllowed.setValue(areSuggestionsAllowed);
    }

    public MutableLiveData<Integer> getSuggestionLimit() {
        return suggestionLimit;
    }

    public void setSuggestionLimit(int suggestionLimit) {
        this.suggestionLimit.setValue(suggestionLimit);
    }
}
