package edu.illinois.cs465.jukebox.viewmodel;

import java.util.ArrayList;
import java.util.Random;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Button;

import edu.illinois.cs465.jukebox.EntryItem;
import edu.illinois.cs465.jukebox.R;

/*
 * This is demo code to accompany the Mobiletuts+ series:
 * Android SDK: Creating a Music Player
 *
 * Sue Smith - February 2014
 */

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    private MediaPlayer player;
    private ArrayList<EntryItem> songQueue;
    private ArrayList<EntryItem> previousSongs;
    private EntryItem currSong;
    private final IBinder musicBind = new MusicBinder();
    private static final int NOTIFY_ID = 1;

    private Random rand;

    public void onCreate() {
        super.onCreate();

        rand = new Random();
        player = new MediaPlayer();
        songQueue = new ArrayList<>();
        previousSongs = new ArrayList<>();

        initMusicPlayer();
    }

    public void initMusicPlayer(){
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);

        addSongQueueListItem(R.drawable.songcover_onandon, R.string.songcover_name1, R.string.songcover_artist1, R.string.songcover_url1);
        addSongQueueListItem(R.drawable.songcover_heroestonight, R.string.songcover_name2, R.string.songcover_artist2, R.string.songcover_url2);
        addSongQueueListItem(R.drawable.songcover_invincible, R.string.songcover_name3, R.string.songcover_artist3, R.string.songcover_url3);
        addSongQueueListItem(R.drawable.songcover_myheart, R.string.songcover_name4, R.string.songcover_artist4, R.string.songcover_url4);
        addSongQueueListItem(R.drawable.songcover_blank, R.string.songcover_name5, R.string.songcover_artist5, R.string.songcover_url5);
        addSongQueueListItem(R.drawable.songcover_symbolism, R.string.songcover_name6, R.string.songcover_artist6, R.string.songcover_url6);
        addSongQueueListItem(R.drawable.songcover_whywelose, R.string.songcover_name7, R.string.songcover_artist7, R.string.songcover_url7);
        addSongQueueListItem(R.drawable.songcover_cradles, R.string.songcover_name8, R.string.songcover_artist8, R.string.songcover_url8);
        addSongQueueListItem(R.drawable.songcover_shine, R.string.songcover_name9, R.string.songcover_artist9, R.string.songcover_url9);
        addSongQueueListItem(R.drawable.songcover_invisible, R.string.songcover_name10, R.string.songcover_artist10, R.string.songcover_url10);
    }

    public void addSongQueueListItem(int image, int song_name, int artist, int url) {
        addSongQueueListItem(image, song_name, artist, url, new Button(this));
    }

    public void addSongQueueListItem(int image, int song_name, int artist, int url, Button button) {
        EntryItem item = new EntryItem(image, song_name, artist, url, button);
        songQueue.add(item);
    }

    public void getSongList(ArrayList<EntryItem> songQueue){
        this.songQueue = songQueue;
    }

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }

    public void playSong(boolean removeFromSongQueue) {
        player.reset();

        if (removeFromSongQueue) {
            currSong = songQueue.remove(0);
            songQueue.add(currSong); // Add current song to end of queue
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
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (player.getCurrentPosition() > 0) {
            mp.reset();
            playSong(true);
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.v("MUSIC PLAYER", "Playback Error");
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();

//            Intent notIntent = new Intent(this, MainActivity.class);
//            notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            PendingIntent pendInt = PendingIntent.getActivity(this, 0,
//                    notIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//            Notification.Builder builder = new Notification.Builder(this);
//
//            builder.setContentIntent(pendInt)
//                    .setSmallIcon(R.drawable.play)
//                    .setTicker(songTitle)
//                    .setOngoing(true)
//                    .setContentTitle("Playing")
//                    .setContentText(songTitle);
//            Notification not = builder.build();
//            startForeground(NOTIFY_ID, not);
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
    }

    public void seekTo(int posn) {
        player.seekTo(posn);
    }

    public void playerStart() {
        player.start();
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

    @Override
    public void onDestroy() {
        stopForeground(true);
    }
}
