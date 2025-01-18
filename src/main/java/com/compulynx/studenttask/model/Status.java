package com.compulynx.studenttask.model;

public enum Status {
    ACTIVE(1),
    INACTIVE(0);
    public final int label;
    Status(int i) {
        this.label=i;
    }
}
