package com.hand.redis.web;


import com.hand.redis.util.RedisStringOps;
import com.hand.redis.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

@RestController
@RequestMapping("/redis")
public class RedisDemo01 {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedisStringOps redisStringOps;


    @GetMapping("/connect")
    public String connectRedis(){
       Jedis jedis= RedisUtil.getJedis();
        System.out.println("获取jedis实例:"+jedis);
        System.out.println("k1="+jedis.get("k1"));
        return "success";
    }

    /**
     * 配置文件连接
     */
    @GetMapping("/connect/2")
    public String connectRedisByProperties(){
        Object k1=stringRedisTemplate.opsForValue().get("k1");
     System.out.println("k1="+k1);
        return "success";
    }


    /**
     * 缓存穿透测试,直接穿过缓存，访问数据库，相当于没有缓存。
     */

    @PostMapping("/cache/breakdown")
    public void cacheBreakDown(@RequestParam("username") String username){

       String user= stringRedisTemplate.opsForValue().get(username);
       if (!StringUtils.isEmpty(user)){
           //缓存里没有,查数据库，如果数据库里有那么将值设置到缓存里。
           //stringRedisTemplate.opsForValue().set(username,username);
           stringRedisTemplate.delete(username);
       }else{
           //否则就直接打印
            System.out.println(username);
       }

    }


    /**
     * 缓存击穿,有一个非常热的key，然后在高并发下持续访问该key,该Key因此会击穿
     */


    /**
     * 缓存雪崩，大面积的key在同一时间失效
     */


    /**
     *  setnx命令和expire命令的使用\
     *   设置成功返回1，设置失败返回0
     *   1表示该客户端获取到了锁
     *   0表示该锁已经被其他客户端使用，需要等待其释放锁。
     *   参考博客: https://www.cnblogs.com/zuolun2017/p/8028208.html
     */

    @GetMapping("/test/expire")
    public void redisSetNx(){
       Boolean lock= redisStringOps.setAndExpireIfAbsent("phone","华为",10);
       if (!lock){
           System.out.println("请稍后重试!");
           //throw  new RuntimeException("请稍后重试!");
       }else{
           System.out.println("加锁成功!");
       }




    }

}
