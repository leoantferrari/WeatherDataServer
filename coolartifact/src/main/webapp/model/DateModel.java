package main.webapp.model;

public class DateModel {
    String year;

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMinute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }
    public DateModel(String year, String month, String day, String hour, String minute, String second){
        this.year=year;
        this.month=month;
        this.day=day;
        this.hour=hour;
        this.minute=minute;
        this.second=second;
    }
    public String GetDateFormatted(){
        return year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second;

    }
    private String month;
    private String day;
    private String hour;
    private String minute;
    private String second;
}
