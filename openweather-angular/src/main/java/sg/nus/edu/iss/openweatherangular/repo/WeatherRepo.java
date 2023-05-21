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
        this.template.opsForValue().set(weather.getCity(), weather.toJSON().toString(), duration);
    }

    public Optional<Weather> getWeatherFromRedis(String city) throws IOException{
      System.out.println(">>>>>getWeatherFromRedis city>>>"+ city);

        String json = template.opsForValue().get(city);
        System.out.println(">>>>>>>> json from Redis>>>>>>"+json);
        if(null == json|| json.trim().length() <= 0){
          System.out.println(">>>>>>>> I am returning an empty object");
            return Optional.empty();
        }

        System.out.println(">>>>>>>>Data retrieved from Redis Server>>>>>" );

        return Optional.of(Weather.createUserObjectFromRedis(json));
  

    }

}
