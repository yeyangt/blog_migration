package com.august.jianshu.dto;

import com.google.gson.annotations.SerializedName;

public class CreateArticleRequest {
    @SerializedName("notebook_id")
    private long categoryId;
    private String title;
    private boolean at_bottom;

    public CreateArticleRequest() {
    }

    public CreateArticleRequest(long categoryId, String title, boolean atBottom) {
        this.categoryId = categoryId;
        this.title = title;
        this.at_bottom = atBottom;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isAt_bottom() {
        return at_bottom;
    }

    public void setAt_bottom(boolean at_bottom) {
        this.at_bottom = at_bottom;
    }
}
