package edu.illinois.cs465.jukebox;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Objects;

public class GuestSuggestionActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_suggestion);

        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }

        recyclerView = findViewById(R.id.guest_suggestion_recycler_view);
        RecyclerViewCustomEdgeDecorator decoration = new RecyclerViewCustomEdgeDecorator(0, 0, true, false);
        recyclerView.addItemDecoration(decoration);

        entryList = new ArrayList<SongEntry>();

        adapter = new RecyclerViewAdapter(this, entryList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerListener = new RecyclerViewAdapter.RecyclerViewListener() {
            @Override
            public void onDeleteButtonPressed(RecyclerViewAdapter.ViewHolder holder, int _pos, SongEntry removedSong) {

                // TODO: Get host settings' suggestion limit instead of hardcoding '10'
                String newSuggestionCount = String.valueOf(entryList.size()) + " / " + "10";
                suggestionCount.setText(newSuggestionCount);

                createUndoSnackbarText(_pos, removedSong);
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
                int position = viewHolder.getAdapterPosition();
                SongEntry removedSong = entryList.remove(position);
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(position, entryList.size());

                // TODO: Get host settings' suggestion limit instead of hardcoding '10'
                String newSuggestionCount = String.valueOf(entryList.size()) + " / " + "10";
                suggestionCount.setText(newSuggestionCount);

                createUndoSnackbarText(position, removedSong);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // Initialize Guest suggestions
        addSongListItem(R.drawable.songcover_onandon, R.string.songcover_name1, R.string.songcover_artist1, R.string.songcover_url1);
        addSongListItem(R.drawable.songcover_heroestonight, R.string.songcover_name2, R.string.songcover_artist2, R.string.songcover_url2);
        addSongListItem(R.drawable.songcover_invincible, R.string.songcover_name3, R.string.songcover_artist3, R.string.songcover_url3);
        addSongListItem(R.drawable.songcover_myheart, R.string.songcover_name4, R.string.songcover_artist4, R.string.songcover_url4);
        addSongListItem(R.drawable.songcover_blank, R.string.songcover_name5, R.string.songcover_artist5, R.string.songcover_url5);
        addSongListItem(R.drawable.songcover_symbolism, R.string.songcover_name6, R.string.songcover_artist6, R.string.songcover_url6);
        addSongListItem(R.drawable.songcover_whywelose, R.string.songcover_name7, R.string.songcover_artist7, R.string.songcover_url7);
        addSongListItem(R.drawable.songcover_cradles, R.string.songcover_name8, R.string.songcover_artist8, R.string.songcover_url8);
        addSongListItem(R.drawable.songcover_shine, R.string.songcover_name9, R.string.songcover_artist9, R.string.songcover_url9);
        addSongListItem(R.drawable.songcover_invisible, R.string.songcover_name10, R.string.songcover_artist10, R.string.songcover_url10);

        submitButton = findViewById(R.id.guest_suggestion_submit_button);
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

        suggestionCount = findViewById(R.id.guestSuggestionCount);
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
        String newSuggestionCount = String.valueOf(entryList.size()) + " / " + "10";
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
        addSongListItem(image, song_name, artist, url, new Button(this));
    }

    public void addSongListItem(int image, int song_name, int artist, int url, Button button) {
        SongEntry item = new SongEntry(image, song_name, artist, url, button);
        entryList.add(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}