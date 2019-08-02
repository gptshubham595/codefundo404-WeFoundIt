package com.codefundo.votenew;

public class Dateit {
    public  String finaltime,day,month,year;

    public Dateit(String finaltime, String day, String month, String year) {
        this.finaltime=finaltime;
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public String getFinaltime() {
        return finaltime;
    }

    public void setFinaltime(String finaltime) {
        this.finaltime= finaltime;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
