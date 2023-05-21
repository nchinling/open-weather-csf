package sg.nus.edu.iss.openweatherangular.service;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import sg.nus.edu.iss.openweatherangular.model.Weather;
import sg.nus.edu.iss.openweatherangular.repo.WeatherRepo;

@Service
public class WeatherService {
    @Autowired
    private WeatherRepo weatherrepo;

    @Value("${open.weather.url}")
    private String openWeatherUrl;

    @Value("${open.weather.key}")
    private String openWeatherApiKey;
 
    private Random rand = new SecureRandom();

    public Integer getRandom(int min, int max, int count){
        List<Integer> result = new LinkedList<>();
        for (int i = 0; i < count; i++)
            result.add(rand.nextInt(max) + min +1);
        return result.get(0);
    }


    public void save(final Weather weather){
        String orderId = UUID.randomUUID().toString().substring(0,8);
        weather.setDataId(orderId);
        weatherrepo.save(weather);
    }

    public Optional<Weather> getWeatherFromRedis(String city) throws IOException{
        System.out.println(">>>>>>>> I am in Redis service>>>>>>");
        return weatherrepo.getWeatherFromRedis(city);
    }  

   //function to get info from an external server using API.
   public Optional<Weather> getWeatherFromAPI(String city, String unitMeasurement, String language)
   throws IOException{
       System.out.println("openWeatherUrl: " + openWeatherUrl);
       System.out.println("openWeatherApiKey: " + openWeatherApiKey);
   
       String weatherUrl = UriComponentsBuilder
                           .fromUriString(openWeatherUrl)
                           .queryParam("q", city.replaceAll(" ", "+"))
                           .queryParam("units", unitMeasurement)
                           .queryParam("appId", openWeatherApiKey)
                           .queryParam("lang", language)
                           .toUriString();

       RestTemplate template= new RestTemplate();
       ResponseEntity<String> r  = template.getForEntity(weatherUrl, 
               String.class);
       //r.getBody is a string response from api.
       Weather w = Weather.createUserObject(r.getBody());
       String cityName = w.getCity();
       String temperature = w.getTemperature();

       System.out.println(">>>cityName: " + cityName);
       System.out.println(">>>temperature: " + temperature);
       if(w != null)
           return Optional.of(w);
       return Optional.empty();

   }
}
