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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HostPartyOverviewBeforeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HostPartyOverviewBeforeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HostPartyOverviewBeforeFragment newInstance(String param1, String param2) {
        HostPartyOverviewBeforeFragment fragment = new HostPartyOverviewBeforeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_host_party_overview_before, container, false);

        creationViewModel = new ViewModelProvider(requireActivity()).get(HostCreationViewModel.class);

        hostCreationGeneralFragment = (HostCreationGeneralFragment) getChildFragmentManager().findFragmentById(R.id.fragmentGeneralPartyOverviewBefore);
        Objects.requireNonNull(hostCreationGeneralFragment).bindViewModel();
        hostCreationGeneralFragment.setEnabled(false);

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
                creationViewModel.getPartyInfo().getValue().getPartyCode()
        ).show(getActivity().getSupportFragmentManager(), "StartPartyDialog");
    }
}