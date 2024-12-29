package com.august.jianshu.dto;

import com.google.gson.annotations.SerializedName;

/**
 * 创建文章
 */
public class CreateArticleResponse {
    private long id;
    private String title;
    private String slug;
    private boolean shared;
    /**
     * 分类Id
     */
    @SerializedName("notebook_id")
    private long categoryId;
    private int seq_in_nb;
    private int note_type;
    @SerializedName("autosave_control")
    private int autosaveControl;
    private long content_updated_at;
    private long last_compiled_at;

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSlug() {
        return slug;
    }

    public boolean isShared() {
        return shared;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public int getSeq_in_nb() {
        return seq_in_nb;
    }

    public int getNote_type() {
        return note_type;
    }

    public int getAutosaveControl() {
        return autosaveControl;
    }

    public long getContent_updated_at() {
        return content_updated_at;
    }

    public long getLast_compiled_at() {
        return last_compiled_at;
    }

    @Override
    public String toString() {
        return "CreateArticleResponse{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", slug='" + slug + '\'' +
                ", shared=" + shared +
                ", notebook_id=" + categoryId +
                ", seq_in_nb=" + seq_in_nb +
                ", note_type=" + note_type +
                ", autosave_control=" + autosaveControl +
                ", content_updated_at=" + content_updated_at +
                ", last_compiled_at=" + last_compiled_at +
                '}';
    }
}
