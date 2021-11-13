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

/**
 * A simple {@link SavableFragment} subclass.
 * Use the {@link HostSettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HostSettingFragment extends SavableFragment {

    private HostCreationViewModel viewModel;

    private EditText editThreshold, editTimer, editLimit;
    private SwitchCompat switchAllow;
    private Button endPartyButton;

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

        editThreshold = view.findViewById(R.id.edit_skip_threshold);
        editTimer = view.findViewById(R.id.edit_skip_timer);
        switchAllow = view.findViewById(R.id.switch_suggestion_allow);
        editLimit = view.findViewById(R.id.edit_suggestion_limit);
        endPartyButton = view.findViewById(R.id.buttonHostSettingsEndParty);

        endPartyButton.setOnClickListener(v -> endButtonClick(getActivity()));

        if(getActivity().getClass() == HostPartyOverviewDuringActivity.class) // If host settings is on the during party screen
        {
            endPartyButton.setVisibility(View.VISIBLE);
        } else {
            endPartyButton.setVisibility(View.GONE);
        }

        return view;
    }

    public void endButtonClick(FragmentActivity ctx) {
        new CustomDialogFragment(ctx, "Confirm", "Are you sure you want to end the party?", "End", "Cancel", HostPartyOverviewPostActivity.class).show(getActivity().getSupportFragmentManager(), "EndPartyDialog");
    }

    public void save() {
        viewModel.setInteger(HostCreationViewModel.SKIP_THRESHOLD, Integer.parseInt(editThreshold.getText().toString()));
        viewModel.setInteger(HostCreationViewModel.SKIP_TIMER, Integer.parseInt(editTimer.getText().toString()));
        viewModel.setInteger(HostCreationViewModel.SUGGESTION_LIMIT, Integer.parseInt(editLimit.getText().toString()));
        viewModel.setBoolean(HostCreationViewModel.ARE_SUGGESTIONS_ALLOWED, switchAllow.isChecked());
    }

    private void bindIntegerObserver(TextView view, String key) {
        final Observer<Integer> observer = view::setText;
        LiveData<Integer> data = viewModel.getInteger(key, 0);
        data.observe(this, observer);
    }

    private void bindBooleanObserver(SwitchCompat view, String key) {
        final Observer<Boolean> observer = view::setChecked;
        LiveData<Boolean> data = viewModel.getBoolean(key, true);
        data.observe(this, observer);
    }

    public void bindViewModel() {
        bindIntegerObserver(editThreshold, HostCreationViewModel.SKIP_THRESHOLD);
        bindIntegerObserver(editTimer, HostCreationViewModel.SKIP_TIMER);
        bindIntegerObserver(editLimit, HostCreationViewModel.SUGGESTION_LIMIT);

        bindBooleanObserver(switchAllow, HostCreationViewModel.ARE_SUGGESTIONS_ALLOWED);
    }
}