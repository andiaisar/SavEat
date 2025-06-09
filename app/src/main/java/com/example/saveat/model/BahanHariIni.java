package com.example.saveat.model;

import java.util.Date;

public class BahanHariIni {
    private long id;
    private String nama;
    private int jumlah;
    private String satuan;
    private Date kadaluarsa;
    private String imagePath;
    private String category;

    public BahanHariIni(long id, String nama, int jumlah, String satuan, Date kadaluarsa, String imagePath, String category) {
        this.id = id;
        this.nama = nama;
        this.jumlah = jumlah;
        this.satuan = satuan;
        this.kadaluarsa = kadaluarsa;
        this.imagePath = imagePath;
        this.category = category;
    }

    // Getters
    public long getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public int getJumlah() {
        return jumlah;
    }

    public String getSatuan() {
        return satuan;
    }

    public Date getKadaluarsa() {
        return kadaluarsa;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getCategory() {
        return category;
    }

    // Setters
    public void setId(long id) {
        this.id = id;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    public void setKadaluarsa(Date kadaluarsa) {
        this.kadaluarsa = kadaluarsa;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}