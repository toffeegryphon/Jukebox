package edu.illinois.cs465.jukebox.viewmodel;

import java.util.ArrayList;
import java.util.Random;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Button;

import edu.illinois.cs465.jukebox.R;
import edu.illinois.cs465.jukebox.SongEntry;

/*
 * This is demo code to accompany the Mobiletuts+ series:
 * Android SDK: Creating a Music Player
 *
 * Sue Smith - February 2014
 */

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    private MediaPlayer player;
    private ArrayList<SongEntry> songQueue;
    private ArrayList<SongEntry> previousSongs;
    private SongEntry currSong;
    private final IBinder musicBind = new MusicBinder();
    private boolean isMediaPlayerPrepared = false;
    private boolean initialPlay = true;
    private boolean isStopped = false;

    private ArrayList<MusicServiceListener> mListeners;

    private Random rand;

    public void onCreate() {
        super.onCreate();

        mListeners = new ArrayList<>();
        rand = new Random();
        player = new MediaPlayer();
        songQueue = new ArrayList<>();
        previousSongs = new ArrayList<>();
        isMediaPlayerPrepared = false;
        initialPlay = true;
        isStopped = false;

        initMusicPlayer();
    }

    private void createNew() {
        mListeners = new ArrayList<>();
        rand = new Random();
        songQueue = new ArrayList<>();
        previousSongs = new ArrayList<>();
        isMediaPlayerPrepared = false;
        initialPlay = true;
        isStopped = false;
    }

    public void initMusicPlayer(){
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
        player.setOnPreparedListener(this);
    }

    private SongEntry addSongListItem(int image, int song_name, int artist, int url) {
        Button deleteButton = new Button(this);
        return addSongListItem(image, song_name, artist, url, deleteButton);
    }

    private SongEntry addSongListItem(int image, int song_name, int artist, int url, Button button) {
        SongEntry item = new SongEntry(image, song_name, artist, url, button);
        songQueue.add(item);
        if (currSong == null) {
            if (songQueue.size() == 1) {
                currSong = item;
            } else {
                currSong = songQueue.get(0);
            }
        }
        return item;
    }

    public void setSongList(ArrayList<SongEntry> _songQueue){
        songQueue.clear();
        songQueue.addAll(_songQueue);

        if (!mListeners.isEmpty()) {
            for (MusicServiceListener l : mListeners) {
                l.onQueueUpdate(this.songQueue);
            }
        }
    }

    public int getSongQueueSize() {
        return songQueue.size();
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent){
        // Allow music to play on all activities
        // player.stop();
        // player.release();
        return false;
    }

    public void playSong(boolean removeFromSongQueue) {
        if (isStopped) return;

        isMediaPlayerPrepared = false;
        initialPlay = false;

        player.reset();

        if (removeFromSongQueue && !songQueue.isEmpty()) {
            SongEntry prevSong = currSong;
            currSong = songQueue.remove(0);

            // Avoid playing the same song twice in a row
            while (!songQueue.isEmpty() && songQueue.get(0).equals(prevSong)) {
                currSong = songQueue.remove(0);
            }

            songQueue.add(currSong); // Add current song to end of queue

            if (!mListeners.isEmpty()) {
                for (MusicServiceListener l : mListeners) {
                    l.onQueueUpdate(this.songQueue);
                }
            }
        }

        if (songQueue.isEmpty()) {
            if (currSong != null) {
                songQueue.add(currSong);
            } else {
                // Add default songs if users did not suggest any
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

                currSong = songQueue.remove(0);
                songQueue.add(currSong);
            }

            if (!mListeners.isEmpty()) {
                for (MusicServiceListener l : mListeners) {
                    l.onQueueUpdate(this.songQueue);
                }
            }
        }

        previousSongs.add(0, currSong);

        String title = getResources().getString(currSong.name);
        String artist = getResources().getString(currSong.artist);
        String url = getResources().getString(currSong.url);
        int imageResource = currSong.image;

        try {
            player.setDataSource(url);
        }
        catch (Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }

        player.prepareAsync();

        if (!mListeners.isEmpty()) {
            for (MusicServiceListener l : mListeners) {
                l.onMediaPlayerNewSong();
            }
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (player.getCurrentPosition() > 0) {
            mp.reset();
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.v("MUSIC PLAYER", "Playback Error");
        mp.reset();
        return false;
    }

    public SongEntry getCurrentSong() {
        return currSong;
    }

    public int getPosition() {
        if (!isStopped) {
            return player.getCurrentPosition();
        } else {
            return 0;
        }
    }

    public int getDuration() {
        if (!isStopped) {
            return player.getDuration();
        } else {
            return 0;
        }
    }

    public boolean isPlaying() {
        if (!isStopped) {
            return player.isPlaying();
        } else {
            return false;
        }
    }

    public void pausePlayer() {
        if (isStopped) return;

        player.pause();

        if (!mListeners.isEmpty()) {
            for (MusicServiceListener l : mListeners) {
                l.onMediaPlayerPause();
            }
        }
    }

    public void seekTo(int posn) {
        if (isStopped) return;

        player.seekTo(posn);
    }

    public void startPlayer() {
        if (isStopped) return;

        player.start();

        if (!mListeners.isEmpty()) {
            for (MusicServiceListener l : mListeners) {
                l.onMediaPlayerUnpause();
            }
        }
    }

    public void playPrev() {
        if (!previousSongs.isEmpty()) {
            currSong = previousSongs.remove(0);
        }
        if (player.getCurrentPosition() < 5 * 1000) { // Go to actual previous song if within first 5 seconds
            if (!previousSongs.isEmpty()) {
                currSong = previousSongs.remove(0);
            }
        }

        playSong(false);
    }

    public void playNext() {
        playSong(true);
    }

    public boolean removeSongFromQueue(int index, boolean actuallyRemove) {
        return removeSongFromQueue(songQueue.get(index), actuallyRemove);
    }

    public boolean removeSongFromQueue(SongEntry songToRemove, boolean actuallyRemove) {
        if (songQueue.size() == 1) { return false; }

        boolean removed = songQueue.remove(songToRemove);

        // Place song at end of the queue so that it's not gone forever
        if (removed && !actuallyRemove) {
            songQueue.add(songToRemove);
        }

        if (!mListeners.isEmpty()) {
            for (MusicServiceListener l : mListeners) {
                l.onQueueUpdate(this.songQueue);
            }
        }

        return removed;
    }

    public void stopMusicService() {
        isStopped = true;
        player.pause();
        isMediaPlayerPrepared = false;
        currSong = null;
        unregisterAllListeners();
        stopForeground(true);

        createNew();
    }

    @Override
    public void onDestroy() {
        isStopped = true;
        player.stop();
        player.release();
        isMediaPlayerPrepared = false;
        currSong = null;
        unregisterAllListeners();
        stopForeground(true);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        isMediaPlayerPrepared = true;

        if (!mListeners.isEmpty()) {
            for (MusicServiceListener l : mListeners) {
                l.onMediaPlayerPrepared();
                l.onMediaPlayerUnpause();
            }
        }
    }

    public boolean isMediaPlayerPrepared() {
        return isMediaPlayerPrepared;
    }

    public boolean isInitialPlay() { return initialPlay; }

    public interface MusicServiceListener {
        void onRegister(ArrayList<SongEntry> songList);
        void onMediaPlayerPrepared();
        void onMediaPlayerPause();
        void onMediaPlayerUnpause();
        void onMediaPlayerNewSong();
        void onQueueUpdate(ArrayList<SongEntry> songList);
    }

    public void registerListener(MusicServiceListener listener) {
        if (!mListeners.contains(listener)) {
            mListeners.add(listener);
            listener.onRegister(songQueue);
        }
    }

    public boolean unregisterListener(MusicServiceListener listener) {
        return mListeners.remove(listener);
    }

    private void unregisterAllListeners() {
        mListeners = new ArrayList<>();
    }
}
