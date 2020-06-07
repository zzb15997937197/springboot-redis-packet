package com.hand.redis.service;

import com.hand.redis.model.RedPacket;

public interface RedPacketService {

    /**
     * 获取红包
     *
     * @param id ——编号
     * @return 红包信息
     */
    public RedPacket getRedPacket(Long id);

    /**
     * 扣减红包
     *
     * @param id——编号
     * @return 影响条数.
     */
    public int decreaseRedPacket(Long id);

}
