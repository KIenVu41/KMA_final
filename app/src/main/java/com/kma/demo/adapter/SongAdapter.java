package com.kma.demo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.demo.databinding.ItemProgressBinding;
import com.kma.demo.databinding.ItemSongBinding;
import com.kma.demo.listener.IOnClickSongItemListener;
import com.kma.demo.data.model.Song;
import com.kma.demo.listener.OnEndlessScrollListener;
import com.kma.demo.utils.GlideUtils;

import java.util.List;

public class SongAdapter extends ListAdapter<Song, RecyclerView.ViewHolder> {

    public IOnClickSongItemListener iOnClickSongItemListener;
    public IOnClickSongItemListener iOnClickSongItemDownloadListener;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnEndlessScrollListener listener;
    private boolean isLoadingAdd = false;

    public SongAdapter(@NonNull DiffUtil.ItemCallback<Song> diffCallback, IOnClickSongItemListener iOnClickSongItemListener, IOnClickSongItemListener iOnClickSongItemDownloadListener) {
        super(diffCallback);
        this.iOnClickSongItemListener = iOnClickSongItemListener;
        this.iOnClickSongItemDownloadListener = iOnClickSongItemDownloadListener;
    }

    public void setListener(OnEndlessScrollListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case VIEW_TYPE_ITEM:
                ItemSongBinding itemSongBinding = ItemSongBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                viewHolder = new SongViewHolder(itemSongBinding);
                break;
            case VIEW_TYPE_LOADING:
                ItemProgressBinding itemProgressBinding = ItemProgressBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                viewHolder = new LoadingViewHolder(itemProgressBinding);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Song song = getItem(position);
//        if (song == null) {
//            return;
//        }
        switch (getItemViewType(position)) {
            case VIEW_TYPE_ITEM:
                SongViewHolder songViewHolder = (SongViewHolder) holder;
                if(iOnClickSongItemDownloadListener == null) {
                    songViewHolder.mItemSongBinding.imgDownload.setVisibility(View.GONE);
                }

                GlideUtils.loadUrl(song.getImage(), songViewHolder.mItemSongBinding.imgSong);
                songViewHolder.mItemSongBinding.tvSongName.setText(song.getTitle());
                songViewHolder.mItemSongBinding.tvArtist.setText(song.getArtist());
                songViewHolder.mItemSongBinding.tvCountView.setText(String.valueOf(song.getCount()));

                songViewHolder.mItemSongBinding.layoutItem.setOnClickListener(v -> iOnClickSongItemListener.onClickItemSong(song));
                songViewHolder.mItemSongBinding.imgDownload.setOnClickListener(v -> {
                    iOnClickSongItemDownloadListener.onClickItemSong(song);
                });
                break;
            case VIEW_TYPE_LOADING:
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
                loadingViewHolder.ItemProgressBinding.loadmoreProgress.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void setCallback(IOnClickSongItemListener iOnClickSongItemListener) {
        this.iOnClickSongItemListener = iOnClickSongItemListener;
        this.iOnClickSongItemDownloadListener = iOnClickSongItemListener;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == getCurrentList().size() - 1 && isLoadingAdd) {
            return VIEW_TYPE_LOADING;
        }
        return VIEW_TYPE_ITEM;
    }

    public void addFooterLoading() {
        isLoadingAdd = true;
    }

    public void removeFooterLoading() {
        isLoadingAdd = false;
        int position = getCurrentList().size() - 1;
        Song song = getItem(position);
        if(song != null) {
            getCurrentList().remove(position);
            notifyItemRemoved(position);
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        SongViewHolder songViewHolder = (SongViewHolder) holder;
        songViewHolder.mItemSongBinding.layoutItem.setOnClickListener(null);
        songViewHolder.mItemSongBinding.imgDownload.setOnClickListener(null);
    }


    public static class SongViewHolder extends RecyclerView.ViewHolder {

        private final ItemSongBinding mItemSongBinding;

        public SongViewHolder(ItemSongBinding itemSongBinding) {
            super(itemSongBinding.getRoot());
            this.mItemSongBinding = itemSongBinding;
        }
    }

    public static class LoadingViewHolder extends RecyclerView.ViewHolder {

        private final ItemProgressBinding ItemProgressBinding;

        public LoadingViewHolder(ItemProgressBinding ItemProgressBinding) {
            super(ItemProgressBinding.getRoot());
            this.ItemProgressBinding = ItemProgressBinding;
        }
    }
}
