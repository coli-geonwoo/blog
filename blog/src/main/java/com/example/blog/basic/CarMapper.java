package com.example.blog.basic;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CarMapper {

    CarMapper INSTANCE = Mappers.getMapper(CarMapper.class);

    @Mapping(source = "make", target = "manufacturer")
    @Mapping(source = "numberOfSeats", target = "seatCount")
    CarDto carToCarDto(Car car);

    //특정 객체를 기준으로 update하는 로직도 가능
    @Mapping(target = "make", source = "manufacturer")
    @Mapping(target = "numberOfSeats", source = "seatCount")
    void updateCarFromDto(CarDto carDto, @MappingTarget Car car);


}
