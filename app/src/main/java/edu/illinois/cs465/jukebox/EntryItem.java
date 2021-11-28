package edu.illinois.cs465.jukebox;

import android.widget.Button;

public class EntryItem {
    public int image;
    public int name;
    public int artist;
    public int url;
    public Button button;

    public EntryItem(int image, int name, int artist, int url, Button button) {
        this.image = image;
        this.name = name;
        this.artist = artist;
        this.url = url;
        this.button = button;
    }
}
