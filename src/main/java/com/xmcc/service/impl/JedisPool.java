package com.xmcc.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

@Service
public class JedisPool {

    @Autowired
    private ShardedJedisPool jedisPool;

    public ShardedJedis getJedis(){
       return jedisPool.getResource();
    }

    public void cole(ShardedJedis jedis){
        if (jedis != null)
            jedis.close();
    }
}
