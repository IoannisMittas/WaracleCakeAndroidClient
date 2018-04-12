package com.waracle.androidtest;

public class Cake {
    private String title;
    private String description;
    private String imageLink;

    public Cake(String title, String description, String imageLink) {
        this.title = title;
        this.description = description;
        this.imageLink = imageLink;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }
}
