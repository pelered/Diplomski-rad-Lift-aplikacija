package com.example.lift11.Model;

import java.util.HashMap;
import java.util.Map;

public class Lift_travels {

    private int p_k;//pocetni kat
    private int z_k;//zavrsni kat

    private int n_k;//najnizi kat
    private int v_k;//najvisi kat

    private String start_time;
    private String end_time;

    private int count_p;//broj ljudi

    private String odb_zgrada;
    private String odb_pod_zgrada;
    private String id_lift;


    public Lift_travels(int p_k, int z_k, int n_k, int v_k, String start_time, String end_time, int count_p, String odb_zgrada, String odb_pod_zgrada, String id_lift) {
        this.p_k = p_k;
        this.z_k = z_k;
        this.n_k = n_k;
        this.v_k = v_k;
        this.start_time = start_time;
        this.end_time = end_time;
        this.count_p = count_p;
        this.odb_zgrada = odb_zgrada;
        this.odb_pod_zgrada = odb_pod_zgrada;
        this.id_lift = id_lift;
    }

    public Lift_travels(int p_k, int z_k, int n_k, int v_k, String start_time, String end_time, int count_p, String odb_zgrada, String odb_pod_zgrada) {
        this.p_k = p_k;
        this.z_k = z_k;
        this.n_k = n_k;
        this.v_k = v_k;
        this.start_time = start_time;
        this.end_time = end_time;
        this.count_p = count_p;
        this.odb_zgrada = odb_zgrada;
        this.odb_pod_zgrada = odb_pod_zgrada;
    }

    public int getP_k() {
        return p_k;
    }

    public void setP_k(int p_k) {
        this.p_k = p_k;
    }

    public int getZ_k() {
        return z_k;
    }

    public void setZ_k(int z_k) {
        this.z_k = z_k;
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

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public int getCount_p() {
        return count_p;
    }

    public void setCount_p(int count_p) {
        this.count_p = count_p;
    }

    public String getOdb_zgrada() {
        return odb_zgrada;
    }

    public void setOdb_zgrada(String odb_zgrada) {
        this.odb_zgrada = odb_zgrada;
    }

    public String getOdb_pod_zgrada() {
        return odb_pod_zgrada;
    }

    public void setOdb_pod_zgrada(String odb_pod_zgrada) {
        this.odb_pod_zgrada = odb_pod_zgrada;
    }

    public String getId_lift() {
        return id_lift;
    }

    public void setId_lift(String id_lift) {
        this.id_lift = id_lift;
    }

    @Override
    public String toString() {
        return "Lift_travels{" +
                "p_k=" + p_k +
                ", z_k=" + z_k +
                ", n_k=" + n_k +
                ", v_k=" + v_k +
                ", start_time='" + start_time + '\'' +
                ", end_time='" + end_time + '\'' +
                ", count_p=" + count_p +
                ", odb_zgrada='" + odb_zgrada + '\'' +
                ", odb_pod_zgrada='" + odb_pod_zgrada + '\'' +
                ", id_lift='" + id_lift + '\'' +
                '}';
    }

    /*public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("p_k",p_k);
        result.put("n_k",n_k);
        result.put("v_k",v_k);

        return result;
    }*/


}
