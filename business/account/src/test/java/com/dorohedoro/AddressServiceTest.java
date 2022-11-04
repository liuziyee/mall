package com.dorohedoro;

import com.alibaba.fastjson.JSON;
import com.dorohedoro.util.UserContext;
import com.dorohedoro.dto.AddressDTO;
import com.dorohedoro.dto.UserDTO;
import com.dorohedoro.entity.Address;
import com.dorohedoro.service.IAddressService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class AddressServiceTest {

    @Autowired
    private IAddressService addressService;
    
    @Before
    public void init() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(12L);
        UserContext.setUserData(userDTO);
    }
    
    @Test
    public void createAddress() {
        Address address = new Address();
        address.setUserId(UserContext.getUserData().getId());
        address.setReceiverName("liuziye");
        address.setPhone("10000000000");
        address.setProvince("陕西");
        address.setCity("渭南");
        address.setDistrict("大荔");

        Long id = addressService.createAddress(address);
        log.info("address id: {}", id);
    }
    
    @Test
    public void getAddressByUID() {
        Long uid = UserContext.getUserData().getId();
        List<AddressDTO> addressDTOList = addressService.getAddressListByUserId(uid);
        log.info("address info by uid {}: {}", uid, JSON.toJSONString(addressDTOList));
    }
    
    @Test
    public void getAddressByIds() {
        List<Long> ids = Collections.singletonList(10L);
        List<AddressDTO> addressDTOList = addressService.getAddressListByIds(ids);
        log.info("address info by ids {}: {}", ids, JSON.toJSONString(addressDTOList));
    }
}
