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

    ArrayList<EntryItem> entryList;

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

        entryList = new ArrayList<EntryItem>();

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(getActivity(), entryList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        for (int i = 0; i < 10; i++) {
            addHostApprovalListItem(R.drawable.ic_launcher_background, "Song " + i, "Artist " + i);
        }

        approveButton = view.findViewById(R.id.host_approve_button);
        approveButton.setOnClickListener(v -> Toast.makeText(getActivity(), "Approved song suggestions!", Toast.LENGTH_SHORT).show());


        suggestionCount = view.findViewById(R.id.host_queue_song_count);
        suggestionCount.setText(String.valueOf(entryList.size()));

        return view;
    }

    public void addHostApprovalListItem(int image, String song_name, String artist) {
        addHostApprovalListItem(image, song_name, artist, new Button(getActivity()));
    }

    public void addHostApprovalListItem(int image, String song_name, String artist, Button button) {
        EntryItem item = new EntryItem(image, song_name, artist, button);
        entryList.add(item);
    }
}