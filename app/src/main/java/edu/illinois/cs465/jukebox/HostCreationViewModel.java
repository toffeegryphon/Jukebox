package edu.illinois.cs465.jukebox;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HostCreationViewModel extends ViewModel {
    public static final String USERNAME = "username";
    public static final String THEME = "theme";
    public static final String SKIP_THRESHOLD = "skipThreshold";
    public static final String SKIP_TIMER = "skipTimer";
    public static final String SUGGESTION_LIMIT = "suggestionLimit";
    public static final String ARE_SUGGESTIONS_ALLOWED = "areSuggestionsAllowed";

    private static Gson gson;

    private final MutableLiveData<Calendar> date;

    private final Map<String, MutableLiveData<String>> strings;
    private final Map<String, MutableLiveData<Integer>> integers;
    private final Map<String, MutableLiveData<Boolean>> booleans;

    public HostCreationViewModel() {
        date = new MutableLiveData<>();

        strings = new HashMap<>();
        integers = new HashMap<>();
        booleans = new HashMap<>();
    }

    private <T> MutableLiveData<T> initializeData(T value) {
        MutableLiveData<T> data = new MutableLiveData<>();
        data.setValue(value);

        return data;
    }

    public LiveData<String> getString(String propertyName, String defaultValue) {
        if (!strings.containsKey(propertyName)) strings.put(propertyName, initializeData(defaultValue));
        return strings.get(propertyName);
    }

    public void setString(String propertyName, String value) {
        if (!strings.containsKey(propertyName)) {
            strings.put(propertyName, initializeData(value));
        } else {
            Objects.requireNonNull(strings.get(propertyName)).setValue(value);
        }
    }

    public LiveData<Integer> getInteger(String propertyName, Integer defaultValue) {
        if (!integers.containsKey(propertyName)) integers.put(propertyName, initializeData(defaultValue));
        return integers.get(propertyName);
    }

    public void setInteger(String propertyName, Integer value) {
        if (!integers.containsKey(propertyName)) {
            integers.put(propertyName, initializeData(value));
        } else {
            Objects.requireNonNull(integers.get(propertyName)).setValue(value);
        }
    }

    public LiveData<Boolean> getBoolean(String propertyName, Boolean defaultValue) {
        if (!booleans.containsKey(propertyName)) booleans.put(propertyName, initializeData(defaultValue));
        return booleans.get(propertyName);
    }

    public void setBoolean(String propertyName, Boolean value) {
        if (!booleans.containsKey(propertyName)) {
            booleans.put(propertyName, initializeData(value));
        } else {
            Objects.requireNonNull(booleans.get(propertyName)).setValue(value);
        }
    }

    public LiveData<String> getUserName() {
        return getString(USERNAME, "");
    }

    public void setUserName(String userName) {
        setString(USERNAME, userName);
    }

    public LiveData<String> getTheme() {
        return getString(THEME, "");
    }

    public void setTheme(String theme) {
        setString(THEME, theme);
    }

    public LiveData<Calendar> getDate() { return date; }

    public void setDate(Calendar date) {
        this.date.setValue(date);
    }

    public LiveData<Integer> getSkipThreshold() {
        return getInteger(SKIP_THRESHOLD, 0);
    }

    public void setSkipThreshold(int skipThreshold) {
        setInteger(SKIP_THRESHOLD, skipThreshold);
    }

    public LiveData<Integer> getSkipTimer() {
        return getInteger(SKIP_TIMER, 0);
    }

    public void setSkipTimer(int skipTimer) {
        setInteger(SKIP_TIMER, skipTimer);
    }

    public LiveData<Boolean> getAreSuggestionsAllowed() {
        return getBoolean(ARE_SUGGESTIONS_ALLOWED, true);
    }

    public void setAreSuggestionsAllowed(boolean areSuggestionsAllowed) {
        setBoolean(ARE_SUGGESTIONS_ALLOWED, areSuggestionsAllowed);
    }

    public LiveData<Integer> getSuggestionLimit() {
        return getInteger(SUGGESTION_LIMIT, 0);
    }

    public void setSuggestionLimit(int suggestionLimit) {
        setInteger(SUGGESTION_LIMIT, suggestionLimit);
    }

    public static class LiveStringParser implements JsonSerializer<MutableLiveData<String>>, JsonDeserializer<MutableLiveData<String>> {
        @Override
        public JsonElement serialize(MutableLiveData<String> src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(Objects.requireNonNull(src.getValue()));
        }

        @Override
        public MutableLiveData<String> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            MutableLiveData<String> data = new MutableLiveData<>();
            data.setValue(json.getAsString());
            return data;
        }
    }

    public static class LiveIntegerParser implements JsonSerializer<MutableLiveData<Integer>>, JsonDeserializer<MutableLiveData<Integer>> {
        @Override
        public JsonElement serialize(MutableLiveData<Integer> src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(Objects.requireNonNull(src.getValue()));
        }

        @Override
        public MutableLiveData<Integer> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            MutableLiveData<Integer> data = new MutableLiveData<>();
            data.setValue(json.getAsInt());
            return data;
        }
    }

    public static class LiveBooleanParser implements JsonSerializer<MutableLiveData<Boolean>>, JsonDeserializer<MutableLiveData<Boolean>> {
        @Override
        public JsonElement serialize(MutableLiveData<Boolean> src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(Objects.requireNonNull(src.getValue()));
        }

        @Override
        public MutableLiveData<Boolean> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            MutableLiveData<Boolean> data = new MutableLiveData<>();
            data.setValue(json.getAsBoolean());
            return data;
        }
    }

    public static Gson getGson() {
        if (gson == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            Type LiveString = new TypeToken<MutableLiveData<String>>(){}.getType();
            gsonBuilder.registerTypeAdapter(LiveString, new LiveStringParser());
            Type LiveInteger = new TypeToken<MutableLiveData<Integer>>(){}.getType();
            gsonBuilder.registerTypeAdapter(LiveInteger, new LiveIntegerParser());
            Type LiveBoolean = new TypeToken<MutableLiveData<Boolean>>(){}.getType();
            gsonBuilder.registerTypeAdapter(LiveBoolean, new LiveBooleanParser());

            gson = gsonBuilder.create();
        }

        return gson;
    }

    public String toJson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Type LiveString = new TypeToken<MutableLiveData<String>>(){}.getType();
        gsonBuilder.registerTypeAdapter(LiveString, new LiveStringParser());
        Type LiveInteger = new TypeToken<MutableLiveData<Integer>>(){}.getType();
        gsonBuilder.registerTypeAdapter(LiveInteger, new LiveIntegerParser());
        Type LiveBoolean = new TypeToken<MutableLiveData<Boolean>>(){}.getType();
        gsonBuilder.registerTypeAdapter(LiveBoolean, new LiveBooleanParser());

        Gson gson = gsonBuilder.create();
        return gson.toJson(this);
    }
}
