package com.compulynx.studenttask.model;

public enum ActionType {
    SCORE_CHANGE("Class1"),FNAME_CHANGE("Class2"),LNAME_CHANGE("Class3"),CLASS_4("Class4"),CLASS_5("Class5");
    public final String label;
    ActionType(String class1) {
        this.label=class1;
    }
}
