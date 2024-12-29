package com.august.jianshu.dto;

public interface JianshuArticleItem {

    public long getId();

    public long getCategoryId();

    public String getTitle();

    public boolean isShared();

    public int getAutosaveControl();

    public static JianshuArticleItem createJianshuArticleItem(long id, long categoryId, String title, boolean isShared,int autosaveControl) {
        return new JianshuArticleItem() {
            private final long _id = id;
            private final long _categoryId = categoryId;
            private final String _title = title;
            private final boolean _isShared = isShared;

            private final int _autosaveControl = autosaveControl;

            @Override
            public long getId() { return _id; }

            public int getAutosaveControl() { return _autosaveControl; }

            @Override
            public long getCategoryId() { return _categoryId; }

            @Override
            public String getTitle() { return _title; }

            @Override
            public boolean isShared() { return _isShared; }
        };
    }

}
