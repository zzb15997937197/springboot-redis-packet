package com.hand.redis.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Protocol;
import redis.clients.util.SafeEncoder;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.concurrent.Semaphore;

/**
 * Redis拓展set为setnx
 **/
@Component
public class RedisStringOps {

    /**
     * RedisTemplate 装饰器
     * @date 2019/6/11 14:45
     **/
    private static class RedisTemplateHolder {

        /**
         * 最大有20个redis客户端连接被使用，其他的连接要等待令牌释放
         * 令牌数量自己定义，这个令牌是为了避免高并发下，获取redis连接数时，抛出的异常
         * 在压力测试下，性能也很可观
         */
        private static Semaphore semaphore = new Semaphore(20);

        private RedisTemplateHolder() {
        }

        public static RedisTemplate getRedisTemplate(RedisTemplate redisTemplate) {
            try {
                //每个redisTemplate需要在接收连接前获取到许可证，如果没有获取到许可证会进入阻塞状态直等待其他客户端释放连接，
                semaphore.acquire();
                return redisTemplate;
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }

        public static void release() {
            semaphore.release();
        }

        public static Object execute(Statement statement, RedisTemplate<String, Object> redisTemplate) {
            try {
                return statement.prepare(getRedisTemplate(redisTemplate));
            } finally {
                RedisTemplateHolder.release();
            }
        }
    }

    private interface Statement {
        Object prepare(final RedisTemplate redisTemplate);
    }

    @Resource
    private RedisTemplate redisTemplate;


    //redis序列化器
    private static RedisSerializer<String> stringSerializer = new StringRedisSerializer();
    private static RedisSerializer<Object> blobSerializer = new JdkSerializationRedisSerializer();

    /**
     * setNx命令如果key不存在，set key and expire key
     *
     * @param key
     * @param value
     * @param expire
     * @return
     */
    public boolean setAndExpireIfAbsent(final String key, final Serializable value, final long expire) {

        Boolean result = (Boolean) RedisTemplateHolder.execute(new Statement() {
            @Override
            public Object prepare(RedisTemplate redisTemplate) {
                return redisTemplate.execute(new RedisCallback<Boolean>() {
                    @Override
                    public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                        Object obj = connection.execute("set", serialize(key), serialize(value), SafeEncoder.encode("NX"), SafeEncoder.encode("EX"), Protocol.toByteArray(expire));
                        return obj != null;
                    }
                });
            }
        }, redisTemplate);

        return result;
    }

    private <T> Jackson2JsonRedisSerializer<T> configuredJackson2JsonRedisSerializer(Class<T> clazz) {
        Jackson2JsonRedisSerializer<T> serializer = new Jackson2JsonRedisSerializer<T>(clazz);
        ObjectMapper objectMapper = new ObjectMapper();
        // json转实体忽略未知属性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 实体转json忽略null
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        serializer.setObjectMapper(objectMapper);
        return serializer;
    }

    private byte[] serialize(Object object) {
        return serialize(object, SerializeFormat.STRING);
    }

    private byte[] serialize(Object object, SerializeFormat sf) {
        if (object == null) {
            return new byte[0];
        }
        if (sf == SerializeFormat.BLOB) {
            return blobSerializer.serialize(object);
        }
        if (object instanceof String || CacheKeyGenerator.isPrimitive(object.getClass())) {
            //如果是基本类型或者是包装类型，那么再用字符串序列化器
            return stringSerializer.serialize(String.valueOf(object));
        } else {
            return configuredJackson2JsonRedisSerializer(object.getClass()).serialize(object);
        }
    }
}



