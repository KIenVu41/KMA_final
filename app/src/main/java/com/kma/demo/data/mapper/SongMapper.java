package com.kma.demo.data.mapper;

import com.kma.demo.data.local.entity.SongEntity;
import com.kma.demo.data.model.Song;

public class SongMapper {
    private static SongMapper INSTANCE;

    public static SongMapper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SongMapper();
        }
        return INSTANCE;
    }
    public SongEntity toEntity(Song song) {
        SongEntity songEntity = new SongEntity();
        songEntity.setDocId(song.getDocId());
        songEntity.setArtist(song.getArtist());
        songEntity.setCount(song.getCount());
        songEntity.setTitle(song.getTitle());
        songEntity.setFeatured(song.isFeatured());
        songEntity.setImage(song.getImage());
        songEntity.setLatest(song.isLatest());
        songEntity.setUrl(song.getUrl());
        return songEntity;
    }
    public Song toDTO(SongEntity songEntity) {
        Song song = new Song();
        song.setDocId(songEntity.getDocId());
        song.setArtist(songEntity.getArtist());
        song.setCount(songEntity.getCount());
        song.setTitle(songEntity.getTitle());
        song.setFeatured(songEntity.isFeatured());
        song.setImage(songEntity.getImage());
        song.setLatest(songEntity.isLatest());
        song.setUrl(songEntity.getUrl());
        return song;
    }
}