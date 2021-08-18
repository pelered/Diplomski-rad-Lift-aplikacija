package com.example.lift11.Model;

public class Lift {
    private String ime;
    private String zgrada;
    private String pod_zg;
    private String u_uid;
    private int n_k;
    private int v_k;

    private String key;



    public Lift() {
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
                '}';
    }
}
