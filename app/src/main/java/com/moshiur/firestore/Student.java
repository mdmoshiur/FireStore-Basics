package com.moshiur.firestore;

public class Student {
    private String name, roll, cgpa;

    public Student() {
        // this empty constructor is needed for fire store
    }

    public Student(String name, String roll, String cgpa) {
        this.name = name;
        this.roll = roll;
        this.cgpa = cgpa;
    }

    public String getName() {
        return name;
    }

    public String getRoll() {
        return roll;
    }

    public String getCgpa() {
        return cgpa;
    }
}
