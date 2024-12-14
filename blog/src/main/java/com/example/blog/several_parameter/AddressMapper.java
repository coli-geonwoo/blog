package com.example.blog.several_parameter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface AddressMapper {

    //다양한 파라미터를 받을 경우 명확히 지정 > 모호할 경우 에러 발생 가능
    //파라미터가 null일 경우 mapping되는 값도 null이 된다
    @Mapping(target = "description", source = "person.description")
    @Mapping(target = "houseNumber", source = "address.houseNo")
    DeliveryAddressDto personAndAddressToDeliveryAddressDto(Person person, Address address);

    //직접 바인딩도 가능
    @Mapping(target = "description", source = "person.description")
    @Mapping(target = "houseNumber", source = "hn")
    DeliveryAddressDto personAndAddressToDeliveryAddressDto_V2(Person person, Integer hn);
}
