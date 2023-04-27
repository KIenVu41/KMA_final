package com.kma.demo.data.mapper;

import com.kma.demo.data.local.entity.AllEntity;
import com.kma.demo.data.local.entity.FeaturedEntity;
import com.kma.demo.data.local.entity.LatestEntity;
import com.kma.demo.data.local.entity.PopularEntity;
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
    public SongEntity toEntity(Song song, int page, int type, long createAt) {
        SongEntity songEntity = new SongEntity();
        songEntity.setId(song.getId());
        songEntity.setDocId(song.getDocId());
        songEntity.setArtist(song.getArtist());
        songEntity.setCount(song.getCount());
        songEntity.setTitle(song.getTitle());
        songEntity.setFeatured(song.isFeatured());
        songEntity.setImage(song.getImage());
        songEntity.setLatest(song.isLatest());
        songEntity.setUrl(song.getUrl());
        songEntity.setPage(page);
        songEntity.setType(type);
        songEntity.setCreateAt(createAt);
        return songEntity;
    }
    public Song toDTO(SongEntity songEntity) {
        Song song = new Song();
        song.setId(songEntity.getId());
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

    public AllEntity toAllEntity(Song song, int page, int type, long createAt) {
        AllEntity songEntity = new AllEntity();
        songEntity.setId(song.getId());
        songEntity.setDocId(song.getDocId());
        songEntity.setArtist(song.getArtist());
        songEntity.setCount(song.getCount());
        songEntity.setTitle(song.getTitle());
        songEntity.setFeatured(song.isFeatured());
        songEntity.setImage(song.getImage());
        songEntity.setLatest(song.isLatest());
        songEntity.setUrl(song.getUrl());
        songEntity.setPage(page);
        songEntity.setType(type);
        songEntity.setCreateAt(createAt);
        return songEntity;
    }
    public Song toDTO(AllEntity songEntity) {
        Song song = new Song();
        song.setId(songEntity.getId());
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

    public FeaturedEntity toFeaturedEntity(Song song, int page, int type, long createAt) {
        FeaturedEntity songEntity = new FeaturedEntity();
        songEntity.setId(song.getId());
        songEntity.setDocId(song.getDocId());
        songEntity.setArtist(song.getArtist());
        songEntity.setCount(song.getCount());
        songEntity.setTitle(song.getTitle());
        songEntity.setFeatured(song.isFeatured());
        songEntity.setImage(song.getImage());
        songEntity.setLatest(song.isLatest());
        songEntity.setUrl(song.getUrl());
        songEntity.setPage(page);
        songEntity.setType(type);
        songEntity.setCreateAt(createAt);
        return songEntity;
    }
    public Song toDTO(FeaturedEntity songEntity) {
        Song song = new Song();
        song.setId(songEntity.getId());
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

    public LatestEntity toLatestEntity(Song song, int page, int type, long createAt) {
        LatestEntity songEntity = new LatestEntity();
        songEntity.setId(song.getId());
        songEntity.setDocId(song.getDocId());
        songEntity.setArtist(song.getArtist());
        songEntity.setCount(song.getCount());
        songEntity.setTitle(song.getTitle());
        songEntity.setFeatured(song.isFeatured());
        songEntity.setImage(song.getImage());
        songEntity.setLatest(song.isLatest());
        songEntity.setUrl(song.getUrl());
        songEntity.setPage(page);
        songEntity.setType(type);
        songEntity.setCreateAt(createAt);
        return songEntity;
    }
    public Song toDTO(LatestEntity songEntity) {
        Song song = new Song();
        song.setId(songEntity.getId());
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

    public PopularEntity toPopularEntity(Song song, int page, int type, long createAt) {
        PopularEntity songEntity = new PopularEntity();
        songEntity.setId(song.getId());
        songEntity.setDocId(song.getDocId());
        songEntity.setArtist(song.getArtist());
        songEntity.setCount(song.getCount());
        songEntity.setTitle(song.getTitle());
        songEntity.setFeatured(song.isFeatured());
        songEntity.setImage(song.getImage());
        songEntity.setLatest(song.isLatest());
        songEntity.setUrl(song.getUrl());
        songEntity.setPage(page);
        songEntity.setType(type);
        songEntity.setCreateAt(createAt);
        return songEntity;
    }
    public Song toDTO(PopularEntity songEntity) {
        Song song = new Song();
        song.setId(songEntity.getId());
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
