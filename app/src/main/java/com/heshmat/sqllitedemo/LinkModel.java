package com.heshmat.sqllitedemo;

import java.util.List;
import java.util.Objects;

public class LinkModel {
    private String name;
    private String url;
    private String comment;
    private long id;


    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LinkModel(long id, String name, String comment, String url) {
        this.name = name;
        this.url = url;
        this.comment = comment;
        this.id = id;
    }

    public LinkModel(String name, String comment, String url) {
        this.name = name;
        this.url = url;
        this.comment = comment;

    }

    public LinkModel(long id) {

        this.id = id;

    }


    public String getName() {
        return name;
    }

    public String getUrl() {

        if (url.contains("www.")) {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url;
            }
        }

        return url;
    }

    public String getComment() {
        return comment;
    }


    public LinkModel() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LinkModel)) return false;
        LinkModel linkModel = (LinkModel) o;
        return id == linkModel.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, url, comment, id);
    }

    public static LinkModel returnSelectedLink(long id, List<LinkModel> list) {
        for (LinkModel linkModel : list) {
            if (linkModel.getId() == id)
                return linkModel;
        }
        return null;
    }
}
