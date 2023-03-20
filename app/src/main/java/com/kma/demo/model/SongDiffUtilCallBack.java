package com.kma.demo.model;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

public class SongDiffUtilCallBack extends DiffUtil.ItemCallback<Song> {

    @Override
    public boolean areItemsTheSame(@NonNull Song oldItem, @NonNull Song newItem) {
        return oldItem.getId() == newItem.getId();
    }

    @Override
    public boolean areContentsTheSame(@NonNull Song oldItem, @NonNull Song newItem) {
        return oldItem.equals(newItem);
    }
}
