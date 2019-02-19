package app.questions.utilities;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class RedisConfiguration {

	
	 @Bean
	 public JedisConnectionFactory redisConnectionFactory() {
	        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
	        jedisConnectionFactory.setUsePool(true);
	        jedisConnectionFactory.setHostName("127.0.0.1");
	        jedisConnectionFactory.setPort(6379);
	        return jedisConnectionFactory;
	    }
	 @Bean(name="redisTemplate")
	 public RedisTemplate<String, String> redisTemplate() {
	        RedisTemplate<String, String> redisTemplate = new RedisTemplate<String, String>();
	        redisTemplate.setConnectionFactory(redisConnectionFactory());
	        redisTemplate.setDefaultSerializer(new StringRedisSerializer());
	        return redisTemplate;
	    }
	 
	 @Bean
	 public ObjectMapper objectMapper() {
	        return new ObjectMapper();
	    }

}
