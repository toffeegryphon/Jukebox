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

        addHostQueueListItem(R.drawable.songcover_onandon, R.string.songcover_name1, R.string.songcover_artist1, R.string.songcover_url1);
        addHostQueueListItem(R.drawable.songcover_heroestonight, R.string.songcover_name2, R.string.songcover_artist2, R.string.songcover_url2);
        addHostQueueListItem(R.drawable.songcover_invincible, R.string.songcover_name3, R.string.songcover_artist3, R.string.songcover_url3);
        addHostQueueListItem(R.drawable.songcover_myheart, R.string.songcover_name4, R.string.songcover_artist4, R.string.songcover_url4);
        addHostQueueListItem(R.drawable.songcover_blank, R.string.songcover_name5, R.string.songcover_artist5, R.string.songcover_url5);
        addHostQueueListItem(R.drawable.songcover_symbolism, R.string.songcover_name6, R.string.songcover_artist6, R.string.songcover_url6);
        addHostQueueListItem(R.drawable.songcover_whywelose, R.string.songcover_name7, R.string.songcover_artist7, R.string.songcover_url7);
        addHostQueueListItem(R.drawable.songcover_cradles, R.string.songcover_name8, R.string.songcover_artist8, R.string.songcover_url8);
        addHostQueueListItem(R.drawable.songcover_shine, R.string.songcover_name9, R.string.songcover_artist9, R.string.songcover_url9);
        addHostQueueListItem(R.drawable.songcover_invisible, R.string.songcover_name10, R.string.songcover_artist10, R.string.songcover_url10);

        queueCount = view.findViewById(R.id.host_queue_song_count);
        queueCount.setText(String.valueOf(entryList.size()));

        return view;
    }

    public void addHostQueueListItem(int image, int song_name, int artist, int url) {
        addHostQueueListItem(image, song_name, artist, url, new Button(getActivity()));
    }

    public void addHostQueueListItem(int image, int song_name, int artist, int url, Button button) {
        EntryItem item = new EntryItem(image, song_name, artist, url, button);
        entryList.add(item);
    }
}