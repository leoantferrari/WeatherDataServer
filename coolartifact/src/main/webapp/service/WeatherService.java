package main.webapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import main.webapp.model.DateModel;
import main.webapp.model.WeatherInfo;
import main.webapp.model.WeatherInfoLibrary;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.*;
import java.util.List;

@Path("/weatherdata")
public class WeatherService {

    @Context
    private HttpServletRequest request;
    private WeatherInfoLibrary library;

    public WeatherService() {
        library = new WeatherInfoLibrary();
    }

    /**
     *  Post method which is called to save WeatherInfo to Database
     *
     *
     * @param weather is JSON data parsed into service
     * @return response with successful status and the location if succeeded or 404 status and location
     */
    @POST
    @Path("/save")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response Save( String weather
    ) {
        int status=200;
        try{
            JSONParser parse = new JSONParser();
            JSONObject jobj = (JSONObject)parse.parse(weather);

            WeatherInfo weatherman = new WeatherInfo(Integer.parseInt(jobj.get("weatherid").toString()), jobj.get("date").toString(), jobj.get("place").toString(),jobj.get("main").toString(), jobj.get("temp").toString());

            if(library.SaveData(weatherman)==true){
                Response response1 = Response.status(status).header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Credentials", "true").header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization").header("Access-Control-Allow-Methods", "GET, POST, DELETE").entity(weatherman.getPlace()).build();
                return  response1;
            }


        }catch(Exception ex){
            ex.printStackTrace();

        }
        status = 404;







        Response response = Response
                .status(status)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Headers",
                        "origin, content-type, accept, authorization")
                .header("Access-Control-Allow-Methods",
                        "GET, POST, DELETE")
                .entity(weather)
                .build();
        return response;







    }

    /**
     * Post Method which saves the current Weather Information of a specific place
     * @param place
     * @return json information of specific place
     */
    @POST
    @Path("/savecurrent/{place}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response SaveCurrent( @PathParam("place") String place
    ) {
        int status=200;
        WeatherInfo info = library.GetCurrentWeather(place);
        ObjectMapper mapper = new ObjectMapper();


        if(library.SaveCurrent(place)){
            try{
                return  Response.status(status).header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Credentials", "true").header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization").header("Access-Control-Allow-Methods", "GET, POST, DELETE").entity(mapper.writeValueAsString(info)).build();

            }catch (Exception ex){
                return  Response.status(status).header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Credentials", "true").header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization").header("Access-Control-Allow-Methods", "GET, POST, DELETE").entity(place).build();

            }
        }

        else{
            status=300;
            return Response.status(status).header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Credentials", "true").header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization").header("Access-Control-Allow-Methods", "GET, POST, DELETE").entity(place).build();
        }



    }

    @GET
    @Path("/{place}/{startdate}/{enddate}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response GetCurrent(   @PathParam("place") String place,
                                @PathParam("startdate") String startdate,
                                @PathParam("enddate")String enddate) {



        DateModel datefrom = new DateModel(startdate.substring(0,4),startdate.substring(4,6),startdate.substring(6,8), startdate.substring(8,10), startdate.substring(10,12), startdate.substring(12,14));
        DateModel dateto = new DateModel(enddate.substring(0,4),enddate.substring(4,6),enddate.substring(6,8), enddate.substring(8,10), enddate.substring(10,12), enddate.substring(12,14));

        List<WeatherInfo> lol= library.GetWeatherData(place,datefrom.GetDateFormatted(),dateto.GetDateFormatted());

        ObjectMapper mapper = new ObjectMapper();

        String json="";
        try{
            json=mapper.writeValueAsString(lol);
        }catch (Exception ex){
            ex.printStackTrace();
        }



        int status = 200;
            Response response;

           response = Response.status(status).header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Credentials", "true").header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization").header("Access-Control-Allow-Methods", "GET, POST, DELETE").entity(json).build();

            return response;


       }

    @GET
    @Path("/weather/{place}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response GetAll(@PathParam("place") String place) {




        List<WeatherInfo> lol= library.GetAllFromLocation(place);

        ObjectMapper mapper = new ObjectMapper();

        String json="";
        try{
            json=mapper.writeValueAsString(lol);
        }catch (Exception ex){
            ex.printStackTrace();
        }



        int status = 200;
        Response response;

        response = Response.status(status).header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Credentials", "true").header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization").header("Access-Control-Allow-Methods", "GET, POST, DELETE").entity(json).build();

        return response;


    }




    }




