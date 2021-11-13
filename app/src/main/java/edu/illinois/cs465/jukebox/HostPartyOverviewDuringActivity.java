package edu.illinois.cs465.jukebox;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HostPartyOverviewDuringActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_overview_during);

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