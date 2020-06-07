package com.hand.redis.web;


import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/callback")
public class RedisStockController {


    @Value("${server.port}")
    private  String port;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    private Redisson redisson;

    /**
     * 测试nginx是否可以代理
     * @return
     */

    @GetMapping
    public String testNginx(){
        return "欢迎你，nginx!";
    }




    @GetMapping("/test01")
    public String LoadBalance(){
        System.out.println("实例的端口为:"+port);
        return "欢迎你，nginx!实例为:"+port;
    }


    @GetMapping("/redis/stuck")
    public String getRedisStuck() throws IOException {
      String lockKey="product001";
       RLock lock= redisson.getLock(lockKey);
        try{
            lock.lock(10,TimeUnit.SECONDS);
            int stock=Integer.parseInt(stringRedisTemplate.opsForValue().get("stock"));
            String str="";
            if (stock>0){
                //思考，如果执行在这时发生宕机时，怎么办？,finally又执行不了，key没有删除，其他线程不能拿到锁，产生死锁。
                int realStock=stock-1;
                stringRedisTemplate.opsForValue().set("stock",realStock+"");
                str="扣减成功，剩余库存:"+realStock;
                System.out.println(str);
            }else{
                str="没有库存啦！";
                System.out.println(str);
            }
        }finally {
            //解锁
            lock.unlock();
        }
      return "end";
    }


}
