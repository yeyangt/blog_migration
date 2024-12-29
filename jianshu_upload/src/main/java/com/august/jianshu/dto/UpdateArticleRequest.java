package com.august.jianshu.dto;

import com.google.gson.annotations.SerializedName;

/**
 * 更新文章
 */
public class UpdateArticleRequest {
    private long id;
    private String title;
    @SerializedName("autosave_control")
    private int autosaveControl;

    private String content;

    public UpdateArticleRequest() {
    }

    public UpdateArticleRequest(long id, String title, int autosaveControl,String content) {
        this.id = id;
        this.title = title;
        this.autosaveControl = autosaveControl;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public int getAutosaveControl() {
        return autosaveControl;
    }

    public void setAutosaveControl(int autosaveControl) {
        this.autosaveControl = autosaveControl;
    }
}
