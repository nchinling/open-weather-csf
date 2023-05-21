package sg.nus.edu.iss.openweatherangular.model;

import java.io.Serializable;
import jakarta.json.JsonObject;


public class WeatherCondition implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String mainWeather;
    private String description;

    
    public WeatherCondition() {}

    public WeatherCondition(String mainWeather, String description) {
        this.mainWeather = mainWeather;
        this.description = description;
    }

    public String getMainWeather() {return mainWeather;}
    public void setMainWeather(String mainWeather) {this.mainWeather = mainWeather;}

    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}

    // public static WeatherCondition createFromJson(JsonObject j) {
    //     WeatherCondition wc = new WeatherCondition();
        
    //     // wc.mainWeather = "%s - %s"
    //     // .formatted(j.getString("main"), j.getString("description"));

    //     wc.mainWeather = j.getString("main");
    //     wc.description = j.getString("description");
        

    //     return wc;
    // }

    public static WeatherCondition createFromJson(JsonObject j) {
        WeatherCondition wc = new WeatherCondition();
        
        // wc.mainWeather = "%s - %s"
        // .formatted(j.getString("main"), j.getString("description"));

        wc.setMainWeather(j.getString("main"));
        wc.setDescription(j.getString("description"));
        
        return wc;
    }

    public static WeatherCondition createFromRedisJson(JsonObject j) {
        WeatherCondition wc = new WeatherCondition();
        
        wc.setMainWeather(j.getString("mainWeather"));
        wc.setDescription(j.getString("description"));
        
        return wc;
    }





}
