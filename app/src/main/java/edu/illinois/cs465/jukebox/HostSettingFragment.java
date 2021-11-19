package edu.illinois.cs465.jukebox;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.slider.Slider;

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
    private Slider editThreshold;
    private TextView labelThreshold;

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

        editThreshold = view.findViewById(R.id.slider_skip_threshold);
        labelThreshold = view.findViewById(R.id.label_skip_threshold);
        labelThreshold.setText(getResources().getString(R.string.label_skip_threshold, (int) editThreshold.getValue()));
        editTimer = view.findViewById(R.id.edit_skip_timer);
        switchAllow = view.findViewById(R.id.switch_suggestion_allow);
        editLimit = view.findViewById(R.id.edit_suggestion_limit);
        endPartyButton = view.findViewById(R.id.buttonHostSettingsEndParty);

        bindViewModel();

        initListeners();

        if(getActivity().getClass() == HostPartyOverviewDuringActivity.class) // If host settings is on the during party screen
        {
            endPartyButton.setVisibility(View.VISIBLE);
        } else {
            endPartyButton.setVisibility(View.GONE);
        }

        return view;
    }

    private void initListeners() {
        editThreshold.addOnChangeListener((slider, value, fromUser) -> {
            labelThreshold.setText(getResources().getString(R.string.label_skip_threshold, (int) value));
        });
        endPartyButton.setOnClickListener(v -> endButtonClick(getActivity()));
    }

    public void endButtonClick(FragmentActivity ctx) {
        new CustomDialogFragment(ctx, "Confirm", "Are you sure you want to end the party?", "End", "Cancel", HostPartyOverviewPostActivity.class, viewModel.getPartyInfo().getValue().getPartyCode()).show(getActivity().getSupportFragmentManager(), "EndPartyDialog");
    }

    public void save() {
        viewModel.setHostSettingInfo((int) editThreshold.getValue(), Integer.parseInt(editTimer.getText().toString()), Integer.parseInt(editLimit.getText().toString()), switchAllow.isChecked());
    }

    public void bindViewModel() {
        LiveData<PartyInfo> data = viewModel.getPartyInfo();
        data.observe(getViewLifecycleOwner(), new Observer<PartyInfo>() {

            @Override
            public void onChanged(PartyInfo partyInfo) {
                editThreshold.setValue(partyInfo.getSkipThreshold());
                editTimer.setText(String.valueOf(partyInfo.getSkipTimer()));
                editLimit.setText(String.valueOf(partyInfo.getSuggestionLimit()));
                switchAllow.setChecked(partyInfo.getAreSuggestionsAllowed());
            }
        });

    }
}