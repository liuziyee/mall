package com.dorohedoro.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dorohedoro.constant.ResCode;
import com.dorohedoro.dto.AddressDTO;
import com.dorohedoro.entity.Address;
import com.dorohedoro.exception.BizException;
import com.dorohedoro.mapper.AddressMapper;
import com.dorohedoro.service.IAddressService;
import com.dorohedoro.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements IAddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public Long createAddress(Address address) {
        addressMapper.insert(address);
        return address.getId();
    }

    @Override
    public List<AddressDTO> getAddressListByUserId(Long userId) {
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getUserId, userId);

        List<Address> addressList = addressMapper.selectList(wrapper);
        return BeanUtil.copyList(addressList, AddressDTO.class);
    }

    @Override
    public AddressDTO getAddressById(Long id) {
        LambdaQueryWrapper<Address> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Address::getId, id);

        Address address = addressMapper.selectOne(wrapper);
        if (address == null) {
            throw new BizException(ResCode.valid);
        }
        return BeanUtil.copy(address, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAddressListByIds(List<Long> ids) {
        List<Address> addressList = addressMapper.selectBatchIds(ids);
        return BeanUtil.copyList(addressList, AddressDTO.class);
    }
}
