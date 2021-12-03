package edu.illinois.cs465.jukebox;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.Objects;

import edu.illinois.cs465.jukebox.viewmodel.HostCreationViewModel;
import edu.illinois.cs465.jukebox.viewmodel.HostPartyOverviewBeforeViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HostPartyOverviewBeforeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HostPartyOverviewBeforeFragment extends Fragment {
    private HostCreationViewModel creationViewModel;

    View view;
    Button buttonStart;
    HostCreationGeneralFragment hostCreationGeneralFragment;


    public HostPartyOverviewBeforeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_host_party_overview_before, container, false);

        creationViewModel = new ViewModelProvider(requireActivity()).get(HostCreationViewModel.class);

        hostCreationGeneralFragment = (HostCreationGeneralFragment) getChildFragmentManager().findFragmentById(R.id.fragmentGeneralPartyOverviewBefore);
        Objects.requireNonNull(hostCreationGeneralFragment).bindViewModel();
        hostCreationGeneralFragment.setFocusableFields(false);

        // Setup start party button
        buttonStart = (Button) view.findViewById(R.id.button_start);
        buttonStart.setOnClickListener(v -> startButtonClick(getActivity()));

        return view;
    }

    public void startButtonClick(FragmentActivity ctx) {
        new CustomDialogFragment(
                ctx,
                "Confirm",
                "Are you sure you want to start the party?",
                "Start",
                "Cancel",
                HostPartyOverviewDuringActivity.class,
                null,
                creationViewModel.getPartyInfo().getValue().getPartyCode()
        ).show(getActivity().getSupportFragmentManager(), "StartPartyDialog");
    }
}