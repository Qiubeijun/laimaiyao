package com.laimaiyao.model;

import org.litepal.crud.LitePalSupport;

/**
 * Created by MI on 2019/4/5
 */
public class User extends LitePalSupport {
    private String UID;
    private String NickName;
    private String Gender;
    private String Email;
    private int Locked;
    private int Code;
    private String Token;
    private boolean LoginStatus;

    public boolean isLoginStatus() {
        return LoginStatus;
    }

    public void setLoginStatus(boolean loginStatus) {
        LoginStatus = loginStatus;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    public int getCode() {
        return Code;
    }

    public void setCode(int code) {
        Code = code;
    }

    public int getLocked() {
        return Locked;
    }

    public void setLocked(int locked) {
        Locked = locked;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String nickName) {
        NickName = nickName;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}
