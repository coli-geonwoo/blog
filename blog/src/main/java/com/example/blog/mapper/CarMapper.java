package com.example.blog.mapper;

import com.example.blog.car.Car;
import com.example.blog.car.CarDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CarMapper {

    CarMapper INSTANCE = Mappers.getMapper(CarMapper.class);

    @Mapping(source= "numberOfSeats", target = "seatCount")
    CarDto carToCarDto(Car car);
}
