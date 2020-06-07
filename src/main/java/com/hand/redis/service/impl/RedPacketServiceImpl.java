package com.hand.redis.service.impl;

import com.hand.redis.mapper.RedPacketMapper;
import com.hand.redis.model.RedPacket;
import com.hand.redis.service.RedPacketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service
public class RedPacketServiceImpl implements RedPacketService {

    @Autowired
    RedPacketMapper redPacketMapper;

    @Override
    @Transactional(isolation= Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public RedPacket getRedPacket(Long id) {
        return redPacketMapper.getRedPacket(id);
    }

    @Override
    @Transactional(isolation=Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int decreaseRedPacket(Long id) {
        return redPacketMapper.decreaseRedPacket(id);
    }

}
