package edu.illinois.cs465.jukebox;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Objects;

import edu.illinois.cs465.jukebox.model.PartyInfo;
import edu.illinois.cs465.jukebox.viewmodel.HostCreationViewModel;
import edu.illinois.cs465.jukebox.viewmodel.MusicService;

public class HostPartyOverviewDuringActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private DocumentReference partyReference;
    private HostCreationViewModel creationViewModel;

    private MusicService musicService;
    // protected MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_overview_during);

        db = FirebaseFirestore.getInstance(); // TODO This should be elsewhere

        if (getIntent().hasExtra(PartyInfo.PARTY_CODE)) {
            String partyCode = getIntent().getStringExtra(PartyInfo.PARTY_CODE);

            creationViewModel = new ViewModelProvider(this).get(HostCreationViewModel.class);
            creationViewModel.init(partyCode);

            partyReference = db.collection("partyInfo").document(partyCode);
            partyReference.update("hasStarted", true)
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

        mediaPlayer = getNed
//        if (mediaPlayer == null) {
//            mediaPlayer = new MediaPlayer();
//            prepareMediaPlayer();
//        }

        // navController.getCurrentDestination()

        // TODO: mediaPlayer.OnCompletionListener for queue
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mediaPlayer.pause();

        startActivity(intent);
    }

    private void prepareMediaPlayer() {
        try {
            // popHostQueueListItem();
            mediaPlayer.setDataSource("https://www.dropbox.com/s/5f1l6seztxvq373/Cartoon%20-%20On%20_%20On%20%28feat.%20Daniel%20Levi%29%20_NCS%20Release_.mp3?dl=1");
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception ex) {
            Toast.makeText(this, "ERROR: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}