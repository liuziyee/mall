package com.dorohedoro.service;

import com.dorohedoro.dto.AddressDTO;
import com.dorohedoro.entity.Address;

import java.util.List;

public interface IAddressService {

    Long createAddress(Address address);

    List<AddressDTO> getAddressListByUID(Long uid);

    AddressDTO getAddressById(Long id);

    List<AddressDTO> getAddressListByIds(List<Long> ids);
}
