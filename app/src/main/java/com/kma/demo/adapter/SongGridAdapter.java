package com.kma.demo.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.demo.databinding.ItemSongGridBinding;
import com.kma.demo.listener.IOnClickSongItemListener;
import com.kma.demo.model.Song;
import com.kma.demo.utils.GlideUtils;

import java.util.List;

public class SongGridAdapter extends ListAdapter<Song, SongGridAdapter.SongGridViewHolder> {

    public final IOnClickSongItemListener iOnClickSongItemListener;

    public SongGridAdapter(@NonNull DiffUtil.ItemCallback<Song> diffCallback, IOnClickSongItemListener iOnClickSongItemListener) {
        super(diffCallback);
        this.iOnClickSongItemListener = iOnClickSongItemListener;
    }

    @NonNull
    @Override
    public SongGridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSongGridBinding itemSongGridBinding = ItemSongGridBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SongGridViewHolder(itemSongGridBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull SongGridViewHolder holder, int position) {
        Song song = getItem(position);
        if (song == null) {
            return;
        }
        GlideUtils.loadUrl(song.getImage(), holder.mItemSongGridBinding.imgSong);
        holder.mItemSongGridBinding.tvSongName.setText(song.getTitle());
        holder.mItemSongGridBinding.tvArtist.setText(song.getArtist());

        holder.mItemSongGridBinding.layoutItem.setOnClickListener(v -> iOnClickSongItemListener.onClickItemSong(song));
    }

    public static class SongGridViewHolder extends RecyclerView.ViewHolder {

        private final ItemSongGridBinding mItemSongGridBinding;

        public SongGridViewHolder(ItemSongGridBinding itemSongGridBinding) {
            super(itemSongGridBinding.getRoot());
            this.mItemSongGridBinding = itemSongGridBinding;
        }
    }
}
