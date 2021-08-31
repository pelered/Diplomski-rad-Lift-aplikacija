package com.example.lift11.Model;

public class Lift_state {
    private String t_k;
    private String c_p;
    private String b_p;
    private String state;

    public Lift_state() {
    }

    public Lift_state(String t_k, String c_p, String b_p, String state) {
        this.t_k = t_k;
        this.c_p = c_p;
        this.b_p = b_p;
        this.state = state;
    }

    public String getT_k() {
        return t_k;
    }

    public void setT_k(String t_k) {
        this.t_k = t_k;
    }

    public String getC_p() {
        return c_p;
    }

    public void setC_p(String c_p) {
        this.c_p = c_p;
    }

    public String getB_p() {
        return b_p;
    }

    public void setB_p(String b_p) {
        this.b_p = b_p;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "Lift_state{" +
                "t_k='" + t_k + '\'' +
                ", c_p='" + c_p + '\'' +
                ", b_p='" + b_p + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
