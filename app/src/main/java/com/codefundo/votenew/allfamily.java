package com.codefundo.votenew;

import com.google.firebase.database.PropertyName;

public class allfamily {
    public  String name,image,aadhaar,dob,eligible,gender,slotend,slotstart;
    public allfamily(){

    }

    public String getSlotend() {
        return slotend;
    }

    public void setSlotend(String slotend) {
        this.slotend = slotend;
    }

    public String getSlotstart() {
        return slotstart;
    }

    public void setSlotstart(String slotstart) {
        this.slotstart = slotstart;
    }

    public allfamily(String name, String image, String aadhaar, String dob, String eligible, String gender, String slotend, String slotstart) {
        this.name = name;
        this.image = image;
        this.aadhaar = aadhaar;
        this.dob = dob;
        this.eligible = eligible;
        this.gender = gender;
        this.slotend = slotend;
        this.slotstart = slotstart;
    }

    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    public String getAadhaar() {
        return aadhaar;
    }

    public void setAadhaar(String aadhaar) {
        this.aadhaar = aadhaar;
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
}