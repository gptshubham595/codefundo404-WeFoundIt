package com.codefundo.votenew;

public class voters {
String name,dob,eligible,voted;

    public voters() {
    }

    public voters(String name, String dob, String eligible, String voted) {
        this.name = name;
        this.dob = dob;
        this.eligible = eligible;
        this.voted = voted;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getEligible() {
        return eligible;
    }

    public void setEligible(String eligible) {
        this.eligible = eligible;
    }

    public String getVoted() {
        return voted;
    }

    public void setVoted(String voted) {
        this.voted = voted;
    }
}
