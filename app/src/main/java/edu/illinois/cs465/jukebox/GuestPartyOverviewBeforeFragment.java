package edu.illinois.cs465.jukebox;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.MediaController;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import edu.illinois.cs465.jukebox.viewmodel.HostCreationViewModel;

import java.util.Objects;

public class GuestPartyOverviewBeforeFragment extends Fragment {
    private HostCreationViewModel creationViewModel;

    View view;
    HostCreationGeneralFragment hostCreationGeneralFragment;

    public GuestPartyOverviewBeforeFragment() {
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
        view = inflater.inflate(R.layout.fragment_guest_overview_before, container, false);

        creationViewModel = new ViewModelProvider(requireActivity()).get(HostCreationViewModel.class);

        hostCreationGeneralFragment = (HostCreationGeneralFragment) getChildFragmentManager().findFragmentById(R.id.fragmentGeneralGuestPartyOverviewBefore);
        Objects.requireNonNull(hostCreationGeneralFragment).bindViewModel();

        hostCreationGeneralFragment.setFocusableFields(false);

        return view;
    }
}