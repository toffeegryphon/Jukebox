package edu.illinois.cs465.jukebox.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;


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

    public void saveParty() {
        db.collection("partyInfo")
                .add(mPartyInfo.getValue())
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("INFO", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("INFO", "Error adding document", e);
                    }
                });
    }
}
