package edu.illinois.cs465.jukebox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.illinois.cs465.jukebox.viewmodel.MusicService;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    ArrayList<SongEntry> data_entry_list;

    private ArrayList<RecyclerViewListener> mListeners;

    public RecyclerViewAdapter(Context mContext, ArrayList<SongEntry> entryList) {
        this.mContext = mContext;
        this.data_entry_list = entryList;
        mListeners = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_song, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SongEntry entry = data_entry_list.get(position);
        holder.image.setImageResource(entry.image);
        holder.song.setText(entry.name);
        holder.artist.setText(entry.artist);
        //holder.button.(data_buttons.get(position));
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int _pos = holder.getAdapterPosition();
                data_entry_list.remove(_pos);
                notifyItemRemoved(_pos);
                notifyItemRangeChanged(_pos, data_entry_list.size());

                if (!mListeners.isEmpty()) {
                    for (RecyclerViewListener l : mListeners) {
                        l.onDeleteButtonPressed(_pos);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data_entry_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView song;
        TextView artist;
        Button button;
        FrameLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.fragment_song_cover);
            song = itemView.findViewById(R.id.fragment_song_name);
            artist = itemView.findViewById(R.id.fragment_song_artist);
            button = itemView.findViewById(R.id.fragment_song_button);
            parentLayout = itemView.findViewById(R.id.fragment_song_parent_layout);
        }
    }

    public interface RecyclerViewListener {
        void onDeleteButtonPressed(int _pos);
    }

    public void registerListener(RecyclerViewListener listener) {
        if (!mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    public boolean unregisterListener(RecyclerViewListener listener) {
        return mListeners.remove(listener);
    }
}
