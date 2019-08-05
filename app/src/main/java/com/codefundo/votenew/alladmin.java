package com.codefundo.votenew;

public class alladmin {
    public  String aadhaar,device_token,mobile;
    public alladmin(){

    }

    public alladmin(String aadhaar, String device_token, String mobile) {
        this.aadhaar = aadhaar;
        this.device_token = device_token;
        this.mobile = mobile;
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