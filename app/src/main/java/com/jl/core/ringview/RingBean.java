package com.jl.core.ringview;

public class RingBean {
    private String name;
    private float rate;

    public String getName() {
        return name;
    }

    public float getRate() {
        return rate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public RingBean(String name, float rate) {
        this.name = name;
        this.rate = rate;
    }
}
