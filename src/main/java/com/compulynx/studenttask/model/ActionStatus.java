package com.compulynx.studenttask.model;

public enum ActionStatus {

    CREATED(0),
    APPROVED(1),
    REJECTED(2);
    public final int label;
    ActionStatus(int i) {
        this.label=i;
    }
}
