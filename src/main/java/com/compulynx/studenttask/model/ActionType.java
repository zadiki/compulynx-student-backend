package com.compulynx.studenttask.model;

public enum ActionType {
    SCORE_CHANGE("SCORE_CHANGE"), FNAME_CHANGE("FNAME_CHANGE")
    ,DOB_CHANGE("DOB_CHANGE"),PHOTO_CHANGE("PHOTO_CHANGE"),LNAME_CHANGE("LNAME_CHANGE"), STATUS_CHANGE("STATUS_CHANGE"), CLASS_CHANGE("CLASS_CHANGE");
    public final String label;

    ActionType(String actionType) {
        this.label = actionType;
    }
}
