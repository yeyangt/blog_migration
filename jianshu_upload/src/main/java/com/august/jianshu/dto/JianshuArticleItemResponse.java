package com.august.jianshu.dto;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class JianshuArticleItemResponse implements JianshuArticleItem{
    private long id;
    private String slug;
    private boolean shared;
    /**
     * 分类Id
     */
    @SerializedName("notebook_id")
    private long categoryId;
    private int seq_in_nb;
    private int note_type;
    /**
     * 自动保存的版本
     */
    @SerializedName("autosave_control")
    private int autosaveControl;
    private String title;
    private long content_updated_at;
    private long last_compiled_at;
    private boolean paid;
    private boolean in_book;
    private boolean is_top;
    private boolean reprintable;
    private Object schedule_publish_at;

    public long getId() {
        return id;
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

    public String getTitle() {
        return title;
    }

    public long getContent_updated_at() {
        return content_updated_at;
    }

    public long getLast_compiled_at() {
        return last_compiled_at;
    }

    public boolean isPaid() {
        return paid;
    }

    public boolean isIn_book() {
        return in_book;
    }

    public boolean isIs_top() {
        return is_top;
    }

    public boolean isReprintable() {
        return reprintable;
    }

    public Object getSchedule_publish_at() {
        return schedule_publish_at;
    }

    @Override
    public String toString() {
        return "ArticleItem{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", notebook_id=" + categoryId +
                ", shared=" + shared +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        // 检查引用是否相同
        if (this == o) return true;

        // 检查类型和类是否匹配
        if (o == null || getClass() != o.getClass()) return false;

        // 转换为特定的类
        JianshuArticleItemResponse that = (JianshuArticleItemResponse) o;

        // 比较字段，确保对象相等
        return ((JianshuArticleItemResponse) o).getId()==(this.getId());
    }

    @Override
    public int hashCode() {
        // 计算字段的hash值，并合并
        return Objects.hash(this.getId());
    }
}
