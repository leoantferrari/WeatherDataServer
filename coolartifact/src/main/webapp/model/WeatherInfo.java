package main.webapp.model;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class WeatherInfo {


    public int WeatherID;
    public String Date;
    public String place;
    public String Main;

    public int getWeatherID() {
        return WeatherID;
    }

    public void setWeatherID(int weatherID) {
        WeatherID = weatherID;
    }

    public String getDate() {
        return Date;
    }

    public void setWeatherDate(String date) {
        Date = date;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getMain() {
        return Main;
    }

    public void setMain(String main) {
        Main = main;
    }

    public String getTemp() {
        return Temp;
    }

    public void setTemp(String temp) {
        Temp = temp;
    }

    public String Temp;





    public WeatherInfo(String name){
        place = name;

    }

    public WeatherInfo(int WeatherID, String Date, String place,  String Main, String Temp){
        this.WeatherID= WeatherID;
        this.Date=Date;
        this.place=place;
        this.Main=Main;
        this.Temp=Temp;
    }





}
