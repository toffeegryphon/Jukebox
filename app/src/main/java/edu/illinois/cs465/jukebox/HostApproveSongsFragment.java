package edu.illinois.cs465.jukebox;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Objects;

public class HostApproveSongsFragment extends Fragment {

    ArrayList<SongEntry> entryList;

    RecyclerView recyclerView;
    RecyclerViewAdapter adapter;
    Button approveButton;
    TextView suggestionCount;

    private MusicService musicService;
    private Intent playIntent;
    private boolean musicBound = false;
    private MusicService.MusicServiceListener musicListener;
    private RecyclerViewAdapter.RecyclerViewListener recyclerListener;

    public HostApproveSongsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (playIntent == null) {
            playIntent = new Intent(this.getContext(), MusicService.class);
            requireActivity().bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            requireActivity().startService(playIntent);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_host_approve_songs, container, false);

        recyclerView = view.findViewById(R.id.host_approve_recycler_view);
        RecyclerViewCustomEdgeDecorator decoration = new RecyclerViewCustomEdgeDecorator(0,230,false,true);
        recyclerView.addItemDecoration(decoration);

        entryList = new ArrayList<SongEntry>();

        adapter = new RecyclerViewAdapter(getActivity(), entryList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerListener = new RecyclerViewAdapter.RecyclerViewListener() {
            @Override
            public void onDeleteButtonPressed(RecyclerViewAdapter.ViewHolder holder, int _pos, SongEntry removedSong) {
//                if (musicBound) {
//                    musicService.removeSongFromQueue(_pos, true);
//                }
                suggestionCount.setText(String.valueOf(entryList.size()));

                createUndoSnackbarText(_pos, removedSong);
            }
        };
        adapter.registerListener(recyclerListener);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) { return false; }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
                SongEntry removedSong = entryList.remove(position);
                adapter.notifyDataSetChanged();

//                if (musicBound) {
//                    musicService.removeSongFromQueue(removedSong, true);
//                }

                suggestionCount.setText(String.valueOf(entryList.size()));

                createUndoSnackbarText(position, removedSong);
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                if (viewHolder != null) {
                    final View foregroundView = ((RecyclerViewAdapter.ViewHolder) viewHolder).foregroundLayout;

                    getDefaultUIUtil().onSelected(foregroundView);
                }
            }

            @Override
            public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
                                        RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                        int actionState, boolean isCurrentlyActive) {
                final View foregroundView = ((RecyclerViewAdapter.ViewHolder) viewHolder).foregroundLayout;
                getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
                        actionState, isCurrentlyActive);
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                final View foregroundView = ((RecyclerViewAdapter.ViewHolder) viewHolder).foregroundLayout;
                getDefaultUIUtil().clearView(foregroundView);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView,
                                    RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {
                final View foregroundView = ((RecyclerViewAdapter.ViewHolder) viewHolder).foregroundLayout;

                getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
                        actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        approveButton = view.findViewById(R.id.host_approve_button);
        approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (musicBound) {
                    musicService.setSongList(entryList);
                    Snackbar.make(approveButton, "Approved all song suggestions!", Snackbar.LENGTH_SHORT)
                            .setAnchorView(Objects.requireNonNull(requireActivity().findViewById(R.id.bottomNavigationViewBeforeParty)))
                            .show();
                }
            }
        });

        suggestionCount = view.findViewById(R.id.host_queue_song_count);
        suggestionCount.setText(String.valueOf(entryList.size()));

        return view;
    }

    private void createUndoSnackbarText(int position, SongEntry removedSong) {
        String snackbarText = "Removed '" + getResources().getString(removedSong.name) + "'";
        Snackbar snackbar = Snackbar
                .make(approveButton, snackbarText, Snackbar.LENGTH_LONG)
                .setAnchorView(Objects.requireNonNull(requireActivity().findViewById(R.id.bottomNavigationViewBeforeParty)))
                .setAction("UNDO", new View.OnClickListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onClick(View view) {
                        entryList.add(position, removedSong);
                        adapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(position);

//                        if (musicBound) {
//                            musicService.addSongToQueue(position, removedSong);
//                        }
                    }
                });
        snackbar.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateList(ArrayList<SongEntry> songList) {
        entryList.clear();
        entryList.addAll(songList);
        Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
        suggestionCount.setText(String.valueOf(entryList.size()));
    }

    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();

            musicListener = new MusicService.MusicServiceListener() {
                @Override
                public void onRegister(ArrayList<SongEntry> songList) {
                    updateList(songList);
                }

                public void onMediaPlayerPrepared() { }
                public void onMediaPlayerPause() { }
                public void onMediaPlayerUnpause() { }
                public void onMediaPlayerNewSong() { }

                @Override
                public void onQueueUpdate(ArrayList<SongEntry> songList) {
                    updateList(songList);
                }
            };

            musicService.registerListener(musicListener);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicService.unregisterListener(musicListener);
            musicBound = false;
        }
    };

    @Override
    public void onDestroy() {
        if (musicBound) {
            musicService.unregisterListener(musicListener);
        }
        adapter.unregisterListener(recyclerListener);
        super.onDestroy();
    }

//    public void addHostApprovalListItem(int image, int song_name, int artist, int url) {
//        addHostApprovalListItem(image, song_name, artist, url, new Button(getActivity()));
//    }
//
//    public void addHostApprovalListItem(int image, int song_name, int artist, int url, Button button) {
//        SongEntry item = new SongEntry(image, song_name, artist, url, button);
//        entryList.add(item);
//    }
}