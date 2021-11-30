package edu.illinois.cs465.jukebox;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

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

    TextInputLayout nameLayout, themeLayout;

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
                // Validate fields
                nameLayout = adapter.getItem(position).getView().findViewById(R.id.text_input_layout_edit_text_name);
                themeLayout = adapter.getItem(position).getView().findViewById(R.id.text_input_layout_edit_text_theme);
                //dateLayout = adapter.getItem(position).getView().findViewById(R.id.text_input_layout_edit_text_date);
                //timeLayout = adapter.getItem(position).getView().findViewById(R.id.text_input_layout_edit_text_time);
                //locLayout = adapter.getItem(position).getView().findViewById(R.id.text_input_layout_edit_text_location);
                //descLayout = adapter.getItem(position).getView().findViewById(R.id.text_input_layout_edit_text_desc);

                boolean validName = isValidField(nameLayout, "Enter a valid party name");
                boolean validTheme = isValidField(themeLayout, "Enter a valid party theme");
                //boolean validDate = isValidField(dateLayout, "Enter a party date");
                //boolean validTime = isValidField(timeLayout, "Enter a party time");
                //boolean validLoc = isValidField(locLayout, "Enter a party location");
                //boolean validDesc = isValidField(descLayout, "Enter a party description");

                if (validName && validTheme) {
                    stepsPager.setCurrentItem(position + 1);
                }
            } else {
                // *** No need to validate since values default to 0 if empty ***
                //skipTimerLayout = adapter.getItem(position).getView().findViewById(R.id.skip_timer_text_input_layout);
                //suggestionLimitLayout = adapter.getItem(position).getView().findViewById(R.id.suggestion_limit_text_input_layout);

                //boolean validSkipTimer = isValidField(skipTimerLayout, "Enter a valid time");
                //boolean validSuggestionLimit = isValidField(suggestionLimitLayout, "Enter a valid suggestion limit");

                //if (validSkipTimer && validSuggestionLimit) {
                    viewModel.saveParty(getSharedPreferences("host", Context.MODE_PRIVATE));
                    String partyCode = Objects.requireNonNull(viewModel.getPartyInfo().getValue()).getPartyCode();
                    Intent intent = new Intent(HostCreationActivity.this, HostPartyOverviewBeforeActivity.class);
                    intent.putExtra(PartyInfo.PARTY_CODE, partyCode);
                    intent.putExtra("initialCreation", true);
                    startActivity(intent);
                //}
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

    private boolean isValidField(TextInputLayout layout, String error_text) {
        if (layout.getEditText().getText().toString().isEmpty()) {
            layout.setErrorEnabled(true);
            layout.setError(error_text);
            layout.getEditText().addTextChangedListener(new TextWatcher() {
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
                public void afterTextChanged(Editable editable) {
                    if (!layout.getEditText().getText().toString().isEmpty()) {
                        layout.setErrorEnabled(false);
                    }
                }
            });
            return false;
        } else {
            layout.setErrorEnabled(false);
            return true;
        }
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