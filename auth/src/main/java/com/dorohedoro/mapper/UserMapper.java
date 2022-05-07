package com.dorohedoro.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.dorohedoro.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface UserMapper extends BaseMapper<User> {

    Map<String, Object> selectMapById(Long id);

    IPage<User> selectPageByExtraInfo(IPage<User> page, String keyword);
}
