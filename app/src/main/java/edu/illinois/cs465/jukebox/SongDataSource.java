package edu.illinois.cs465.jukebox;

import java.util.ArrayList;
import java.util.List;

import edu.illinois.cs465.jukebox.SongModel;

public class SongDataSource {
    List<SongModel> list;

    public SongDataSource() {
        List<SongModel> list = new ArrayList<SongModel>();
        // added in this order for maximum visual pleasingness
        list.add(new SongModel(R.drawable.songcover_kda, R.string.songcover_name6, R.string.songcover_artist6));
        list.add(new SongModel(R.drawable.songcover_nonstop, R.string.songcover_name7, R.string.songcover_artist7));
        list.add(new SongModel(R.drawable.songcover_shivers, R.string.songcover_name8, R.string.songcover_artist8));
        list.add(new SongModel(R.drawable.songcover_squid, R.string.songcover_name9, R.string.songcover_artist9));
        list.add(new SongModel(R.drawable.songcover_wire, R.string.songcover_name10, R.string.songcover_artist10));
        list.add(new SongModel(R.drawable.songcover_aot, R.string.songcover_name1, R.string.songcover_artist1));
        list.add(new SongModel(R.drawable.songcover_bb, R.string.songcover_name2, R.string.songcover_artist2));
        list.add(new SongModel(R.drawable.songcover_csgo, R.string.songcover_name3, R.string.songcover_artist3));
        list.add(new SongModel(R.drawable.songcover_edamame, R.string.songcover_name4, R.string.songcover_artist4));
        list.add(new SongModel(R.drawable.songcover_enemy, R.string.songcover_name5, R.string.songcover_artist5));
        this.list = list;
    }
}
