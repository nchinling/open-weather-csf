package sg.nus.edu.iss.openweatherangular.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;

public class Weather implements Serializable {
    private static final long serialVersionUID = 1L;

    private String city;
    private String temperature;
    private String visibility;
    private Long sunriseTime;
    private Long sunsetTime;
    //List is used because the JSON data has an array 
    private List<WeatherCondition> weathercondition = new LinkedList<>();

    //need to implement id for insertion into redis
    private String dataId;

    public Weather(){}

    public Weather(String city, String temperature) {
        this.city = city;
        this.temperature = temperature;
    }

    public Weather(String dataId) {
        this.dataId = dataId;
    }

    public Weather(String city, String temperature, String visibility, Long sunriseTime, Long sunsetTime,
            List<WeatherCondition> weathercondition, String dataId) {
        this.city = city;
        this.temperature = temperature;
        this.visibility = visibility;
        this.sunriseTime = sunriseTime;
        this.sunsetTime = sunsetTime;
        this.weathercondition = weathercondition;
        this.dataId = dataId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    
    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public Long getSunriseTime() {
        return sunriseTime;
    }

    public void setSunriseTime(Long sunriseTime) {
        this.sunriseTime = sunriseTime;
    }

    public Long getSunsetTime() {
        return sunsetTime;
    }

    public void setSunsetTime(Long sunsetTime) {
        this.sunsetTime = sunsetTime;
    }

    public List<WeatherCondition> getWeathercondition() {
        return weathercondition;
    }

    public void setWeathercondition(List<WeatherCondition> weathercondition) {
        this.weathercondition = weathercondition;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }



    @Override
    public String toString() {
        return "Weather [city=" + city + ", temperature=" + temperature + ", visibility=" + visibility
                + ", sunriseTime=" + sunriseTime + ", sunsetTime=" + sunsetTime + ", weathercondition="
                + weathercondition + ", dataId=" + dataId + "]";
    }

    public static Weather createUserObject(String json) throws IOException {
        Weather w = new Weather();
        try(InputStream is = new ByteArrayInputStream(json.getBytes())){
            JsonReader r = Json.createReader(is);
            JsonObject o = r.readObject();
            w.setCity(o.getString("name"));
            JsonObject main = o.getJsonObject("main");
            w.setTemperature(main.getJsonNumber("temp").toString());
            w.setVisibility(o.getJsonNumber("visibility").toString());
            JsonObject sys = o.getJsonObject("sys");
            w.setSunriseTime(sys.getJsonNumber("sunrise").longValue());
            w.setSunsetTime(sys.getJsonNumber("sunset").longValue());
            w.weathercondition = o.getJsonArray("weather").stream()
            .map(v-> (JsonObject)v)
            .map(v-> WeatherCondition.createFromJson(v))
            .toList();
            
        }
        return w;
    }

    
    public static Weather createUserObjectFromRedis(String jsonStr) throws IOException{
        Weather w = new Weather();
        try(InputStream is = new ByteArrayInputStream(jsonStr.getBytes())) {
            JsonObject o = toJSON(jsonStr);
            w.setCity(o.getString("city"));
            w.setDataId(o.getString("dataId"));
            w.setTemperature(o.getString("temperature"));
            w.setVisibility(o.getString("visibility"));
            w.setSunriseTime(o.getJsonNumber("sunriseTime").longValue());
            w.setSunsetTime(o.getJsonNumber("sunsetTime").longValue());
    
            w.weathercondition = o.getJsonArray("weathercondition").stream()
            .map(v-> (JsonObject)v)
            .map(v-> WeatherCondition.createFromRedisJson(v))
            .toList();
            // Retrieve weathercondition as a JsonArray
            // JsonArray weatherConditionsArray = o.getJsonArray("weathercondition");
            // List<WeatherCondition> weatherConditions = new ArrayList<>();
    
            // for (JsonValue value : weatherConditionsArray) {
            //     if (value instanceof JsonObject) {
            //         JsonObject weatherObj = (JsonObject) value;
            //         WeatherCondition weatherCondition = WeatherCondition.createFromRedisJson(weatherObj);
            //         weatherConditions.add(weatherCondition);
            //     }
            // }
        }
   
        return w;
    }

    public static JsonObject toJSON(String json){
        JsonReader r = Json.createReader(new StringReader(json));
        return r.readObject();
    }

    public JsonObject toJSON(){

        //to convert array to JSON
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (WeatherCondition wc : weathercondition) {
            JsonObjectBuilder objBuilder = Json.createObjectBuilder()
                    .add("mainWeather", wc.getMainWeather())
                    .add("description", wc.getDescription());
                    
            arrayBuilder.add(objBuilder);
        }
        
        return Json.createObjectBuilder()
                .add("dataId", this.getDataId())
                .add("city", this.getCity())
                .add("temperature", this.getTemperature())
                .add("visibility", this.getVisibility())
                .add("sunriseTime", this.getSunriseTime())
                .add("sunsetTime", this.getSunsetTime())
                .add("weathercondition", arrayBuilder.build())
                .build();
    }

    

    

    

}
