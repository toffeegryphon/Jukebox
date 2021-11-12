package edu.illinois.cs465.jukebox;

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

public class HostCreationActivity extends AppCompatActivity {

    private HostCreationViewModel viewModel;
    private CreationPagerAdapter adapter;
    private ViewPager stepsPager;

    private Button buttonContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_creation);

        viewModel = new ViewModelProvider(this).get(HostCreationViewModel.class);
        adapter = new CreationPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        stepsPager = findViewById(R.id.steps_pager);
        stepsPager.setOffscreenPageLimit(1);
        stepsPager.setAdapter(adapter);

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
                String json = viewModel.toJson();
                Intent intent = new Intent(HostCreationActivity.this, HostPartyOverviewBeforeActivity.class);
                intent.putExtra(HostPartyOverviewBeforeActivity.DATA_CONFIG, json);
                startActivity(intent);
                Toast.makeText(this.getApplicationContext(), "Party created successfully!", Toast.LENGTH_SHORT).show();
            }
        });
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