package com.kma.demo.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.demo.databinding.ItemBannerSongBinding;
import com.kma.demo.listener.IOnClickSongItemListener;
import com.kma.demo.data.model.Song;
import com.kma.demo.utils.GlideUtils;

public class BannerSongAdapter extends ListAdapter<Song, BannerSongAdapter.BannerSongViewHolder> {

    public final IOnClickSongItemListener iOnClickSongItemListener;

    public BannerSongAdapter(@NonNull DiffUtil.ItemCallback<Song> diffCallback, IOnClickSongItemListener iOnClickSongItemListener) {
        super(diffCallback);
        this.iOnClickSongItemListener = iOnClickSongItemListener;
    }

    @NonNull
    @Override
    public BannerSongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBannerSongBinding itemBannerSongBinding = ItemBannerSongBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BannerSongViewHolder(itemBannerSongBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerSongViewHolder holder, int position) {
        Song song = getItem(position);
        if (song == null) {
            return;
        }
        GlideUtils.loadUrlBanner(song.getImage(), holder.mItemBannerSongBinding.imageBanner);
        holder.mItemBannerSongBinding.layoutItem.setOnClickListener(v -> iOnClickSongItemListener.onClickItemSong(song));
    }

    public static class BannerSongViewHolder extends RecyclerView.ViewHolder {

        private final ItemBannerSongBinding mItemBannerSongBinding;

        public BannerSongViewHolder(@NonNull ItemBannerSongBinding itemBannerSongBinding) {
            super(itemBannerSongBinding.getRoot());
            this.mItemBannerSongBinding = itemBannerSongBinding;
        }
    }
}
