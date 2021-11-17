package edu.illinois.cs465.jukebox.model;

import androidx.lifecycle.MutableLiveData;

public class PartyInfo {
    private long date;
    private String username;
    private String theme;
    private String description;
    private String location;
    private String partyCode;
    private int skipThreshold;
    private int skipTimer;
    private int suggestionLimit;
    private boolean areSuggestionsAllowed;

    private boolean hasStarted = false;

    public static String PARTY_CODE = "partyCode";

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getSkipThreshold() {
        return skipThreshold;
    }

    public void setSkipThreshold(int skipThreshold) {
        this.skipThreshold = skipThreshold;
    }

    public int getSkipTimer() {
        return skipTimer;
    }

    public void setSkipTimer(int skipTimer) {
        this.skipTimer = skipTimer;
    }

    public int getSuggestionLimit() {
        return suggestionLimit;
    }

    public void setSuggestionLimit(int suggestionLimit) {
        this.suggestionLimit = suggestionLimit;
    }

    public boolean getAreSuggestionsAllowed() {
        return areSuggestionsAllowed;
    }

    public void setAreSuggestionsAllowed(boolean areSuggestionsAllowed) {
        this.areSuggestionsAllowed = areSuggestionsAllowed;
    }

    public String getPartyCode() {
        return partyCode;
    }

    public void setPartyCode(String partyCode) {
        this.partyCode = partyCode;
    }

    public boolean isHasStarted() {
        return hasStarted;
    }

    public void setHasStarted(boolean hasStarted) {
        this.hasStarted = hasStarted;
    }
}
