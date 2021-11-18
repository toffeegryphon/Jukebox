package edu.illinois.cs465.jukebox;

import android.widget.Button;

public class EntryItem {
    public int image;
    public String name;
    public String artist;
    public Button button;

    public EntryItem(int image, String name, String artist, Button button) {
        this.image = image;
        this.name = name;
        this.artist = artist;
        this.button = button;
    }
}
