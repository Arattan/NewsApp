package com.example.android.newsapp;

public class Article {

    private String mSection;
    private String mTitle;
    private String mDate;
    private String mAuthor;
    private String mUrl;

    public Article (String section, String title, String author, String date, String url){
        mSection = section;
        mTitle = title;
        mDate = date;
        mAuthor = author;
        mUrl = url;
    }

    public String getSection() { return mSection; }
    public String getTitle() { return mTitle; }
    public String getAuthorTag() { return mAuthor; }
    public String getDate() { return mDate; }
    public String getUrl() { return mUrl;}
}
