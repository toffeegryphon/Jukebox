package edu.illinois.cs465.jukebox;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import edu.illinois.cs465.jukebox.model.PartyInfo;
import edu.illinois.cs465.jukebox.viewmodel.HostCreationViewModel;

public class HostPartyOverviewBeforeActivity extends AppCompatActivity {
    private HostCreationViewModel creationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_overview_before);

        Intent source = getIntent();
        if (source != null) {
            creationViewModel = new ViewModelProvider(this).get(HostCreationViewModel.class);
            creationViewModel.init(source.getStringExtra(PartyInfo.PARTY_CODE));

            if (source.getBooleanExtra("initialCreation", false)) {
                Snackbar.make(findViewById(R.id.hostPartyOverviewBeforeConstraintLayout), "Party created successfully!", Snackbar.LENGTH_SHORT)
                        .setAnchorView(findViewById(R.id.bottomNavigationViewBeforeParty))
                        .show();
            }
        }

        // Setup bottom navigation bar
        BottomNavigationView navView = findViewById(R.id.bottomNavigationViewBeforeParty);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.hostApproveSongsFragment, R.id.hostPartyOverviewBeforeFragment, R.id.hostSettingFragmentBeforeParty)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.fragmentContainerViewBeforeParty);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainerViewBeforeParty);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}