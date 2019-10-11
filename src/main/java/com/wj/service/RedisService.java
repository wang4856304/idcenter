package com.wj.service;

import com.wj.exception.RedisRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author jun.wang
 * @title: RedisService
 * @projectName ownerpro
 * @description: TODO
 * @date 2019/5/7 10:05
 */

@Component
public class RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public static final String UNLOCK_LUA;

    static {
        StringBuilder sb = new StringBuilder();
        sb.append("if redis.call(\"get\",KEYS[1]) == ARGV[1] ");
        sb.append("then ");
        sb.append("    return redis.call(\"del\",KEYS[1]) ");
        sb.append("else ");
        sb.append("    return 0 ");
        sb.append("end ");
        UNLOCK_LUA = sb.toString();
    }

    /**
     * 设置缓存有效期
     * @param key
     * @param time
     * @return
     */
    public boolean expire(String key, long time) {
        if (checkKey(key)) {
            return redisTemplate.expire(key, time, TimeUnit.SECONDS);
        }
        return false;
    }

    /**
     * 根据key 获取过期时间
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(String key) {
        if (checkKey(key)) {
            return redisTemplate.getExpire(key, TimeUnit.SECONDS);
        }
        return 0;
    }

    /**
     * 判断key的存在性
     * @param key
     * @return
     */
    public boolean hasKey(String key) {
        if (checkKey(key)) {
            return redisTemplate.hasKey(key);
        }
        return false;
    }

    /**
     * 删除缓存
     * @param key
     */
    public void delele(String ...key) {
        if (null != key && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            }
            else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }

    /**
     * 获取缓存
     * @param key
     * @return
     */
    public<T> T get(String key) {
        Object value = null;
        if (checkKey(key)) {
            value = redisTemplate.opsForValue().get(key);
        }
        return (T)value;
    }

    /**
     * 设置缓存
     * @param key
     * @param value
     * @return
     */
    public boolean set(String key, Object value) {
        if (checkKey(key)) {
            redisTemplate.opsForValue().set(key, value);
            return true;
        }
        return false;
    }

    /**
     * 设置缓存同时设置有效期
     * @param key
     * @param value
     * @param time
     * @return
     */
    public boolean set(String key, Object value, long time) {
        if (checkKey(key)) {
            redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            return true;
        }
        return false;
    }

    /**
     * 递增
     * @param key
     * @param delta 递增步长
     * @return
     */
    public long incr(String key, long delta) {
        if (checkKey(key)) {
            if (delta <= 0) {
                throw new RedisRuntimeException("delta must be greater than zero");
            }
            return redisTemplate.opsForValue().increment(key, delta);
        }
        return 0;
    }

    /**
     * 递减
     * @param key
     * @param delta 递减步长
     * @return
     */
    public long decr(String key, long delta) {
        if (checkKey(key)) {
            if (delta <= 0) {
                throw new RedisRuntimeException("delta must be greater than zero");
            }
            return redisTemplate.opsForValue().increment(key, -delta);
        }
        return 0;
    }

    /**
     * 获取map中的某个item数据
     * @param key
     * @param item
     * @return
     */
    public<T> T hget(String key, String item) {
        if (checkKey(key)) {
            if (null == item || item.length() == 0) {
                throw new RedisRuntimeException("param item is empty");
            }
            return (T)redisTemplate.opsForHash().get(key, item);
        }
        return null;
    }

    /**
     * 设置map中的某个item数据
     * @param key
     * @param item
     * @param value
     * @return
     */
    public boolean hset(String key, String item, Object value) {
        if (checkKey(key)) {
            if (null == item || item.length() == 0) {
                throw new RedisRuntimeException("param item is empty");
            }
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        }
        return false;
    }

    /**
     * 获取map
     * @param key
     * @return
     */
    public<k, v> Map<k, v>hmget(String key) {
        if (checkKey(key)) {
            return (Map<k, v>)redisTemplate.opsForHash().entries(key);
        }
        return null;
    }

    /**
     * 设置map
     * @param key
     * @param value
     * @return
     */
    public boolean hmset(String key, Map<?, ?> value) {
        if (checkKey(key)) {
            redisTemplate.opsForHash().putAll(key, value);
            return true;
        }
        return false;
    }

    /**
     * 删除hash表中的值
     * @param key
     * @param item
     * @return
     */
    public boolean hdel(String key, Object ...item) {
        if (checkKey(key)) {
            if (null != item) {
                redisTemplate.opsForHash().delete(key, item);
                return true;
            }
        }
        return false;
    }

    /**
     * 判断hash表中item是否存在
     * @param key
     * @param item
     * @return
     */
    public boolean hHasKey(String key, Object item) {
        if (checkKey(key)) {
            if (null != item) {
                return redisTemplate.opsForHash().hasKey(key, item);
            }
        }
        return false;
    }


    /**
     * 获取分布式锁
     * @param key
     * @param value
     * @param time 单位秒
     * @return
     */
    public boolean setLock(String key, String value, long time) {
        if (checkKey(key)) {
            RedisCallback<Boolean> callback = (connection) -> {
                return connection.set(key.getBytes(Charset.forName("UTF-8")), value.getBytes(Charset.forName("UTF-8")), Expiration.seconds(time), RedisStringCommands.SetOption.SET_IF_ABSENT);
            };
            return redisTemplate.execute(callback);
        }
        return false;
    }

    /**
     * 释放分布式锁
     * @param key
     * @param value
     * @return
     */
    public boolean releaseLock(String key,String value) {
        RedisCallback<Boolean> callback = (connection) -> {
            return connection.eval(UNLOCK_LUA.getBytes(), ReturnType.BOOLEAN, 1, key.getBytes(Charset.forName("UTF-8")), value.getBytes(Charset.forName("UTF-8")));
        };
        return redisTemplate.execute(callback);
    }


    private boolean checkKey(String key) {
        if (null == key || key.length() == 0) {
            throw new RedisRuntimeException("key is empty");
        }
        return true;
    }
}
