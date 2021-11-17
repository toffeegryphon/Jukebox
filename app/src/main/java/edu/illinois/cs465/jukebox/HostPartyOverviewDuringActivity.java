package edu.illinois.cs465.jukebox;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import edu.illinois.cs465.jukebox.model.PartyInfo;

public class HostPartyOverviewDuringActivity extends AppCompatActivity {
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_overview_during);

        db = FirebaseFirestore.getInstance(); // TODO This should be elsewhere

        if (getIntent().hasExtra(PartyInfo.PARTY_CODE)) {
            String partyCode = getIntent().getStringExtra(PartyInfo.PARTY_CODE);
            db.collection("partyInfo").document(partyCode)
                    .update("hasStarted", true)
                    .addOnSuccessListener(unused -> Log.d("TESTING", "STARTED!"))
                    .addOnFailureListener(e -> Log.d("TESTING", e.getMessage()));
        }

        // Setup bottom navigation bar
        BottomNavigationView navView = findViewById(R.id.bottomNavigationViewDuringParty);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.hostSongQueueFragment, R.id.hostPartyOverviewDuringFragment, R.id.hostSettingFragmentDuringParty)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.fragmentContainerViewDuringParty);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }
}