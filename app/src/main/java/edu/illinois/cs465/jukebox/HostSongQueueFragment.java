package edu.illinois.cs465.jukebox;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Objects;

import edu.illinois.cs465.jukebox.viewmodel.MusicService;

public class HostSongQueueFragment extends Fragment {

    ArrayList<SongEntry> entryList;

    RecyclerView recyclerView;
    RecyclerViewAdapter adapter;
    TextView queueCount;

    private MusicService musicService;
    private Intent playIntent;
    private boolean musicBound = false;
    private MusicService.MusicServiceListener musicListener;
    private RecyclerViewAdapter.RecyclerViewListener recyclerListener;

    public HostSongQueueFragment() { }

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
        View view = inflater.inflate(R.layout.fragment_host_song_queue, container, false);

        recyclerView = view.findViewById(R.id.host_queue_recycler_view);
        float density = getResources().getDisplayMetrics().density;
        RecyclerViewCustomEdgeDecorator decoration = new RecyclerViewCustomEdgeDecorator(0,(int) (62 * density),true,true);
        recyclerView.addItemDecoration(decoration);

        entryList = new ArrayList<SongEntry>();

        adapter = new RecyclerViewAdapter(getActivity(), entryList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerListener = new RecyclerViewAdapter.RecyclerViewListener() {
            @Override
            public void onDeleteButtonPressed(RecyclerViewAdapter.ViewHolder holder, int _pos, SongEntry removedSong) {
                if (musicBound) {
                    musicService.removeSongFromQueue(_pos, true);
                }
                queueCount.setText(String.valueOf(entryList.size()));

                createSnackbarText(_pos, removedSong);
            }
        };
        adapter.registerListener(recyclerListener);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) { return false; }

            // Commented because adding red background lagged on my emulator. Feel free to try it out
            // Helpful link: https://medium.com/nemanja-kovacevic/recyclerview-swipe-to-delete-no-3rd-party-lib-necessary-6bf6a6601214
//            @Override
//            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
//                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {
//                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//
//                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
//                    Bitmap icon;
//                    Paint paint = new Paint();
//
//                    View itemView = viewHolder.itemView;
//                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
//                    float width = height / 3;
//
//                    if (dX < 0) {
//                        paint.setColor(Color.parseColor("#D32F2F"));
//
//                        RectF background = new RectF(
//                                (float) itemView.getRight() + dX, (float) itemView.getTop(),
//                                (float) itemView.getRight(), (float) itemView.getBottom());
//                        c.drawRect(background, paint);
//
//                        icon = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.trashcan);
//                        RectF iconDest = new RectF(
//                                (float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width,
//                                (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
//                        c.drawBitmap(icon, null, iconDest, paint);
//                    }
//                } else if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
//                    final float alpha = 1.0f - Math.abs(dY) / (float) viewHolder.itemView.getHeight();
//                    viewHolder.itemView.setAlpha(alpha);
//                    viewHolder.itemView.setTranslationY(dY);
//                }
//            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final int position = viewHolder.getAdapterPosition();
                SongEntry removedSong = entryList.remove(position);
                adapter.notifyDataSetChanged();

                if (musicBound) {
                    musicService.removeSongFromQueue(removedSong, true);
                }

                queueCount.setText(String.valueOf(entryList.size()));

                createSnackbarText(position, removedSong);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        queueCount = view.findViewById(R.id.host_queue_song_count);
        queueCount.setText(String.valueOf(entryList.size()));

        return view;
    }

    private void createSnackbarText(int position, SongEntry removedSong) {
        String snackbarText = "Removed '" + getResources().getString(removedSong.name) + "'";
        Snackbar snackbar = Snackbar
                .make(recyclerView, snackbarText, Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onClick(View view) {
                        entryList.add(position, removedSong);
                        adapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(position);

                        if (musicBound) {
                            musicService.addSongToQueue(position, removedSong);
                        }
                    }
                });
        snackbar.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateList(ArrayList<SongEntry> songList) {
        entryList.clear();
        entryList.addAll(songList);
        Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
        queueCount.setText(String.valueOf(entryList.size()));
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
}