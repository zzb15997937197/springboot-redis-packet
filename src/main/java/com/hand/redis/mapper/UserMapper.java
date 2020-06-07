package com.hand.redis.mapper;

import com.hand.redis.model.User;

import java.util.List;

public interface UserMapper {

    List<User> selectAllUser();
}
