package com.kma.demo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.demo.data.model.SongDiffUtilCallBack;
import com.kma.demo.databinding.ItemProgressBinding;
import com.kma.demo.databinding.ItemSongBinding;
import com.kma.demo.listener.IOnClickSongItemListener;
import com.kma.demo.data.model.Song;
import com.kma.demo.listener.OnEndlessScrollListener;
import com.kma.demo.utils.GlideUtils;

import java.util.List;
import java.util.concurrent.Executors;

public class SongAdapter extends ListAdapter<Song, SongAdapter.SongViewHolder> {

    public IOnClickSongItemListener iOnClickSongItemListener;
    public IOnClickSongItemListener iOnClickSongItemDownloadListener;

    public SongAdapter(@NonNull DiffUtil.ItemCallback<Song> diffCallback, IOnClickSongItemListener iOnClickSongItemListener, IOnClickSongItemListener iOnClickSongItemDownloadListener) {
        //super(diffCallback);
        super(new AsyncDifferConfig.Builder<Song>(new SongDiffUtilCallBack()).setBackgroundThreadExecutor(Executors.newSingleThreadExecutor()).build());
        this.iOnClickSongItemListener = iOnClickSongItemListener;
        this.iOnClickSongItemDownloadListener = iOnClickSongItemDownloadListener;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SongViewHolder(ItemSongBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = getItem(position);
        if (song == null) {
            return;
        }

        if(iOnClickSongItemDownloadListener == null) {
            holder.mItemSongBinding.imgDownload.setVisibility(View.GONE);
        }

        GlideUtils.loadUrl(song.getImage(), holder.mItemSongBinding.imgSong);
        holder.mItemSongBinding.tvSongName.setText(song.getTitle());
        holder.mItemSongBinding.tvArtist.setText(song.getArtist());
        holder.mItemSongBinding.tvCountView.setText(String.valueOf(song.getCount()));

        holder.mItemSongBinding.layoutItem.setOnClickListener(v -> iOnClickSongItemListener.onClickItemSong(song));
        holder.mItemSongBinding.imgDownload.setOnClickListener(v -> {
                    iOnClickSongItemDownloadListener.onClickItemSong(song);
                });

    }

    public void setCallback(IOnClickSongItemListener iOnClickSongItemListener) {
        this.iOnClickSongItemListener = iOnClickSongItemListener;
        this.iOnClickSongItemDownloadListener = iOnClickSongItemListener;
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull SongViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.mItemSongBinding.layoutItem.setOnClickListener(null);
        holder.mItemSongBinding.imgDownload.setOnClickListener(null);
    }


    public static class SongViewHolder extends RecyclerView.ViewHolder {

        private final ItemSongBinding mItemSongBinding;

        public SongViewHolder(ItemSongBinding itemSongBinding) {
            super(itemSongBinding.getRoot());
            this.mItemSongBinding = itemSongBinding;
        }
    }
}
