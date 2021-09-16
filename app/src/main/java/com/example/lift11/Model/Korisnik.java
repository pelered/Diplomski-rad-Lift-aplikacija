package com.example.lift11.Model;

public class Korisnik {
    private String uid;
    private String email;
    private Boolean emailVerified;

    public Korisnik(String uid, String email, Boolean emailVerified) {
        this.uid = uid;
        this.email = email;
        this.emailVerified = emailVerified;
    }
    public Korisnik(){

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
        return "Korisnik{" +
                "uid='" + uid + '\'' +
                ", email='" + email + '\'' +
                ", emailVerified=" + emailVerified +
                '}';
    }
}
