package com.example.lift11.Model;

public class State {
    private Boolean state;
    private String key;

    public State() {

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

    @Override
    public String toString() {
        return "State{" +
                "state=" + state +
                ", key='" + key + '\'' +
                '}';
    }
}
