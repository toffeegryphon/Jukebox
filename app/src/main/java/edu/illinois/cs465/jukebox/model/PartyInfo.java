package edu.illinois.cs465.jukebox.model;

import androidx.lifecycle.MutableLiveData;

public class PartyInfo {
    private long date;
    private String username;
    private String theme;
    private String description;
    private String location;
    private String partyCode;
    private Integer skipThreshold;
    private Integer skipTimer;
    private Integer suggestionLimit;
    private Boolean areSuggestionsAllowed;

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

    public Integer getSkipThreshold() {
        return skipThreshold;
    }

    public void setSkipThreshold(Integer skipThreshold) {
        this.skipThreshold = skipThreshold;
    }

    public Integer getSkipTimer() {
        return skipTimer;
    }

    public void setSkipTimer(Integer skipTimer) {
        this.skipTimer = skipTimer;
    }

    public Integer getSuggestionLimit() {
        return suggestionLimit;
    }

    public void setSuggestionLimit(Integer suggestionLimit) {
        this.suggestionLimit = suggestionLimit;
    }

    public Boolean getAreSuggestionsAllowed() {
        return areSuggestionsAllowed;
    }

    public void setAreSuggestionsAllowed(Boolean areSuggestionsAllowed) {
        this.areSuggestionsAllowed = areSuggestionsAllowed;
    }

    public String getPartyCode() {
        return partyCode;
    }

    public void setPartyCode(String partyCode) {
        this.partyCode = partyCode;
    }
}
