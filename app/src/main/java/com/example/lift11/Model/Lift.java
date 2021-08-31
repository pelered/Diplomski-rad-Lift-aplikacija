package com.example.lift11.Model;

import androidx.recyclerview.widget.RecyclerView;

public class Lift {
    private String ime;
    private String zgrada;
    private String pod_zg;
    private String u_uid;
    private int n_k;
    private int v_k;

    private String key;
    private Boolean is_connected;
    private String zg_ime,pod_ime;



    public Lift() {
    }
    public Lift(String ime, String zgrada, String pod_zg, String u_uid, int n_k, int v_k,boolean is_connected) {
        this.ime = ime;
        this.zgrada = zgrada;
        this.pod_zg = pod_zg;
        this.u_uid = u_uid;
        this.n_k = n_k;
        this.v_k = v_k;
        this.is_connected=is_connected;
    }

    public Lift(String ime, String zgrada, String pod_zg, String u_uid, int n_k, int v_k) {
        this.ime = ime;
        this.zgrada = zgrada;
        this.pod_zg = pod_zg;
        this.u_uid = u_uid;
        this.n_k = n_k;
        this.v_k = v_k;
    }
    public Lift(String ime, String zgrada,  String u_uid, int n_k, int v_k) {
        this.ime = ime;
        this.zgrada = zgrada;
        this.u_uid = u_uid;
        this.n_k = n_k;
        this.v_k = v_k;
    }
    public Lift(String ime, String zgrada,  String u_uid, int n_k, int v_k,boolean is_connected) {
        this.ime = ime;
        this.zgrada = zgrada;
        this.u_uid = u_uid;
        this.n_k = n_k;
        this.v_k = v_k;
        this.is_connected=is_connected;
    }

    public String getZg_ime() {
        return zg_ime;
    }

    public void setZg_ime(String zg_ime) {
        this.zg_ime = zg_ime;
    }

    public String getPod_ime() {
        return pod_ime;
    }

    public void setPod_ime(String pod_ime) {
        this.pod_ime = pod_ime;
    }

    public Boolean getIs_connected() {
        return is_connected;
    }

    public void setIs_connected(Boolean is_connected) {
        this.is_connected = is_connected;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getZgrada() {
        return zgrada;
    }

    public void setZgrada(String zgrada) {
        this.zgrada = zgrada;
    }

    public String getPod_zg() {
        return pod_zg;
    }

    public void setPod_zg(String pod_zg) {
        this.pod_zg = pod_zg;
    }

    public String getU_uid() {
        return u_uid;
    }

    public void setU_uid(String u_uid) {
        this.u_uid = u_uid;
    }

    public int getN_k() {
        return n_k;
    }

    public void setN_k(int n_k) {
        this.n_k = n_k;
    }

    public int getV_k() {
        return v_k;
    }

    public void setV_k(int v_k) {
        this.v_k = v_k;
    }
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "Lift{" +
                "ime='" + ime + '\'' +
                ", zgrada='" + zgrada + '\'' +
                ", pod_zg='" + pod_zg + '\'' +
                ", u_uid='" + u_uid + '\'' +
                ", n_k=" + n_k +
                ", v_k=" + v_k +
                ", key='" + key + '\'' +
                ", is_connected=" + is_connected +
                ", zg_ime='" + zg_ime + '\'' +
                ", pod_ime='" + pod_ime + '\'' +
                '}';
    }
}
