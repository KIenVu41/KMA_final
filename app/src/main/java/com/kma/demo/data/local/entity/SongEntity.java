package com.kma.demo.data.local.entity;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "song")
public class SongEntity{
    @PrimaryKey
    private int id;
    private String docId;
    private String title;
    private String image;
    private String url;
    private String artist;
    private boolean latest;
    private boolean featured;
    private int count;
    private int page;
    private int type;
    @ColumnInfo(name = "created_at")
    private long createAt;

    public SongEntity() {
    }

    public SongEntity(int id, String docId, String title, String image, String url, String artist, boolean latest, boolean featured, int count, int page, int type, long createAt) {
        this.id = id;
        this.docId = docId;
        this.title = title;
        this.image = image;
        this.url = url;
        this.artist = artist;
        this.latest = latest;
        this.featured = featured;
        this.count = count;
        this.page = page;
        this.type = type;
        this.createAt = createAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public boolean isLatest() {
        return latest;
    }

    public void setLatest(boolean latest) {
        this.latest = latest;
    }

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    @Override
    public String toString() {
        return "SongEntity{" +
                "id=" + id +
                ", docId='" + docId + '\'' +
                ", title='" + title + '\'' +
                ", image='" + image + '\'' +
                ", url='" + url + '\'' +
                ", artist='" + artist + '\'' +
                ", latest=" + latest +
                ", featured=" + featured +
                ", count=" + count +
                ", page=" + page +
                ", type=" + type +
                '}';
    }
}
