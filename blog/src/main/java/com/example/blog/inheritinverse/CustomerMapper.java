package com.example.blog.inheritinverse;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CustomerMapper {

    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    @Mapping(source = "name", target ="customerName")
    CustomerDto toDto(Customer customer);

    @InheritInverseConfiguration
    Customer toCustomer(CustomerDto customerDto);
}
