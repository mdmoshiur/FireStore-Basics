package com.moshiur.firestore;

import com.google.firebase.firestore.Exclude;

public class Student {
    private String name;
    private int roll;
    private double cgpa;
    private String documentId;

    public Student() {
        // this empty constructor is needed for fire store
    }

    public Student(String name, int roll, double cgpa) {
        this.name = name;
        this.roll = roll;
        this.cgpa = cgpa;
    }

    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getName() {
        return name;
    }

    public int getRoll() {
        return roll;
    }

    public double getCgpa() {
        return cgpa;
    }
}
