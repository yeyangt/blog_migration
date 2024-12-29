package com.august.jianshu.dto;

import com.google.gson.annotations.SerializedName;

/**
 * 更新文章
 */
public class UpdateArticleResponse {
    private long id;
    private long content_updated_at;
    private long last_compiled_at;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getContent_updated_at() {
        return content_updated_at;
    }

    public void setContent_updated_at(long content_updated_at) {
        this.content_updated_at = content_updated_at;
    }

    public long getLast_compiled_at() {
        return last_compiled_at;
    }

    public void setLast_compiled_at(long last_compiled_at) {
        this.last_compiled_at = last_compiled_at;
    }
}
