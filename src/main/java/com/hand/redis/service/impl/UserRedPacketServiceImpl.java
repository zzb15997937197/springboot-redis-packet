package com.hand.redis.service.impl;

import com.hand.redis.mapper.RedPacketMapper;
import com.hand.redis.mapper.UserRedPacketMapper;
import com.hand.redis.model.RedPacket;
import com.hand.redis.model.UserRedPacket;
import com.hand.redis.service.UserRedPacketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserRedPacketServiceImpl implements UserRedPacketService {

// private Logger logger =
    // LoggerFactory.getLogger(UserRedPacketServiceImpl.class);

    @Autowired
    private UserRedPacketMapper userRedPacketMapper;

    @Autowired
    private RedPacketMapper redPacketMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // 失败
    private static final int FAILED = 0;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int grapRedPacket(Long redPacketId, Long userId) {
        // 获取红包信息
        RedPacket redPacket = redPacketMapper.getRedPacket(redPacketId);
        int leftRedPacket = redPacket.getStock();
        // 当前小红包库存大于0
        if (leftRedPacket > 0) {
            redPacketMapper.decreaseRedPacket(redPacketId);
            // logger.info("剩余Stock数量:{}", leftRedPacket);
            //更新到redis中
            leftRedPacket=leftRedPacket-1;
            stringRedisTemplate.opsForValue().set("stock",String.valueOf(leftRedPacket));
            // 生成抢红包信息
            UserRedPacket userRedPacket = new UserRedPacket();
            userRedPacket.setRedPacketId(redPacketId);
            userRedPacket.setUserId(userId);
            userRedPacket.setAmount(redPacket.getUnitAmount());
            userRedPacket.setNote("redpacket- " + redPacketId);
            // 插入抢红包信息
            int result = userRedPacketMapper.grapRedPacket(userRedPacket);
            return result;
        }
        // logger.info("没有红包啦.....剩余Stock数量:{}", leftRedPacket);
        // 失败返回
        return FAILED;
    }
}
