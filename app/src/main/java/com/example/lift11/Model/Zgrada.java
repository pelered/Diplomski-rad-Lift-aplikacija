package com.example.lift11.Model;

import java.util.ArrayList;

public class Zgrada {
    private String ime;
    private String u_uid;
    private ArrayList<String> lifts;
    private ArrayList<String> podzg;
    private String key;

    private String zg_id;

    public Zgrada(){

    }

    public Zgrada(String ime, String u_uid) {
        this.ime = ime;
        this.u_uid = u_uid;
    }
    public Zgrada(String ime, String u_uid,String zg_id) {
        this.ime = ime;
        this.u_uid = u_uid;
        this.zg_id=zg_id;

    }


    public Zgrada(String ime, String u_uid, ArrayList<String> podzg, ArrayList<String> lifts) {
        this.ime = ime;
        this.u_uid = u_uid;
        this.podzg = podzg;
        this.lifts = lifts;

    }


    public Zgrada(String ime,String u_uid,  ArrayList<String> lifts) {
        this.ime = ime;
        this.u_uid = u_uid;
        this.lifts = lifts;
    }

   /* public Zgrada(String ime, String u_uid, ArrayList<String> podzg, ArrayList<String> lifts) {
        this.ime = ime;
        this.u_uid = u_uid;
        this.podzg = podzg;
        this.lifts = lifts;
        //this.zg_id=zg_id;

    }*/
    public Zgrada(String ime, String u_uid, ArrayList<String> lifts,String zg_id) {
        this.ime = ime;
        this.u_uid = u_uid;
        this.lifts = lifts;
        this.zg_id=zg_id;

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getU_uid() {
        return u_uid;
    }

    public void setU_uid(String u_uid) {
        this.u_uid = u_uid;
    }

    public ArrayList<String> getLifts() {
        return lifts;
    }

    public void setLifts(ArrayList<String> lifts) {
        this.lifts = lifts;
    }

    public ArrayList<String> getPodzg() {
        return podzg;
    }

    public void setPodzg(ArrayList<String> podzg) {
        this.podzg = podzg;
    }

    public String getZg_id() {
        return zg_id;
    }

    public void setZg_id(String zg_id) {
        this.zg_id = zg_id;
    }

    @Override
    public String toString() {
        return "Zgrada{" +
                "ime='" + ime + '\'' +
                ", u_uid='" + u_uid + '\'' +
                ", lifts=" + lifts +
                ", podzg=" + podzg +
                ", key='" + key + '\'' +
                ", zg_id='" + zg_id + '\'' +
                '}';
    }
}
