package com.example.saveat;

public class IntroItem {
    private String title;
    private String description;
    private String level;
    private int image;

    public IntroItem(String title, String description, String level, int image) {
        this.title = title;
        this.description = description;
        this.level = level;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }

    public String getLevel() {
        return level;
    }

    public int getImage() {
        return image;
    }
}
