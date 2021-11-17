package edu.illinois.cs465.jukebox.viewmodel;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Objects;


import edu.illinois.cs465.jukebox.model.PartyInfo;

public class HostCreationViewModel extends ViewModel {

    private final FirebaseFirestore db;
    private final MutableLiveData<PartyInfo> mPartyInfo;

    public HostCreationViewModel() {
        db = FirebaseFirestore.getInstance();
        mPartyInfo = new MutableLiveData<>();
        PartyInfo partyInfo = new PartyInfo();
        partyInfo.setDate(Calendar.getInstance().getTimeInMillis());
        partyInfo.setPartyCode("AAAA"); // TODO generate a random code
        mPartyInfo.setValue(partyInfo);
    }

    public void init(String partyCode) {
        Log.d("partyCode", partyCode);
        db.collection("partyInfo").document(partyCode).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        assert document != null;
                        PartyInfo partyInfo = document.toObject(PartyInfo.class);
                        mPartyInfo.setValue(partyInfo);
                    } else {
                        Log.d("INFO", "get failed with ", task.getException());
                    }
                });
    }

    public void setDate(Long date) {
        PartyInfo partyInfo = mPartyInfo.getValue();
        partyInfo.setDate(date);
        mPartyInfo.setValue(partyInfo);
    }

    public void setGeneralPartyInfo(String username, String theme, String description, String location) {
        PartyInfo partyInfo = mPartyInfo.getValue();
        partyInfo.setUsername(username);
        partyInfo.setTheme(theme);
        partyInfo.setDescription(description);
        partyInfo.setLocation(location);
        mPartyInfo.setValue(partyInfo);
    }

    public void setHostSettingInfo(int skipThreshold, int skipTimer, int suggestionLimit, boolean areSuggestionsAllowed) {
        PartyInfo partyInfo = mPartyInfo.getValue();
        partyInfo.setSkipThreshold(skipThreshold);
        partyInfo.setSkipTimer(skipTimer);
        partyInfo.setSuggestionLimit(suggestionLimit);
        partyInfo.setAreSuggestionsAllowed(areSuggestionsAllowed);
        mPartyInfo.setValue(partyInfo);
    }

    public LiveData<PartyInfo> getPartyInfo() {
        return this.mPartyInfo;
    }

    public void saveParty(SharedPreferences local) {
        PartyInfo partyInfo = mPartyInfo.getValue();
        String partyCode = Objects.requireNonNull(partyInfo).getPartyCode();
        db.collection("partyInfo")
                .document(partyCode)
                .set(partyInfo)
                .addOnSuccessListener(unused -> {
                    Log.d("INFO", "DocumentSnapshot added with ID");
                    local.edit()
                            .putString(PartyInfo.PARTY_CODE, partyCode)
                            .putBoolean(PartyInfo.IS_CREATED, true)
                            .apply();
                })
                .addOnFailureListener(e -> Log.w("INFO", "Error adding document", e));
    }
}
