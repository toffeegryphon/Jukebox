package edu.illinois.cs465.jukebox;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

import edu.illinois.cs465.jukebox.model.PartyInfo;
import edu.illinois.cs465.jukebox.viewmodel.HostCreationViewModel;

public class GuestSuggestionFragment extends Fragment {

    View view;

    ArrayList<SongEntry> entryList;

    RecyclerView recyclerView;
    RecyclerViewAdapter adapter;
    Button submitButton;
    TextView suggestionCount;

    private MusicService musicService;
    private Intent playIntent;
    private boolean musicBound = false;
    private MusicService.MusicServiceListener musicListener;
    private RecyclerViewAdapter.RecyclerViewListener recyclerListener;

    private HostCreationViewModel creationViewModel;
    private DocumentReference partyReference;

    private int suggestions;
    ViewFlipper viewFlipper;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_guest_suggestion, container, false);

        creationViewModel = new ViewModelProvider(requireActivity()).get(HostCreationViewModel.class);

        if (playIntent == null) {
            playIntent = new Intent(requireActivity(), MusicService.class);
            requireActivity().bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            requireActivity().startService(playIntent);
        }

        viewFlipper = view.findViewById(R.id.GuestSuggestionViewSwitcher);

        recyclerView = view.findViewById(R.id.guest_suggestion_recycler_view);
        RecyclerViewCustomEdgeDecorator decoration = new RecyclerViewCustomEdgeDecorator(0, 0, true, false);
        recyclerView.addItemDecoration(decoration);

        entryList = new ArrayList<SongEntry>();

        adapter = new RecyclerViewAdapter(requireActivity(), entryList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        recyclerListener = new RecyclerViewAdapter.RecyclerViewListener() {
            @Override
            public void onDeleteButtonPressed(RecyclerViewAdapter.ViewHolder holder, int _pos, SongEntry removedSong) {

                // TODO: Get host settings' suggestion limit instead of hardcoding '10'
                String newSuggestionCount = String.valueOf(entryList.size()) + " / " + suggestions;
                suggestionCount.setText(newSuggestionCount);

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
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(position, entryList.size());

                // TODO: Get host settings' suggestion limit instead of hardcoding '10'
                String newSuggestionCount = String.valueOf(entryList.size()) + " / " + suggestions;
                suggestionCount.setText(newSuggestionCount);

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

        submitButton = view.findViewById(R.id.guest_suggestion_submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (musicBound) {
                    musicService.setSongList(entryList);
                    String snackbarText = "Submitted " + String.valueOf(entryList.size()) + " song suggestions!";
                    Snackbar.make(submitButton, snackbarText, Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        suggestionCount = view.findViewById(R.id.guestSuggestionCount);



        String partyCode = requireActivity().getSharedPreferences("guest", Context.MODE_PRIVATE).getString(PartyInfo.PARTY_CODE, "TCAE");
        partyReference = FirebaseFirestore.getInstance().collection("partyInfo").document(partyCode);

        partyReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                // Update song suggestions amount
                suggestions = Integer.min(((Long) documentSnapshot.getData().get("suggestionLimit")).intValue(), 10);
                String suggestionCountText = suggestions + " / " + suggestions;
                suggestionCount.setText(suggestionCountText);

                // Initialize Guest suggestions
                for (int i = 0; i < suggestions; i++) {
                    if (i == 0) {
                        addSongListItem(R.drawable.songcover_onandon, R.string.songcover_name1, R.string.songcover_artist1, R.string.songcover_url1);
                    } else if (i == 1) {
                        addSongListItem(R.drawable.songcover_heroestonight, R.string.songcover_name2, R.string.songcover_artist2, R.string.songcover_url2);
                    } else if (i == 2) {
                        addSongListItem(R.drawable.songcover_invincible, R.string.songcover_name3, R.string.songcover_artist3, R.string.songcover_url3);
                    } else if (i == 3) {
                        addSongListItem(R.drawable.songcover_myheart, R.string.songcover_name4, R.string.songcover_artist4, R.string.songcover_url4);
                    } else if (i == 4) {
                        addSongListItem(R.drawable.songcover_blank, R.string.songcover_name5, R.string.songcover_artist5, R.string.songcover_url5);
                    } else if (i == 5) {
                        addSongListItem(R.drawable.songcover_symbolism, R.string.songcover_name6, R.string.songcover_artist6, R.string.songcover_url6);
                    } else if (i == 6) {
                        addSongListItem(R.drawable.songcover_whywelose, R.string.songcover_name7, R.string.songcover_artist7, R.string.songcover_url7);
                    } else if (i == 7) {
                        addSongListItem(R.drawable.songcover_cradles, R.string.songcover_name8, R.string.songcover_artist8, R.string.songcover_url8);
                    } else if (i == 8) {
                        addSongListItem(R.drawable.songcover_shine, R.string.songcover_name9, R.string.songcover_artist9, R.string.songcover_url9);
                    } else if (i == 9) {
                        addSongListItem(R.drawable.songcover_invisible, R.string.songcover_name10, R.string.songcover_artist10, R.string.songcover_url10);
                    }
                }

                adapter.notifyDataSetChanged();

                // Update if suggestions are enabled
                boolean suggestionsAllowed = (boolean) documentSnapshot.getData().get("areSuggestionsAllowed");
                if (suggestionsAllowed) {
                    viewFlipper.setDisplayedChild(0);
                } else {
                    viewFlipper.setDisplayedChild(1);
                }
            }
        });

        return view;
    }

    private void createUndoSnackbarText(int position, SongEntry removedSong) {
        String snackbarText = "Removed '" + getResources().getString(removedSong.name) + "'";
        Snackbar snackbar = Snackbar
                .make(submitButton, snackbarText, Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onClick(View view) {
                        entryList.add(position, removedSong);
                        adapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(position);

                        // TODO: Get host settings' suggestion limit instead of hardcoding '10'
                        String newSuggestionCount = String.valueOf(entryList.size()) + " / " + "10";
                        suggestionCount.setText(newSuggestionCount);
                    }
                });
        snackbar.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateList(ArrayList<SongEntry> songList) {
        entryList.clear();
        entryList.addAll(songList);
        Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
        // TODO: Get host settings' suggestion limit instead of hardcoding '10'
        String newSuggestionCount = String.valueOf(entryList.size()) + " / " + suggestions;
        suggestionCount.setText(newSuggestionCount);
    }

    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();

            musicListener = new MusicService.MusicServiceListener() {
                @Override
                public void onRegister(ArrayList<SongEntry> _songList) { }

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

    public void addSongListItem(int image, int song_name, int artist, int url) {
        addSongListItem(image, song_name, artist, url, new Button(requireActivity()));
    }

    public void addSongListItem(int image, int song_name, int artist, int url, Button button) {
        SongEntry item = new SongEntry(image, song_name, artist, url, button);
        entryList.add(item);
    }

//    @Override
//    public void onBackPressed() {
//        Intent intent = new Intent(requireActivity(), MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(intent);
//    }
}