package com.example.emsismartpresence;

public class DocumentItem {
    private String id;
    private String title;
    private String url;

    public DocumentItem(String id, String title, String url) {
        this.id = id;
        this.title = title;
        this.url = url;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getUrl() { return url; }
}
