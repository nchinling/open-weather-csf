package sg.nus.edu.iss.openweatherangular.repo;

import java.io.IOException;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import sg.nus.edu.iss.openweatherangular.model.Weather;



@Repository
public class WeatherRepo {
    
      //autowired in a bean.
      @Autowired @Qualifier("weatherbean")

      private RedisTemplate<String, String> template;


    public void save(Weather weather){
      	//implement cookie of 30 mins
        int cookieTime = 2;
        Duration duration = Duration.ofMinutes(cookieTime);
        System.out.println(">>>>>>weather.getCity>>>"+ weather.getCity());
        this.template.opsForValue().set(weather.getCity(), weather.toJSON().toString());
    }

    public Optional<Weather> getWeatherFromRedis(String city) throws IOException{
      System.out.println(">>>>>getWeatherFromRedis city>>>"+ city);

      String[] cityWords = city.split("\\s");
      String capitalizedCity = "";
      for (String word : cityWords) {
          capitalizedCity += Character.toUpperCase(word.charAt(0)) + word.substring(1) + " ";
      }

      System.out.println(">>>>>>>capitalizedCity>>>>>>>" + capitalizedCity );
     
        String jsonCity = template.opsForValue().get(capitalizedCity);
        System.out.println(">>>>>>>> json from Redis>>>>>>"+jsonCity);
        if(null == jsonCity|| jsonCity.trim().length() <= 0){
          System.out.println(">>>>>>>> I am returning an empty object");
            return Optional.empty();
        }

        System.out.println(">>>>>>>>Data retrieved from Redis Server>>>>>" );

        //creates a Weather object. 
        return Optional.of(Weather.createUserObjectFromRedis(jsonCity));
    }

    // public List<Weather> findAll(int startIndex) throws IOException{
    //     Set<String> allKeys = template.keys("*");
    //     List<Weather> weatherarray = new LinkedList<>();
    //     for (String key : allKeys) {
    //         String result = template.opsForValue().get(key);
    //         System.out.println(">>>>>>>>" + result);

    //         weatherarray.add(Weather.createUserObjectFromRedis(result));
    //     }

    //     return weatherarray;

    // }


}
