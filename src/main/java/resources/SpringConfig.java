package resources;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

//Config redis in Spring MVC

@Configuration
public class SpringConfig {

    @Bean
    public JedisPool getJedisPool() {
        try {
            URI redisURI = new URI("redis://redistogo:46cb163bd7f8cc9d8eb3a84d8cb969f5@viperfish.redistogo.com:10957");
            return new JedisPool(new JedisPoolConfig(),
                    redisURI.getHost(),
                    redisURI.getPort(),
                    0,
                    redisURI.getUserInfo().split(":",2)[1]);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Redis couldn't be configured from URL in REDISTOGO_URL env var:"+ 
                                        System.getenv("REDISTOGO_URL"));
        }
    }

}