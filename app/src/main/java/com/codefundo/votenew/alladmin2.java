package com.codefundo.votenew;

import java.util.ArrayList;
import java.util.List;

public class alladmin2 {
    public  String aadhaar,device_token,mobile;
    private List<allfamily> familyMembers = null;
public alladmin2(){

}

    public alladmin2(String aadhaar, String device_token, String mobile, List<allfamily> familyMembers) {
        this.aadhaar = aadhaar;
        this.device_token = device_token;
        this.mobile = mobile;
        this.familyMembers = familyMembers;
    }

    public void setFamilyMembers(List<allfamily> members) {
        if (familyMembers == null) {
            familyMembers = new ArrayList<allfamily>();
        }
        //This part depends on how you want to add new members.
        familyMembers.addAll(members);
    }
    public List<allfamily> getFamilyMembers() {
        if(familyMembers != null){
            return this.familyMembers;
        } else {
            return new ArrayList<allfamily>();
        }
    }

    public String getAadhaar() {
        return aadhaar;
    }

    public void setAadhaar(String aadhaar) {
        this.aadhaar = aadhaar;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
