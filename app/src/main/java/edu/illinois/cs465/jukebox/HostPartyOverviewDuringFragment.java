package edu.illinois.cs465.jukebox;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import edu.illinois.cs465.jukebox.model.PartyInfo;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HostPartyOverviewDuringFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HostPartyOverviewDuringFragment extends Fragment {
    private DocumentReference partyReference;

    View view;
    ImageView playPauseIcon;
    TextView songName;
    TextView artistName;
    boolean musicIsPlaying = true;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HostPartyOverviewDuringFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HostPartyOverviewDuringFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HostPartyOverviewDuringFragment newInstance(String param1, String param2) {
        HostPartyOverviewDuringFragment fragment = new HostPartyOverviewDuringFragment();
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

        String partyCode = requireActivity().getSharedPreferences("guest", Context.MODE_PRIVATE).getString(PartyInfo.PARTY_CODE, "AAAA");
        partyReference = FirebaseFirestore.getInstance().collection("partyInfo").document(partyCode);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_host_party_overview_during, container, false);

        // Setup start party button
        playPauseIcon = (ImageView) view.findViewById(R.id.imageViewHostDuringPartyPlayPauseButton);
        playPauseIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Replace if statements with actually checking if music is playing or paused
                if (musicIsPlaying) {
                    playPauseIcon.setImageResource(R.drawable.ic_host_play_button);
                    musicIsPlaying = false;
                } else {
                    playPauseIcon.setImageResource(R.drawable.ic_host_pause_button);
                    musicIsPlaying = true;
                }
            }
        });

        songName = view.findViewById(R.id.textViewHostDuringPartySongName);
        songName.setSelected(true);

        artistName = view.findViewById(R.id.textViewHostDuringPartyArtistName);
        artistName.setSelected(true);

        updateSong();

        // TODO: Update progress bar and song time countdown for when music is playing
        // TODO: Make skip button actually skips the current song
        return view;
    }

    private void updateSong() {
        partyReference.addSnapshotListener((value, error) -> {
            if (value != null) {
                String currentSong = value.getString("currentSong");
                if (!currentSong.equals(songName.getText().toString())) {
                    songName.setText(currentSong);
                }
            }
        });
    }
}