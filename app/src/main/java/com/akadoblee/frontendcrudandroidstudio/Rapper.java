package com.akadoblee.frontendcrudandroidstudio;

import java.io.Serializable;

public class Rapper implements Serializable {
    private int id;
    private String aka;
    private String name;
    private String album;
    private String song;

    public Rapper(int id, String aka, String name, String album, String song) {
        this.id = id;
        this.aka = aka;
        this.name = name;
        this.album = album;
        this.song = song;
    }

    // Getters
    public int getId() { return id; }
    public String getAka() { return aka; }
    public String getName() { return name; }
    public String getAlbum() { return album; }
    public String getSong() { return song; }
}