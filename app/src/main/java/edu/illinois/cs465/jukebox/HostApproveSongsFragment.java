package edu.illinois.cs465.jukebox;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HostApproveSongsFragment extends Fragment {

    ArrayList<SongEntry> entryList;

    RecyclerView recyclerView;
    Button approveButton;
    TextView suggestionCount;

    public HostApproveSongsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_host_approve_songs, container, false);

        recyclerView = view.findViewById(R.id.host_approve_recycler_view);
        RecyclerViewCustomEdgeDecorator decoration = new RecyclerViewCustomEdgeDecorator(0,0,true,false);
        recyclerView.addItemDecoration(decoration);

        entryList = new ArrayList<SongEntry>();

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(getActivity(), entryList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        addHostApprovalListItem(R.drawable.songcover_onandon, R.string.songcover_name1, R.string.songcover_artist1, R.string.songcover_url1);
        addHostApprovalListItem(R.drawable.songcover_heroestonight, R.string.songcover_name2, R.string.songcover_artist2, R.string.songcover_url2);
        addHostApprovalListItem(R.drawable.songcover_invincible, R.string.songcover_name3, R.string.songcover_artist3, R.string.songcover_url3);
        addHostApprovalListItem(R.drawable.songcover_myheart, R.string.songcover_name4, R.string.songcover_artist4, R.string.songcover_url4);
        addHostApprovalListItem(R.drawable.songcover_blank, R.string.songcover_name5, R.string.songcover_artist5, R.string.songcover_url5);
        addHostApprovalListItem(R.drawable.songcover_symbolism, R.string.songcover_name6, R.string.songcover_artist6, R.string.songcover_url6);
        addHostApprovalListItem(R.drawable.songcover_whywelose, R.string.songcover_name7, R.string.songcover_artist7, R.string.songcover_url7);
        addHostApprovalListItem(R.drawable.songcover_cradles, R.string.songcover_name8, R.string.songcover_artist8, R.string.songcover_url8);
        addHostApprovalListItem(R.drawable.songcover_shine, R.string.songcover_name9, R.string.songcover_artist9, R.string.songcover_url9);
        addHostApprovalListItem(R.drawable.songcover_invisible, R.string.songcover_name10, R.string.songcover_artist10, R.string.songcover_url10);

        approveButton = view.findViewById(R.id.host_approve_button);
        approveButton.setOnClickListener(v -> Toast.makeText(getActivity(), "Approved song suggestions!", Toast.LENGTH_SHORT).show());


        suggestionCount = view.findViewById(R.id.host_queue_song_count);
        suggestionCount.setText(String.valueOf(entryList.size()));

        return view;
    }

    public void addHostApprovalListItem(int image, int song_name, int artist, int url) {
        addHostApprovalListItem(image, song_name, artist, url, new Button(getActivity()));
    }

    public void addHostApprovalListItem(int image, int song_name, int artist, int url, Button button) {
        SongEntry item = new SongEntry(image, song_name, artist, url, button);
        entryList.add(item);
    }
}