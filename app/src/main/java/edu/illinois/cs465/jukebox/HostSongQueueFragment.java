package edu.illinois.cs465.jukebox;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HostSongQueueFragment extends Fragment {

    ArrayList<EntryItem> entryList;

    RecyclerView recyclerView;
    TextView queueCount;

    public HostSongQueueFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_host_song_queue, container, false);

        recyclerView = view.findViewById(R.id.host_queue_recycler_view);
        float density = getResources().getDisplayMetrics().density;
        RecyclerViewCustomEdgeDecorator decoration = new RecyclerViewCustomEdgeDecorator(0,(int) (56 * density),true,true);
        recyclerView.addItemDecoration(decoration);

        entryList = new ArrayList<EntryItem>();

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(getActivity(), entryList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        for (int i = 0; i < 10; i++) {
            addHostQueueListItem(R.drawable.ic_launcher_background, "Song " + i, "Artist " + i);
        }

        queueCount = view.findViewById(R.id.host_queue_song_count);
        queueCount.setText(String.valueOf(entryList.size()));

        return view;
    }

    public void addHostQueueListItem(int image, String song_name, String artist) {
        addHostQueueListItem(image, song_name, artist, new Button(getActivity()));
    }

    public void addHostQueueListItem(int image, String song_name, String artist, Button button) {
        EntryItem item = new EntryItem(image, song_name, artist, button);
        entryList.add(item);
    }
}