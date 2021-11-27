package edu.illinois.cs465.jukebox;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.internal.TextWatcherAdapter;
import com.google.android.material.slider.Slider;

import java.util.ArrayList;

import edu.illinois.cs465.jukebox.model.PartyInfo;
import edu.illinois.cs465.jukebox.viewmodel.HostCreationViewModel;

/**
 * A simple {@link SavableFragment} subclass.
 * Use the {@link HostSettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HostSettingFragment extends SavableFragment {

    private HostCreationViewModel viewModel;

    private EditText editTimer, editLimit;
    private SwitchCompat switchAllow;
    private Button endPartyButton;
    private TextView skipThreshold;
    private TextView skipThresholdWarning;
    private TextView skipTimerWarning;
    private LinearLayout songSuggestionsLayout;

    private CustomSliderBar skipThresholdSlider;
    private float totalSpan = 100;
    private float firstOffSpan = 9;
    private float purpleSpan = 41;
    private float secondOffSpan = 50;
    private ArrayList<SliderProgressItem> progressItemList;
    private SliderProgressItem mProgressItem;

    public HostSettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HostSettingFragment.
     */
    public static HostSettingFragment newInstance() {
        HostSettingFragment fragment = new HostSettingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(HostCreationViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_host_setting, container, false);

        // Setup custom slider
        skipThresholdSlider = view.findViewById(R.id.slider_skip_threshold);
        progressItemList = new ArrayList<SliderProgressItem>();

        mProgressItem = new SliderProgressItem();
        mProgressItem.progressItemPercentage = ((firstOffSpan / totalSpan) * 100);
        mProgressItem.color = R.color.purple_1_not_active;
        progressItemList.add(mProgressItem);

        mProgressItem = new SliderProgressItem();
        mProgressItem.progressItemPercentage = (purpleSpan / totalSpan) * 100;
        mProgressItem.color = R.color.purple_1_active;
        progressItemList.add(mProgressItem);

        mProgressItem = new SliderProgressItem();
        mProgressItem.progressItemPercentage = (secondOffSpan / totalSpan) * 100;
        mProgressItem.color = R.color.purple_1_not_active;
        progressItemList.add(mProgressItem);

        skipThresholdSlider.initData(progressItemList);
        skipThresholdSlider.invalidate();


        skipThreshold = view.findViewById(R.id.skip_threshold_number);
        skipThreshold.setText(getResources().getString(R.string.skip_threshold_number, (int) skipThresholdSlider.getValue()));
        skipThresholdWarning = view.findViewById(R.id.skip_threshold_warning_text);
        skipThresholdWarning.setVisibility(View.GONE);
        editTimer = view.findViewById(R.id.edit_skip_timer);
        skipTimerWarning = view.findViewById(R.id.skip_timer_warning_text);
        skipTimerWarning.setVisibility(View.GONE);
        switchAllow = view.findViewById(R.id.switch_suggestion_allow);
        editLimit = view.findViewById(R.id.edit_suggestion_limit);
        endPartyButton = view.findViewById(R.id.buttonHostSettingsEndParty);
        songSuggestionsLayout = view.findViewById(R.id.linearLayoutHostSettingsSuggestions);

        bindViewModel();

        initListeners();

        // Fix bottom padding for when "end party" button is visible
        if(getActivity().getClass() == HostPartyOverviewDuringActivity.class)
        {
            endPartyButton.setVisibility(View.VISIBLE);

            int padding_in_dp = 125;
            final float scale = getResources().getDisplayMetrics().density;
            int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
            songSuggestionsLayout.setPadding(0, 0, 0, padding_in_px);
        } else {
            endPartyButton.setVisibility(View.GONE);
            songSuggestionsLayout.setPadding(0,0,0,0);
        }

        return view;
    }

    private void initListeners() {
        skipThresholdSlider.addOnChangeListener((slider, value, fromUser) -> {
                    skipThreshold.setText(getResources().getString(R.string.skip_threshold_number, (int) value));
                    if ((int) value == 0) {
                        skipThresholdWarning.setVisibility(View.VISIBLE);
                        skipThresholdWarning.setText(getResources().getString(R.string.skip_threshold_warning_0));
                    } else if ((int) value < 10) {
                        skipThresholdWarning.setVisibility(View.VISIBLE);
                        skipThresholdWarning.setText(getResources().getString(R.string.skip_threshold_warning_too_low, (int) value));
                    } else if ((int) value == 100) {
                        skipThresholdWarning.setVisibility(View.VISIBLE);
                        skipThresholdWarning.setText(getResources().getString(R.string.skip_threshold_warning_100));
                    } else if ((int) value > 50) {
                        skipThresholdWarning.setVisibility(View.VISIBLE);
                        skipThresholdWarning.setText(getResources().getString(R.string.skip_threshold_warning_too_high, (int) value));
                    } else {
                        skipThresholdWarning.setVisibility(View.GONE);
                    }
                });

        editTimer.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            public void afterTextChanged(Editable editable) {
                if (String.valueOf(editTimer.getText()).isEmpty()) {
                    skipTimerWarning.setVisibility(View.GONE);
                    return;
                }

                int value = Integer.parseInt(String.valueOf(editTimer.getText()));
                if (value <= 0) {
                    skipTimerWarning.setVisibility(View.VISIBLE);
                    skipTimerWarning.setText(getResources().getString(R.string.skip_timer_warning_0));
                } else if (value < 15) {
                    skipTimerWarning.setVisibility(View.VISIBLE);
                    skipTimerWarning.setText(getResources().getString(R.string.skip_timer_warning_too_low, value));
                } else if (value > 60) {
                    skipTimerWarning.setVisibility(View.VISIBLE);
                    skipTimerWarning.setText(getResources().getString(R.string.skip_timer_warning_too_high, value));
                } else {
                    skipTimerWarning.setVisibility(View.GONE);
                }
            }
        });

        endPartyButton.setOnClickListener(v -> endButtonClick(getActivity()));
    }

    public void endButtonClick(FragmentActivity ctx) {
        new CustomDialogFragment(ctx, "Confirm", "Are you sure you want to end the party?", "End", "Cancel", HostPartyOverviewPostActivity.class, viewModel.getPartyInfo().getValue().getPartyCode()).show(getActivity().getSupportFragmentManager(), "EndPartyDialog");
    }

    public void save() {
        int skip = (int) skipThresholdSlider.getValue();
        int time = 0;
        if (!editTimer.getText().toString().isEmpty()) { time = Integer.parseInt(editTimer.getText().toString()); }
        int limit = 0;
        if (!editLimit.getText().toString().isEmpty()) { limit = Integer.parseInt(editLimit.getText().toString()); }
        boolean allow = switchAllow.isChecked();
        viewModel.setHostSettingInfo(skip, time, limit, allow);
    }

    public void bindViewModel() {
        LiveData<PartyInfo> data = viewModel.getPartyInfo();
        data.observe(getViewLifecycleOwner(), new Observer<PartyInfo>() {

            @Override
            public void onChanged(PartyInfo partyInfo) {
                skipThresholdSlider.setValue(partyInfo.getSkipThreshold());
                editTimer.setText(String.valueOf(partyInfo.getSkipTimer()));
                editLimit.setText(String.valueOf(partyInfo.getSuggestionLimit()));
                switchAllow.setChecked(partyInfo.getAreSuggestionsAllowed());

                // TODO: Should only run below on first time
                if (getActivity().getClass() == HostCreationActivity.class
                        && partyInfo.getSkipThreshold() == 0
                        && partyInfo.getSkipTimer() == 0
                        && partyInfo.getSuggestionLimit() == 0
                        && partyInfo.getAreSuggestionsAllowed() == false) {
                    skipThresholdSlider.setValue(20);
                    editTimer.setText("20");
                    switchAllow.setChecked(true);
                    editLimit.setText("10");
                }
            }
        });
    }
}