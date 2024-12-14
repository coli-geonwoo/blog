package com.example.blog.several_parameter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DeliveryAddressDto {

    private final String description;
    private final String houseNumber;
}
