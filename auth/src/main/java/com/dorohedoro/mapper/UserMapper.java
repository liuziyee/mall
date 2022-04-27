package com.dorohedoro.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dorohedoro.entity.User;

import java.util.Map;

public interface UserMapper extends BaseMapper<User> {

    Map<String, Object> selectMapById(Long id);
}
