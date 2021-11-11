package edu.illinois.cs465.jukebox;

import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * A simple {@link SavableFragment} subclass.
 * Use the {@link HostSettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HostSettingFragment extends SavableFragment {

    private HostCreationViewModel viewModel;

    private EditText editThreshold, editTimer, editLimit;
    private SwitchCompat switchAllow;

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

        return view;
    }

    public void save() {
        viewModel.setInteger(HostCreationViewModel.SKIP_THRESHOLD, Integer.parseInt(editThreshold.getText().toString()));
        viewModel.setInteger(HostCreationViewModel.SKIP_TIMER, Integer.parseInt(editTimer.getText().toString()));
        viewModel.setInteger(HostCreationViewModel.SUGGESTION_LIMIT, Integer.parseInt(editLimit.getText().toString()));
        viewModel.setBoolean(HostCreationViewModel.ARE_SUGGESTIONS_ALLOWED, switchAllow.isChecked());
    }
}