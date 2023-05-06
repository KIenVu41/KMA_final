package com.kma.demo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.recyclerview.widget.RecyclerView;

import com.kma.demo.databinding.ItemSongBinding;
import com.kma.demo.listener.IOnClickSongItemListener;
import com.kma.demo.model.Song;
import com.kma.demo.utils.GlideUtils;

import java.util.List;

public class SongBaseAdapter extends BaseAdapter {
    private List<Song> mListSongs;

    public final IOnClickSongItemListener iOnClickSongItemListener;
    public final IOnClickSongItemListener iOnClickSongItemDownloadListener;

    public SongBaseAdapter(List<Song> mListSongs, IOnClickSongItemListener iOnClickSongItemListener, IOnClickSongItemListener iOnClickSongItemDownloadListener) {
        this.mListSongs = mListSongs;
        this.iOnClickSongItemListener = iOnClickSongItemListener;
        this.iOnClickSongItemDownloadListener = iOnClickSongItemDownloadListener;
    }

    @Override
    public int getCount() {
        if(mListSongs == null) {
            return 0;
        }
        return mListSongs.size();
    }

    @Override
    public Object getItem(int position) {
        return mListSongs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseViewHolder holder;
        if(convertView == null) {
            ItemSongBinding itemSongBinding = ItemSongBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            holder = new BaseViewHolder(itemSongBinding);
            holder.view = itemSongBinding.getRoot();
            holder.view.setTag(holder);
        } else {
            holder = (BaseViewHolder) convertView.getTag();
        }

        if(iOnClickSongItemDownloadListener == null) {
            holder.mItemSongBinding.imgDownload.setVisibility(View.GONE);
        }

        Song song = mListSongs.get(position);
        GlideUtils.loadUrl(song.getImage(), holder.mItemSongBinding.imgSong);
        holder.mItemSongBinding.tvSongName.setText(song.getTitle());
        holder.mItemSongBinding.tvArtist.setText(song.getArtist());
        holder.mItemSongBinding.tvCountView.setText(String.valueOf(song.getCount()));

        holder.mItemSongBinding.layoutItem.setOnClickListener(v -> iOnClickSongItemListener.onClickItemSong(song));
        holder.mItemSongBinding.imgDownload.setOnClickListener(v -> {
            iOnClickSongItemDownloadListener.onClickItemSong(song);
        });
        return holder.view;
    }

    private static class BaseViewHolder {
        private View view;
        private ItemSongBinding mItemSongBinding;

        BaseViewHolder(ItemSongBinding mItemSongBinding) {
            this.view = mItemSongBinding.getRoot();
            this.mItemSongBinding = mItemSongBinding;
        }
    }
}
