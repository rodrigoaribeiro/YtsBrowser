package com.digao.ytsbrowser.Model;

import java.util.ArrayList;

public class Movie {
    private int id;
    private String title;
    private String cover; //SmalCover
    private String year;
    private String language;
    private String genre;
    private String rating;
    private String imdbId;
    private int seeds3d;
    private int seeds780p;
    private int seeds1080p;
    private String hash;
    private String sinopse;
    private ArrayList<Torrent> torrents;

    public Movie() {
        torrents = new ArrayList<Torrent>();
    }

    public String getSinopse() {
        return sinopse;
    }

    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    public void addTorrent(Torrent tor) {
        torrents.add(tor);
    }

    public Torrent getTorrent(int index) {
        return torrents.get(index);
    }

    public void deleteTorrent(int index) {
        torrents.remove(index);
    }

    public int countTorrents() {
        return torrents.size();
    }

    public int indexOfTorrent(String quality) {
        int result = -1;
        for (int i = 0; i < torrents.size(); i++) {
            if (torrents.get(i).getQuality().equals(quality)  ) {
                result = i;
            }
        }
        return result;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public int getSeeds3d() {
        return seeds3d;
    }

    public void setSeeds3d(int seeds3d) {
        this.seeds3d = seeds3d;
    }

    public int getSeeds780p() {
        return seeds780p;
    }

    public void setSeeds780p(int seeds780p) {
        this.seeds780p = seeds780p;
    }

    public int getSeeds1080p() {
        return seeds1080p;
    }

    public void setSeeds1080p(int seeds1080p) {
        this.seeds1080p = seeds1080p;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

}

