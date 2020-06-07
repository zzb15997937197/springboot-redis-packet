package com.hand.redis.service;


import com.google.common.hash.BloomFilter;
import com.hand.redis.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class BloomFilterService {


    @Autowired
    private UserMapper userMapper;

    private BloomFilter<Integer> bf;

    /**
     * 创建布隆过滤器
     *
     * @PostConstruct：程序启动时候加载此方法，要求没有参数并且返回值为空
     */
    @PostConstruct
    public void initBloomFilter() {
//        List<User> userList = userMapper.selectAllUser();
//        if (CollectionUtils.isEmpty(userList)) {
//            return;
//        }
//        //创建布隆过滤器(默认3%误差)
//        bf = BloomFilter.create(Funnels.integerFunnel(), userList.size());
//        for (User user : userList) {
//            bf.put(user.getId());
//        }
    }

    /**
     * 判断id可能存在于布隆过滤器里面
     *
     * @param id
     * @return
     */
    public boolean userIdExists(int id) {
        return bf.mightContain(id);
    }
}
