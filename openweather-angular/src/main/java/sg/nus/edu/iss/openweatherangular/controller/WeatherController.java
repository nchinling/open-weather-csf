package sg.nus.edu.iss.openweatherangular.controller;

import java.io.IOException;
import java.io.StringReader;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import sg.nus.edu.iss.openweatherangular.model.Weather;
import sg.nus.edu.iss.openweatherangular.service.WeatherService;

@Controller
@RequestMapping(path="/api", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "http://localhost:4200")
// @CrossOrigin(origins = "*")
public class WeatherController {
    
    @Autowired
    private WeatherService weatherSvc;

    @GetMapping(path="/weather")
    @ResponseBody
    public ResponseEntity<String> getWeather(@RequestParam(required=true) String city,
    @RequestParam(defaultValue = "metric",required=false) String units, 
    @RequestParam(defaultValue = "en",required=false) String language) throws IOException{
        // Integer num = weatherSvc.getWeather(city);
        System.out.println("I am in Weather server");
        System.out.println(">>>>>>>>City in controller>>>>>" + city);
        
        Optional<Weather> wr = weatherSvc.getWeatherFromRedis(city);
        if (wr.isPresent()){
            Weather weather = wr.get();
            System.out.println("Obtained weather data from Redis");
            //no need to save
            // weatherSvc.save(weather);

            String sunriseTime = DateTimeConverter(weather.getSunrise());
            String sunsetTime = DateTimeConverter(weather.getSunset());
            JsonObject resp = Json.createObjectBuilder()
                .add("city", weather.getCity())
                .add("temperature", weather.getTemperature())
                .add("visibility",weather.getVisibility() )
                .add("sunrise", sunriseTime)
                .add("sunset", sunsetTime)
                .add("description", weather.getWeathercondition().get(0).getDescription())
                .add("mainWeather", weather.getWeathercondition().get(0).getMain())
                .build();
                System.out.println(">>>FromRedisresp: " + resp);
            
            return ResponseEntity.ok(resp.toString());
        }
        
        Optional<Weather> w = weatherSvc.getWeatherFromAPI(city, units, language);
        if (w.isPresent()) {
            Weather weather = w.get();
            weatherSvc.save(weather);
            System.out.println("Obtained weather data from API");
            String sunriseTime = DateTimeConverter(weather.getSunrise());
            String sunsetTime = DateTimeConverter(weather.getSunset());

            JsonObject resp = Json.createObjectBuilder()
                .add("city", weather.getCity())
                .add("temperature", weather.getTemperature())
                .add("visibility",weather.getVisibility() )
                .add("sunrise", sunriseTime)
                .add("sunset", sunsetTime)
                .add("description", weather.getWeathercondition().get(0).getDescription())
                .add("mainWeather", weather.getWeathercondition().get(0).getMain())
                .build();
                System.out.println(">>>resp: " + resp);
            
            return ResponseEntity.ok(resp.toString());
        } 
        // Handle the case when the Optional is empty
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body("Weather information not available for the provided city.");
        
        
    }


    private String DateTimeConverter(long epochTime){
        // Convert epoch time to LocalDateTime
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(epochTime), ZoneId.systemDefault());

        // Format LocalDateTime to a readable string
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentTime = dateTime.format(formatter);

        return currentTime;
    }

    //for practice: generates a single random number
    // @GetMapping(path="/weather")
    // @ResponseBody
    // public ResponseEntity<String> getRandom(@RequestParam(defaultValue="10") Integer count){
    //     Integer num = weatherSvc.getRandom(0,1000,count);
    //     System.out.println("I am in server3");
    //     JsonObject resp = Json.createObjectBuilder()
    //         .add("number", num)
    //         .build();
            
    //     return ResponseEntity.ok(resp.toString());
    // }


    //POST/api/weather
    //Content-Type: application/json
   
    @PostMapping(path="/weather", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> postPersonData(@RequestBody String payload){
        
        System.out.printf(">>> in postPersonDataNew\n");

        JsonReader reader = Json.createReader(new StringReader(payload));
        JsonObject req = reader.readObject();
        String name = req.getString("name");
        String address = req.getString("address");
        Integer age = req.getInt("age");

        System.out.println("Name: " + name);
        System.out.println("Address: " + address);
        System.out.println("Age: " + age);

        String message = "Life is beautifuls";

        JsonObject resp = Json.createObjectBuilder()
              .add("message", message)
              .add("timestamp", (new Date()).toString())
              .build();
  
        return ResponseEntity.ok(resp.toString());
    }

    // POST /api/weather
    // Content-Type: application/x-www-form-urlencoded
    @PostMapping(path="/weather", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    public ResponseEntity<String> postRandomForm(@RequestBody MultiValueMap<String, String> form){
        System.out.printf(">>> in postRandomForm\n");

       String name = form.getFirst("name");
       String address= form.getFirst("address");
       int age = getValue(form.getFirst("age"), 10);

       System.out.println("Name: " + name);
       System.out.println("Address: " + address);
       System.out.println("Age: " + age);
  
        // List<Integer> nums = weatherSvc.getRandomNumbers(min, max, count);
       
        String message = "Life is beautifuls";

        JsonObject resp = Json.createObjectBuilder()
              .add("message", message)
              .add("timestamp", (new Date()).toString())
              .build();
  
        return ResponseEntity.ok(resp.toString());
    }


        //need to use parseInt for age as form data is in string
        private int getValue(String value, int defaultValue){
            if(value.trim().length()<=0 || (null==value))
                return defaultValue;
            return Integer.parseInt(value);
        }

}






