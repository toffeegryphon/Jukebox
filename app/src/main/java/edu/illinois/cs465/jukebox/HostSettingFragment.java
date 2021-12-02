package edu.illinois.cs465.jukebox;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

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

    private EditText suggestionLimit;
    private TextView suggestionLimitWarning;
    private SwitchCompat switchAllow;
    private Button endPartyButton;
    private TextView skipThreshold, skipThresholdWarning;
    private TextView skipTimer, skipTimerWarning;
    private LinearLayout songSuggestionsLayout;

    private CustomSliderBar skipThresholdSlider;

    private CustomSliderBar skipTimerSlider;

    private MusicService musicService;
    private Intent playIntent;

    private DocumentReference partyReference;

    public HostSettingFragment() {
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

        // Setup skip threshold custom slider
        skipThresholdSlider = view.findViewById(R.id.slider_skip_threshold);
        createCustomSlider(skipThresholdSlider, 100, 9, 41, 50);

        // Setup skip timer custom slider
        skipTimerSlider = view.findViewById(R.id.slider_skip_timer);
        createCustomSlider(skipTimerSlider, 90, 14, 46, 30);

        skipThreshold = view.findViewById(R.id.skip_threshold_number);
        skipThreshold.setText(getResources().getString(R.string.skip_threshold_number, (int) skipThresholdSlider.getValue()));
        skipThresholdWarning = view.findViewById(R.id.skip_threshold_warning_text);
        skipThresholdWarning.setVisibility(View.GONE);
        skipTimer = view.findViewById(R.id.skip_timer_number);
        skipTimer.setText(getResources().getString(R.string.skip_timer_number, (int) skipTimerSlider.getValue()));
        skipTimerWarning = view.findViewById(R.id.skip_timer_warning_text);
        skipTimerWarning.setVisibility(View.GONE);
        switchAllow = view.findViewById(R.id.switch_suggestion_allow);
        suggestionLimit = view.findViewById(R.id.edit_suggestion_limit);
        suggestionLimitWarning = view.findViewById(R.id.suggestion_limit_warning_text);
        suggestionLimitWarning.setVisibility(View.GONE);
        endPartyButton = view.findViewById(R.id.buttonHostSettingsEndParty);
        songSuggestionsLayout = view.findViewById(R.id.linearLayoutHostSettingsSuggestions);

        bindViewModel();
        initListeners();

        // Fix bottom padding for when "end party" button is visible and remove song suggestions
        if(getActivity().getClass() == HostPartyOverviewDuringActivity.class)
        {
            endPartyButton.setVisibility(View.VISIBLE);
            view.findViewById(R.id.linearLayoutHostSettingsSuggestions).setVisibility(View.GONE);

            int padding_in_dp = 125;
            final float scale = getResources().getDisplayMetrics().density;
            int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
            songSuggestionsLayout.setPadding(0, 0, 0, padding_in_px);

            if (playIntent == null) {
                playIntent = new Intent(this.getContext(), MusicService.class);
                getActivity().bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
                getActivity().startService(playIntent);
            }
        } else {
            endPartyButton.setVisibility(View.GONE);
            songSuggestionsLayout.setPadding(0,0,0,0);
        }

        partyReference = FirebaseFirestore.getInstance().collection("partyInfo").document("AAAA");

        return view;
    }

    private void createCustomSlider(CustomSliderBar slider, float totalSpan, float firstOffSpan, float purpleSpan, float secondOffSpan) {
        ArrayList<SliderProgressItem> itemList = new ArrayList<SliderProgressItem>();

        SliderProgressItem mProgressItem = new SliderProgressItem();
        mProgressItem.progressItemPercentage = ((firstOffSpan / totalSpan) * 100);
        mProgressItem.color = R.color.purple_1_not_active;
        itemList.add(mProgressItem);

        mProgressItem = new SliderProgressItem();
        mProgressItem.progressItemPercentage = (purpleSpan / totalSpan) * 100;
        mProgressItem.color = R.color.purple_1_active;
        itemList.add(mProgressItem);

        mProgressItem = new SliderProgressItem();
        mProgressItem.progressItemPercentage = (secondOffSpan / totalSpan) * 100;
        mProgressItem.color = R.color.purple_1_not_active;
        itemList.add(mProgressItem);

        slider.initData(itemList);
        slider.invalidate();
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

        skipTimerSlider.addOnChangeListener((slider, value, fromUser) -> {
            skipTimer.setText(getResources().getString(R.string.skip_timer_number, (int) value));
            if ((int) value == 0) {
                skipTimerWarning.setVisibility(View.VISIBLE);
                skipTimerWarning.setText(getResources().getString(R.string.skip_timer_warning_0));
            } else if ((int) value == 1) {
                skipTimerWarning.setVisibility(View.VISIBLE);
                skipTimerWarning.setText(getResources().getString(R.string.skip_timer_warning_1));
            } else if ((int) value < 15) {
                skipTimerWarning.setVisibility(View.VISIBLE);
                skipTimerWarning.setText(getResources().getString(R.string.skip_timer_warning_too_low, (int) value));
            } else if ((int) value > 60) {
                skipTimerWarning.setVisibility(View.VISIBLE);
                skipTimerWarning.setText(getResources().getString(R.string.skip_timer_warning_too_high, (int) value));
            } else {
                skipTimerWarning.setVisibility(View.GONE);
            }
        });

        suggestionLimit.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            public void afterTextChanged(Editable editable) {
                if (String.valueOf(suggestionLimit.getText()).isEmpty()) {
                    suggestionLimit.setText("0");
                    suggestionLimitWarning.setVisibility(View.VISIBLE);
                    suggestionLimitWarning.setText(getResources().getString(R.string.suggestion_limit_warning_0));
                    return;
                }

                int value = Integer.parseInt(String.valueOf(suggestionLimit.getText()));
                if (value <= 0) {
                    suggestionLimitWarning.setVisibility(View.VISIBLE);
                    suggestionLimitWarning.setText(getResources().getString(R.string.suggestion_limit_warning_0));
                } else if (value > 25) {
                    suggestionLimitWarning.setVisibility(View.VISIBLE);
                    suggestionLimitWarning.setText(getResources().getString(R.string.suggestion_limit_warning_too_high, value));
                } else {
                    suggestionLimitWarning.setVisibility(View.GONE);
                }
            }
        });

        endPartyButton.setOnClickListener(v -> endButtonClick(getActivity()));
    }

    public void endButtonClick(FragmentActivity ctx) {
        new CustomDialogFragment(ctx, "Confirm", "Are you sure you want to end the party?", "End", "Cancel", HostPartyOverviewPostActivity.class, musicService, viewModel.getPartyInfo().getValue().getPartyCode()).show(getActivity().getSupportFragmentManager(), "EndPartyDialog");
    }

    public void save() {
        int skipThreshold = (int) skipThresholdSlider.getValue();
        int skipTime = (int) skipTimerSlider.getValue();
        int limit = 0;
        if (!suggestionLimit.getText().toString().isEmpty()) { limit = Integer.parseInt(suggestionLimit.getText().toString()); }
        boolean allow = switchAllow.isChecked();
        viewModel.setHostSettingInfo(skipThreshold, skipTime, limit, allow);
    }

    public void bindViewModel() {
        LiveData<PartyInfo> data = viewModel.getPartyInfo();
        data.observe(getViewLifecycleOwner(), new Observer<PartyInfo>() {

            @Override
            public void onChanged(PartyInfo partyInfo) {
                // TODO: Should only run below on first time
                if (getActivity().getClass() == HostCreationActivity.class
                        && partyInfo.getSkipThreshold() == 0
                        && partyInfo.getSkipTimer() == 0
                        && partyInfo.getSuggestionLimit() == 0
                        && partyInfo.getAreSuggestionsAllowed() == false) {
                    viewModel.setHostSettingInfo(20, 20, 10, true);
                }

                skipThresholdSlider.setValue(partyInfo.getSkipThreshold());
                skipTimerSlider.setValue(partyInfo.getSkipTimer());
                suggestionLimit.setText(String.valueOf(partyInfo.getSuggestionLimit()));
                switchAllow.setChecked(partyInfo.getAreSuggestionsAllowed());
            }
        });
    }

    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) { }
    };

    // TODO: Is this right?
    @Override
    public void onDestroy() {
        partyReference.update("skipThreshold", (int) skipThresholdSlider.getValue());
        partyReference.update("skipTimer", (int) skipTimerSlider.getValue());
        partyReference.update("areSuggestionsAllowed", switchAllow.isChecked());
        partyReference.update("suggestionLimit", Integer.parseInt(suggestionLimit.getText().toString()));
        save();
        super.onDestroy();
    }
}