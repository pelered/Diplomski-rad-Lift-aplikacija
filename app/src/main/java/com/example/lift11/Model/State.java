package com.example.lift11.Model;

public class State {
    private Boolean state;
    private String key;
    private String u_uid;
    private String fcmTokens;
    public State() {

    }

    public State(Boolean state, String u_uid, String fcmTokens) {
        this.state = state;
        this.u_uid = u_uid;
        this.fcmTokens = fcmTokens;
    }


    @Override
    public String toString() {
        return "State{" +
                "state=" + state +
                ", key='" + key + '\'' +
                ", u_uid='" + u_uid + '\'' +
                ", fcmTokens='" + fcmTokens + '\'' +
                '}';
    }

    public String getFcmTokens() {
        return fcmTokens;
    }

    public void setFcmTokens(String fcmTokens) {
        this.fcmTokens = fcmTokens;
    }

    public String getU_uid() {
        return u_uid;
    }

    public void setU_uid(String u_uid) {
        this.u_uid = u_uid;
    }

    public State(Boolean state) {
        this.state = state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Boolean getState() {
        return state;
    }

    public String getKey() {
        return key;
    }

}
