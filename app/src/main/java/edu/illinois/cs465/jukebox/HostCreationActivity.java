package edu.illinois.cs465.jukebox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import java.util.ArrayList;

public class HostCreationActivity extends AppCompatActivity {

    private HostCreationViewModel viewModel;
    private CreationPagerAdapter adapter;
    private ViewPager stepsPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_creation);

        viewModel = new ViewModelProvider(this).get(HostCreationViewModel.class);
        adapter = new CreationPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        stepsPager = findViewById(R.id.steps_pager);
        stepsPager.setAdapter(adapter);
    }
}

class CreationPagerAdapter extends FragmentPagerAdapter {

    private final ArrayList<Fragment> fragments;

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
}