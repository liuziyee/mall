package com.dorohedoro.dto;

import lombok.Data;

@Data
public class AddressDTO {
    
    private String receiverName;

    private String phone;

    private String province;

    private String city;

    private String district;

    private String detail;
}
