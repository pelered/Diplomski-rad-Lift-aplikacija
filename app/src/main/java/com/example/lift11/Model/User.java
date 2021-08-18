package com.example.lift11.Model;

public class User {
    private String uid;
    private String email;
    private Boolean emailVerified;

    public User(String uid, String email, Boolean emailVerified) {
        this.uid = uid;
        this.email = email;
        this.emailVerified = emailVerified;
    }
    public User(){

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", email='" + email + '\'' +
                ", emailVerified=" + emailVerified +
                '}';
    }
}
