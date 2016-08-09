package resources;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import redis.clients.jedis.JedisPool;

//Config redis in Spring MVC

@Configuration
public class SpringConfig {

    @Bean
    public JedisPool getJedisPool() throws Exception {
        try {
        	String filename = "license/redis_url.txt";
    		
    		BufferedReader br = new BufferedReader(new FileReader(filename));
    		String url = br.readLine();
    		br.close();
            URI redisURI = new URI(url);
            return new JedisPool(new GenericObjectPoolConfig(),
                    redisURI.getHost(),
                    redisURI.getPort(),
                    600000,
                    redisURI.getUserInfo().split(":",2)[1]);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Redis couldn't be configured from URL in REDISTOGO_URL env var:"+ 
                                        System.getenv("REDISTOGO_URL"));
        } catch (Exception e) {
        	throw new Exception(e);
        }
    
    }

}