package com.example.blog.several_parameter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class AddressMapperTest {

    @Test
    void personAndAddressToDeliveryAddressDto() {
        Address address = new Address(3);
        Person person = new Person("testPerson");

        AddressMapper mapper = Mappers.getMapper(AddressMapper.class);
        DeliveryAddressDto deliveryAddressDto = mapper.personAndAddressToDeliveryAddressDto(person, address);

        assertThat(deliveryAddressDto.getDescription()).isEqualTo(person.getDescription());
        assertThat(deliveryAddressDto.getHouseNumber()).isEqualTo(String.valueOf(address.getHouseNo()));
    }

    @Test
    void personAndAddressToDeliveryAddressDto_V2() {
        Integer hn = 3;
        Person person = new Person("testPerson");

        AddressMapper mapper = Mappers.getMapper(AddressMapper.class);
        DeliveryAddressDto deliveryAddressDto = mapper.personAndAddressToDeliveryAddressDto_V2(person, hn);

        assertThat(deliveryAddressDto.getDescription()).isEqualTo(person.getDescription());
        assertThat(deliveryAddressDto.getHouseNumber()).isEqualTo(String.valueOf(hn));
    }


}
