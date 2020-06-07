package com.hand.redis.web;


import com.hand.redis.service.UserRedPacketService;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/userRedPacket")
public class UserRedPacketController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private Redisson redisson;

    @Autowired
    private UserRedPacketService userRedPacketService;

    @RequestMapping(value = "/grapRedPacket.do",method = RequestMethod.POST)
    public String grapRedPacket(@RequestParam("redPacketId") Long redPacketId,
                                             @RequestParam("userId") Long userId) throws InterruptedException{
        // 抢红包
        String lockey="packet";
        RLock lock=redisson.getLock(lockey);
        Map<String, Object> retMap = new HashMap<String, Object>();
        Boolean isLock=false;
        try {
            //尝试获取到锁，如果锁正在被使用，那么给用户提示，请稍后重试！ 我们需要考虑到的是避免其他客户端也能拿到锁的情况。
             isLock=lock.tryLock(10, TimeUnit.SECONDS);
            if (!isLock){
                return "人太多了，请稍后重试！";
            }
            int result = userRedPacketService.grapRedPacket(redPacketId, userId);
            boolean flag = result > 0;
            retMap.put("success", flag);
            retMap.put("message", flag ? "抢红包成功" : "抢红包失败");
        }finally {
            if (isLock){
                lock.unlock();
            }
        }
        return retMap.toString();
    }

}
