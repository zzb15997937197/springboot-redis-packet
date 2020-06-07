package com.hand.redis.mapper;

import com.hand.redis.model.UserRedPacket;

public interface UserRedPacketMapper {

    /**
     * 插入抢红包信息.
     * @param userRedPacket ——抢红包信息
     * @return 影响记录数.
     */
    public int grapRedPacket(UserRedPacket userRedPacket);
}
