package edu.illinois.cs465.jukebox;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import edu.illinois.cs465.jukebox.viewmodel.HostCreationViewModel;
import edu.illinois.cs465.jukebox.viewmodel.HostPartyOverviewBeforeViewModel;

public class HostPartyOverviewBeforeActivity extends AppCompatActivity {
    public static final String DATA_CONFIG = "config";

    private HostPartyOverviewBeforeViewModel overviewBeforeViewModel;
    private HostCreationViewModel creationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_overview_before);

        Intent source = getIntent();
        if (source != null) {
            overviewBeforeViewModel = new ViewModelProvider(this).get(HostPartyOverviewBeforeViewModel.class);
            overviewBeforeViewModel.init(source.getStringExtra(DATA_CONFIG));

            creationViewModel = new ViewModelProvider(this).get(HostCreationViewModel.class);
            creationViewModel.init(source.getStringExtra(DATA_CONFIG));
        }

        // Setup bottom navigation bar
        BottomNavigationView navView = findViewById(R.id.bottomNavigationViewBeforeParty);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.hostApproveSongsFragment, R.id.hostPartyOverviewBeforeFragment, R.id.hostSettingFragmentBeforeParty)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.fragmentContainerViewBeforeParty);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }
}