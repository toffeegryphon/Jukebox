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

    private ArrayList<MusicServiceListener> mListeners;

    private Random rand;

    public void onCreate() {
        super.onCreate();

        mListeners = new ArrayList<>();
        rand = new Random();
        player = new MediaPlayer();
        songQueue = new ArrayList<>();
        previousSongs = new ArrayList<>();

        initMusicPlayer();
    }

    public void initMusicPlayer(){
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
        player.setOnPreparedListener(this);
    }

    public void setSongList(ArrayList<SongEntry> songQueue){
        this.songQueue = songQueue;

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
        player.reset();

        if (removeFromSongQueue) {
            currSong = songQueue.remove(0);

            // Avoid playing the same song twice in a row
            while (songQueue.get(0).equals(currSong)) {
                currSong = songQueue.remove(0);
            }

            songQueue.add(currSong); // Add current song to end of queue

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
        return player.getCurrentPosition();
    }

    public int getDuration() {
        return player.getDuration();
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    public void pausePlayer() {
        player.pause();

        if (!mListeners.isEmpty()) {
            for (MusicServiceListener l : mListeners) {
                l.onMediaPlayerPause();
            }
        }
    }

    public void seekTo(int posn) {
        player.seekTo(posn);
    }

    public void startPlayer() {
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

    public boolean removeSongFromQueue(int index) {
        return removeSongFromQueue(songQueue.get(index));
    }

    public boolean removeSongFromQueue(SongEntry songToRemove) {
        boolean removed = songQueue.remove(songToRemove);

        // Place song at end of the queue so that it's not gone forever
        if (removed) {
            songQueue.add(songToRemove);
        }

        if (!mListeners.isEmpty()) {
            for (MusicServiceListener l : mListeners) {
                l.onQueueUpdate(this.songQueue);
            }
        }

        return removed;
    }

    @Override
    public void onDestroy() {
        player.stop();
        player.release();
        unregisterAllListeners();
        stopForeground(true);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();

        if (!mListeners.isEmpty()) {
            for (MusicServiceListener l : mListeners) {
                l.onMediaPlayerPrepared();
                l.onMediaPlayerUnpause();
            }
        }
    }

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
        mListeners = null;
    }
}
