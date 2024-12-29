package com.august.jianshu.dto;

import com.google.gson.annotations.SerializedName;

public class MemberInfoResponse {

    @SerializedName("is_login")
    private boolean isLogin;

    @SerializedName("nickname")
    private String nickname;

    @SerializedName("avatar_url")
    private String avatarUrl;

    @SerializedName("is_member")
    private boolean isMember;

    @SerializedName("expires_at")
    private String expiresAt;

    // Getters and Setters
    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean isLogin) {
        this.isLogin = isLogin;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public boolean isMember() {
        return isMember;
    }

    public void setMember(boolean isMember) {
        this.isMember = isMember;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }

    @Override
    public String toString() {
        return "MemberInfoResponse{" +
                "isLogin=" + isLogin +
                ", nickname='" + nickname + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", isMember=" + isMember +
                ", expiresAt='" + expiresAt + '\'' +
                '}';
    }
}
