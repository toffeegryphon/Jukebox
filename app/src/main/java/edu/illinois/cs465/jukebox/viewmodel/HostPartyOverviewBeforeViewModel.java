package edu.illinois.cs465.jukebox.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;

import edu.illinois.cs465.jukebox.model.PartyInfo;

public class HostPartyOverviewBeforeViewModel extends ViewModel {
    private final FirebaseFirestore db;
    private final MutableLiveData<PartyInfo> mPartyInfo;
    public HostPartyOverviewBeforeViewModel() {
        db = FirebaseFirestore.getInstance();
        mPartyInfo = new MutableLiveData<>();
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

    public LiveData<PartyInfo> getPartyInfo() {
        return this.mPartyInfo;
    }
}
