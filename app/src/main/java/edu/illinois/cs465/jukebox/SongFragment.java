package edu.illinois.cs465.jukebox;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SongFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SongFragment extends Fragment {

    Button delete_button;
    TextView song_name_view;
    TextView song_artist_view;

    // TODO: Rename and change types of parameters
    private String song_name;
    private String song_artist;

    public SongFragment() {
        // Required empty public constructor
        song_name = "SHIIEEHH";
        song_artist = "Senator Clay Davis";
    }

    public SongFragment(String name, String artist) {
        song_name = name;
        song_artist = artist;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_song, container, false);

//        delete_button = view.findViewById(R.id.fragment_song_button);
//        song_name_view = view.findViewById(R.id.fragment_song_name);
//        song_artist_view = view.findViewById(R.id.fragment_song_artist);
//
//        song_name_view.setText(song_name);
//        song_artist_view.setText(song_artist);

//        if(getActivity().getClass() == GuestSuggestionActivity.class)
//        {
//            delete_button.setOnClickListener(v -> ((GuestSuggestionActivity) getActivity()).removeFragment(this));
//        } else {
//
//        }

        return view;
    }
}