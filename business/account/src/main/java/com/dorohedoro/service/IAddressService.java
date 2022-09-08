package com.dorohedoro.service;

import com.dorohedoro.dto.AddressDTO;
import com.dorohedoro.entity.Address;

import java.util.List;

public interface IAddressService {

    Long createAddress(Address address);

    List<AddressDTO> getAddressListByUserId(Long userId);
    
    AddressDTO getAddressById(Long id);

    List<AddressDTO> getAddressListByIds(List<Long> ids);
}
