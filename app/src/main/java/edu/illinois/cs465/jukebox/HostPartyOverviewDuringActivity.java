package edu.illinois.cs465.jukebox;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import edu.illinois.cs465.jukebox.model.PartyInfo;
import edu.illinois.cs465.jukebox.viewmodel.HostCreationViewModel;

public class HostPartyOverviewDuringActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private HostCreationViewModel creationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_overview_during);

        db = FirebaseFirestore.getInstance(); // TODO This should be elsewhere

        if (getIntent().hasExtra(PartyInfo.PARTY_CODE)) {
            String partyCode = getIntent().getStringExtra(PartyInfo.PARTY_CODE);

            creationViewModel = new ViewModelProvider(this).get(HostCreationViewModel.class);
            creationViewModel.init(partyCode);

            db.collection("partyInfo").document(partyCode)
                    .update("hasStarted", true)
                    .addOnSuccessListener(unused -> Log.d("TESTING", "STARTED!"))
                    .addOnFailureListener(e -> Log.d("TESTING", e.getMessage()));

            getSharedPreferences("host", Context.MODE_PRIVATE).edit()
                    .putInt(PartyInfo.HOST_MODE, PartyInfo.HOST_STARTED)
                    .apply();
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}