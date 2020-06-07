package com.hand.redis.mapper;

import com.hand.redis.model.RedPacket;

public interface RedPacketMapper {


    /**
     * 获取红包信息.
     * @param id --红包id
     * @return 红包具体信息
     */
    public RedPacket getRedPacket(Long id);

    /**
     * 扣减抢红包数.
     * @param id -- 红包id
     * @return 更新记录条数
     */
    public int decreaseRedPacket(Long id);




}
