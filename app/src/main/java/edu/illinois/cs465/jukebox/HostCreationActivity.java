package edu.illinois.cs465.jukebox;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.illinois.cs465.jukebox.model.PartyInfo;
import edu.illinois.cs465.jukebox.viewmodel.HostCreationViewModel;

public class HostCreationActivity extends AppCompatActivity {

    private HostCreationViewModel viewModel;
    private CreationPagerAdapter adapter;
    private ViewPager stepsPager;

    private Button buttonContinue;
    private List<Button> indicators;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_creation);

        viewModel = new ViewModelProvider(this).get(HostCreationViewModel.class);
        adapter = new CreationPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        stepsPager = findViewById(R.id.steps_pager);
        stepsPager.setAdapter(adapter);

        indicators = new ArrayList<>();
        indicators.add(findViewById(R.id.step_1));
        indicators.add(findViewById(R.id.step_2));

        buttonContinue = findViewById(R.id.button_continue);
        initListeners();
    }

    private void initListeners() {
        buttonContinue.setOnClickListener(v -> {
            int position = stepsPager.getCurrentItem();
            adapter.save(position);

            if (position + 1 < adapter.getCount()) {
                stepsPager.setCurrentItem(position + 1);
            } else {
                viewModel.saveParty(getSharedPreferences("host", Context.MODE_PRIVATE));
                String partyCode = Objects.requireNonNull(viewModel.getPartyInfo().getValue()).getPartyCode();
                Intent intent = new Intent(HostCreationActivity.this, HostPartyOverviewBeforeActivity.class);
                intent.putExtra(PartyInfo.PARTY_CODE, partyCode);
                startActivity(intent);
                Toast.makeText(this.getApplicationContext(), "Party created successfully!", Toast.LENGTH_SHORT).show();
            }
        });

        stepsPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private int current = 0;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                indicators.get(current).setEnabled(false);
                indicators.get(position).setEnabled(true);
                current = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        int position = stepsPager.getCurrentItem();
        adapter.save(position);
        if (position - 1 < 0) {
            super.onBackPressed();
        } else {
            stepsPager.setCurrentItem(position - 1);
        }
    }
}

class CreationPagerAdapter extends FragmentPagerAdapter {

    private final ArrayList<SavableFragment> fragments;

    public CreationPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);

        fragments = new ArrayList<>();
        fragments.add(new HostCreationGeneralFragment());
        fragments.add(new HostSettingFragment());
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return 2;
    }

    public void save(int position) {
        fragments.get(position).save();
    }
}