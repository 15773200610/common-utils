package com.xcbeyond.common.data.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * redis操作工具类.
 * (基于RedisTemplate)
 * @Auther: xcbeyond
 * @Date: 2019/1/16 16:13
 */
@Component
public class RedisUtils {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 读取缓存
     * @param key
     * @return
     */
    public String get(final String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 获取所有以prefix开头的key
     * @param prefix 前缀key
     * @return
     */
    public Set<String> getKeysByPrefix(String prefix){
        return redisTemplate.keys(prefix + "*");
    }

    /**
     * 写入缓存
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, String value) {
        boolean result = false;
        try {
            redisTemplate.opsForValue().set(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 写入缓存并设置缓存过期时间
     * @param key
     * @param value
     * @param expiration 过期时间，秒
     * @return
     */
    public boolean set(final String key, String value,long expiration) {
        boolean result = false;
        try {
            redisTemplate.opsForValue().set(key, value, expiration, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 写入缓存,并带自动序列的key
     * @param key
     * @param value
     * @return
     */
    public boolean setAutoKey(final String key, String value) {
        boolean result = false;
        try {
            RedisAtomicLong entityIdCounter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
            Long increment = entityIdCounter.getAndIncrement();

            redisTemplate.opsForValue().set(key + ":" + increment, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 是否包含key
     * @param key
     * @return
     */
    public boolean hasKey(final String key) {
        boolean result = false;
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 更新缓存
     * @param key
     * @param value
     * @return
     */
    public boolean getAndSet(final String key, String value) {
        boolean result = false;
        try {
            redisTemplate.opsForValue().getAndSet(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 删除缓存
     * @param key
     * @return
     */
    public boolean delete(final String key) {
        boolean result = false;
        try {
            redisTemplate.delete(key);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 按照key的前缀删除
     * @param prefix 前缀key
     * @return
     */
    public void deleteByPrefix(final String prefix) {
        Set<String> keys = redisTemplate.keys(prefix + "*");
        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 写入 list 以元素的形式
     * @param key
     * @param value
     * @return
     */
    public boolean addList(final String key, String value) {
        boolean result = false;
        try {
            ListOperations<String, String> list = redisTemplate.opsForList();
            list.rightPush(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 直接写入 list
     * @param key
     * @param list
     * @return
     */
    public boolean addListAll(final String key, List<String> list) {
        boolean result = false;
        try {
            ListOperations<String, String> list2 = redisTemplate.opsForList();
            list2.rightPushAll(key, list);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 删除list中元素
     * @param key
     * @param value
     * @return
     */
    public boolean deleteListEm(final String key, String value) {
        boolean result = false;
        try {
            ListOperations<String, String> list2 = redisTemplate.opsForList();
            list2.remove(key, 1, value); //将删除list中存储在列表中第一次出现的  value
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 判断是否存在key
     * @param key
     * @return
     */
    public boolean isExsitKey(final String key){
        return redisTemplate.hasKey(key);
    }

    /**
     * 增加hash结构的数据
     * @param key
     * @param map
     * @return
     */
    public boolean addHash(final String key, Map<String, Object> map){
        boolean result = false;
        try {
            redisTemplate.opsForHash().putAll(key, map);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 删除Hash结构的数据
     * @param key
     * @param mapKey
     * @return
     */
    public boolean delHash(final String key, String mapKey){
        boolean result = false;
        try {
            redisTemplate.opsForHash().delete(key, mapKey);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取hash中的值
     * @param key
     */
    public Map<Object, Object> getHashSet(final String key) {
        return redisTemplate.opsForHash().entries(key);
    }
}
