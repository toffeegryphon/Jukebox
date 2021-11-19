package edu.illinois.cs465.jukebox;

import android.widget.ImageView;

public class SongModel {
    public int image;
    public int name;
    public int artist;

    public SongModel(int image, int name, int artist) {
        this.image = image;
        this.name = name;
        this.artist = artist;
    }
}
