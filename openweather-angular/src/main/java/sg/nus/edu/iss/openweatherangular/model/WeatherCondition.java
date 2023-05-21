package sg.nus.edu.iss.openweatherangular.model;

import java.io.Serializable;
import jakarta.json.JsonObject;


public class WeatherCondition implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String main;
    private String description;

    
    public WeatherCondition() {}

    

    public WeatherCondition(String main, String description) {
        this.main = main;
        this.description = description;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }


    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}

    public static WeatherCondition createFromJson(JsonObject j) {
        WeatherCondition wc = new WeatherCondition();
        
        wc.setMain(j.getString("main"));
        wc.setDescription(j.getString("description"));
        
        return wc;
    }


}
