package com.xmcc.service.impl;

import com.xmcc.beans.CachePreFix;
import com.xmcc.exception.ParamException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ShardedJedis;

@Service
@Slf4j
public class CacheService {

    @Autowired
    private JedisPool jedisPool;

    /**
     * 设置缓存
     * @param data 待设置的数据
     * @param timeOut 超时时间
     * @param key 存入内存的key值，因为数据是以key—value形式存入内存
     * @param preFix 为了符合实际开发需求，采用给key添加前缀的方式来更贴切实际开发
     */
    public void setCache(String data, int timeOut, String key, CachePreFix preFix) {
        if (StringUtils.isBlank(data)) { return; }

        ShardedJedis jedis = null;
        try {
            jedis = jedisPool.getJedis();
            jedis.setex(concatKey(key, preFix), timeOut, data);
        } catch (Exception e) {
            log.info(e.getMessage());
        }finally {
            jedisPool.cole(jedis);
        }
    }

    /**
     * 读取缓存
     * @param key
     * @param preFix
     * @return 返回读取的数据
     */
    public String readCache(String key, CachePreFix preFix) {
        ShardedJedis jedis = null;
        String data = null;
        try {
            jedis = jedisPool.getJedis();
            data = jedis.get(concatKey(key, preFix));
        } catch (Exception e) {
            log.info(e.getMessage());
        } finally {
            jedisPool.cole(jedis);
        }
        return data;
    }

    //拼接key值
    public String concatKey(String key, CachePreFix preFix){
        if (StringUtils.isBlank(key)){
            throw new ParamException("key值为空，请设置！");
        }
        return preFix.name()+"_"+key;
    }
}
