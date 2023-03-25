package com.kma.demo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.demo.R;
import com.kma.demo.databinding.ItemSongPlayingBinding;
import com.kma.demo.listener.IOnClickSongPlayingItemListener;
import com.kma.demo.data.model.Song;
import com.kma.demo.utils.GlideUtils;

public class SongPlayingAdapter extends ListAdapter<Song, SongPlayingAdapter.SongPlayingViewHolder> {

    public final IOnClickSongPlayingItemListener iOnClickSongPlayingItemListener;

    public SongPlayingAdapter(@NonNull DiffUtil.ItemCallback<Song> diffCallback, IOnClickSongPlayingItemListener iOnClickSongPlayingItemListener) {
        super(diffCallback);
        this.iOnClickSongPlayingItemListener = iOnClickSongPlayingItemListener;
    }

    @NonNull
    @Override
    public SongPlayingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSongPlayingBinding itemSongPlayingBinding = ItemSongPlayingBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SongPlayingViewHolder(itemSongPlayingBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull SongPlayingViewHolder holder, int position) {
        Song song = getItem(position);
        if (song == null) {
            return;
        }
        if (song.isPlaying()) {
            holder.mItemSongPlayingBinding.layoutItem.setBackgroundResource(R.color.background_bottom);
            holder.mItemSongPlayingBinding.imgPlaying.setVisibility(View.VISIBLE);
        } else {
            holder.mItemSongPlayingBinding.layoutItem.setBackgroundResource(R.color.white);
            holder.mItemSongPlayingBinding.imgPlaying.setVisibility(View.GONE);
        }
        GlideUtils.loadUrl(song.getImage(), holder.mItemSongPlayingBinding.imgSong);
        holder.mItemSongPlayingBinding.tvSongName.setText(song.getTitle());
        holder.mItemSongPlayingBinding.tvArtist.setText(song.getArtist());

        holder.mItemSongPlayingBinding.layoutItem.setOnClickListener(v
                -> iOnClickSongPlayingItemListener.onClickItemSongPlaying(holder.getAdapterPosition()));
    }

    public static class SongPlayingViewHolder extends RecyclerView.ViewHolder {

        private final ItemSongPlayingBinding mItemSongPlayingBinding;

        public SongPlayingViewHolder(ItemSongPlayingBinding itemSongPlayingBinding) {
            super(itemSongPlayingBinding.getRoot());
            this.mItemSongPlayingBinding = itemSongPlayingBinding;
        }
    }
}
