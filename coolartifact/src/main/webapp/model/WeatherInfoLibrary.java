package main.webapp.model;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.*;

/**
 *  WeatherInfoLibrary handles the connection between the Database and the Server. It also requests information from
 *  OpenWeatherMap API using an ApiKey.
 *
 * @author Leo Ferrari
 * @since  15.05.2020
 * @version 1.4
 *
 */
public class WeatherInfoLibrary {

    //Database Info
   // String hostName = "leoantsmith.database.windows.net";
    String dbName = "weatherdata";
    String user = "root";
    String password = "gibbiX12345";
    String url = "jdbc:sqlserver://leoantsmith.database.windows.net:1433;database=WeatherData;user=leoantsmith@WeatherData;password=gibbiX12345;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
    Connection connection = null;

    //API Key for OpenWeatherMap
    public String ApiKey = "4dc6052f87cf14206bd51538961248cf";

    /**
     * Method requests current WeatherInfo from OpenWeatherMap Api and returns it in
     *  a WeatherInfo Object.
     *
     * @param place the place of which the weather should be requested
     * @return WeatherInfo Object
     */
    public WeatherInfo GetCurrentWeather(String place){
        String inline="";

        try{
            // this is the URL to the OpenWeatherMap Api. The requested place and a valid API key are added
            URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q="+place+"&appid="+ApiKey+"");

            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();


            if(conn.getResponseCode()!=200){
                throw new RuntimeException("Http: "+conn.getResponseCode());
            }else{
                Scanner sc = new Scanner(url.openStream());
                while(sc.hasNext())
                {
                    inline+=sc.nextLine();
                }
                System.out.println("JSON data in string format");
                System.out.println(inline);
                sc.close();
            }

            JSONParser parse = new JSONParser();
            JSONObject jobj = (JSONObject)parse.parse(inline);
            JSONArray jsonArray1 = (JSONArray) jobj.get("weather");

            JSONObject jobj2 = (JSONObject)jsonArray1.get(0);
            String main = jobj2.get("main").toString();
            JSONObject json = (JSONObject)parse.parse(jobj.get("main").toString());
            String temp = json.get("temp").toString();

            Random random = new Random();

            LocalDateTime now = LocalDateTime.now();
            WeatherInfo lol2 = new WeatherInfo(random.nextInt(), now.toString(), place, main,(""+(Float.parseFloat(temp)-273.15)+"").substring(0,5) );



            return lol2;
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }


    }

    /**
     *  This method gets all the weatherdata from a specific location using the database.
     * @param place the location of which the data is sought
     * @return returns a list of all weatherdata
     */
    public List<WeatherInfo> GetAllFromLocation(String place){
        try{
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/weather","root","");
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT weatherinfo.weatherid, weatherinfo.date, place.placename, weatherinfo.main, weatherinfo.temp FROM weather.weatherinfo INNER JOIN weather.place on weatherinfo.placeid = place.idplace  WHERE place.placename='"+place+"';");
            List<WeatherInfo> info = new ArrayList<WeatherInfo>();
            while(rs.next()){
                info.add(new WeatherInfo(Integer.parseInt(rs.getString("weatherid").toString()),rs.getString("date"), rs.getString("placename"), rs.getString("main"), rs.getString("temp")));
            }
            return info;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     *  This method gets Weatherdata from the database from a specific place and time. You can give in a place, and a date
     *  to and from.
     * @param place is the place of which data is sought
     * @param dateFrom date from
     * @param dateTo date to
     * @return returns a List of all the WeatherInfo objects in database
     */
    public List<WeatherInfo> GetWeatherData(String place, String dateFrom, String dateTo){
        try{
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/weather","root","");
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("SELECT weatherinfo.weatherid, weatherinfo.date, place.placename, weatherinfo.main, weatherinfo.temp FROM weather.weatherinfo INNER JOIN weather.place on weatherinfo.placeid = place.idplace WHERE place.placename='"+place+"' AND weatherinfo.date BETWEEN '"+dateFrom+"' AND '"+dateTo+"';");
            List<WeatherInfo> info = new ArrayList<WeatherInfo>();


            while(rs.next()){
                info.add(new WeatherInfo(Integer.parseInt(rs.getString("weatherid").toString()),rs.getString("date"), rs.getString("placename"), rs.getString("main"), rs.getString("temp")));
            }

            return info;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method saves a WeatherInfo object to the database
     * @param weather is the WeatherInfo object that should be saved
     * @return returns true if successful, false if not
     */
    public boolean SaveData(WeatherInfo weather){
        boolean exists=false;
        String placeid="";
        try{

            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/weather","root","");
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("select * from weather.place where placename='"+weather.getPlace()+"'");
            List<WeatherInfo> info = new ArrayList<WeatherInfo>();


            while(rs.next()){
                if(rs.getString("placename").toString().equals(weather.getPlace())) {
                    exists = true;
                }
            }
            if(exists==false){
                stmt.execute("INSERT INTO weather.place(placename) VALUES('"+weather.getPlace()+"')");
            }
            ResultSet rs1= stmt.executeQuery("select idplace from weather.place where placename='"+weather.getPlace()+"'");
            while (rs1.next()) {

                placeid+=rs1.getString("idplace").toString();

            }

            stmt.execute("INSERT INTO weatherInfo(date, placeid, main, temp) VALUES ('" + weather.getDate()+ "','" +placeid + "','"+ weather.getMain() +  "','"+weather.getTemp()+"');");



          return true;
        }catch(Exception e){ System.out.println(e);
        return false;}

    }

    /**
     *  This Method saves the current weather(fetched form OpenWeatherMap Api) of the parsed place to the database
     *
     * @param place is the place of which the weather should be fetched
     * @return a boolean, true if successful, false if not
     */
    public boolean SaveCurrent(String place){
        boolean exists = false;
        String placeid="";
        try{
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/weather","root","");
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("select * from weather.place where placename='"+place+"'");
            List<WeatherInfo> info = new ArrayList<WeatherInfo>();


            while(rs.next()){
                if(rs.getString("placename").toString().equals(place)) {
                    exists = true;
                }
            }
            if(exists==false){
                stmt.execute("INSERT INTO weather.place(placename) VALUES('"+place+"')");
            }

            ResultSet rs1= stmt.executeQuery("select idplace from weather.place where placename='"+place+"'");
            while (rs1.next()) {

                placeid+=rs1.getString("idplace").toString();

            }

            WeatherInfo weather = GetCurrentWeather(place);
            stmt.execute("INSERT INTO weatherInfo(date, placeid, main, temp) VALUES ('" + weather.getDate()+ "','" + placeid + "','"+ weather.getMain() +  "','"+weather.getTemp()+"');");

            return true;

        }catch(Exception e){
            e.printStackTrace();

            return false;

        }



    }
}
