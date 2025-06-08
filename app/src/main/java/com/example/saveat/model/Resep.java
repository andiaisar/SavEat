package com.example.saveat.model;

import java.util.List;

public class Resep {
    private String id;
    private String nama;
    private String daerah;
    private String gambarUrl;
    private List<String> bahan;

    // Constructor, getter, setter
    public Resep(String id, String nama, String daerah, String gambarUrl, List<String> bahan) {
        this.id = id;
        this.nama = nama;
        this.daerah = daerah;
        this.gambarUrl = gambarUrl;
        this.bahan = bahan;
    }

    // Getter methods
    public String getId() { return id; }
    public String getNama() { return nama; }
    public String getDaerah() { return daerah; }
    public String getGambarUrl() { return gambarUrl; }
    public List<String> getBahan() { return bahan; }
}
