package com.example.saveat.model;

public class Ingredient {
    private String id;
    private String name;
    private String category;
    private int imageRes;

    public Ingredient(String id, String name, String category, int imageRes) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.imageRes = imageRes;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public int getImageRes() {
        return imageRes;
    }
}