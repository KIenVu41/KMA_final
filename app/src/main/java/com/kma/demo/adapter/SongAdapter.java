package com.kma.demo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.demo.databinding.ItemSongBinding;
import com.kma.demo.listener.IOnClickSongItemListener;
import com.kma.demo.data.model.Song;
import com.kma.demo.utils.GlideUtils;

import java.util.List;

public class SongAdapter extends ListAdapter<Song, SongAdapter.SongViewHolder> {

    private  List<Song> mListSongs;
    public final IOnClickSongItemListener iOnClickSongItemListener;
    public final IOnClickSongItemListener iOnClickSongItemDownloadListener;

    public SongAdapter(@NonNull DiffUtil.ItemCallback<Song> diffCallback, IOnClickSongItemListener iOnClickSongItemListener, IOnClickSongItemListener iOnClickSongItemDownloadListener) {
        super(diffCallback);
        this.iOnClickSongItemListener = iOnClickSongItemListener;
        this.iOnClickSongItemDownloadListener = iOnClickSongItemDownloadListener;
    }

//    public SongAdapter(List<Song> mListSongs, IOnClickSongItemListener iOnClickSongItemListener, IOnClickSongItemListener iOnClickSongItemDownloadListener) {
//        this.mListSongs = mListSongs;
//        this.iOnClickSongItemListener = iOnClickSongItemListener;
//        this.iOnClickSongItemDownloadListener = iOnClickSongItemDownloadListener;
//    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSongBinding itemSongBinding = ItemSongBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SongViewHolder(itemSongBinding);
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

//    @Override
//    public int getItemCount() {
//        return null == mListSongs ? 0 : mListSongs.size();
//    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {

        private final ItemSongBinding mItemSongBinding;

        public SongViewHolder(ItemSongBinding itemSongBinding) {
            super(itemSongBinding.getRoot());
            this.mItemSongBinding = itemSongBinding;
        }
    }
}
