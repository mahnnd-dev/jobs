package dev.m.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class RedisConfig {

    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    @Value("${redis.sentinel.master-name}")
    private String redisMaterName;

    @Value("${redis.connect-timeout}")
    private int connectTimeout;

    @Value("${redis.password}")
    private String password;

    @Value("${redis.pool-config.max-total}")
    private int maxTotal;

    @Value("${redis.pool-config.max-idle}")
    int maxIdle;

    @Value("${redis.pool-config.min-idle}")
    int minIdle;

    @Value("${redis.pool-config.max-wait-millis:20000}")
    long maxWait;

    @Value("${redis.pool-config.jmx-enable}")
    boolean jmxEnable;

    @Bean
    public JedisSentinelPool jedisSentinelPool() {
        JedisSentinelPool sentinelPool = new JedisSentinelPool(redisMaterName, this.sentinelNodes(), this.jedisPoolConfig(), connectTimeout, password);
        logger.info(">>>> Init {} at: {}, Master: {}:{}", "JedisSentinelPool", LocalDateTime.now(), sentinelPool.getCurrentHostMaster().getHost(), sentinelPool.getCurrentHostMaster().getPort());
        return sentinelPool;
    }

    @Bean
    @ConfigurationProperties(prefix = "redis.sentinel.node")
    public Set<String> sentinelNodes() {
        return new HashSet<>();
    }

    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setMaxWaitMillis(maxWait);
        config.setJmxEnabled(jmxEnable);
        return config;
    }

}


