package com.example.blog.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.blog.domain.Car;
import com.example.blog.domain.CarDto;
import com.example.blog.domain.CarType;
import org.junit.jupiter.api.Test;

class CarMapperTest {

    @Test
    void carToCarDto() {
        //given
        Car car = new Car( "Morris", 5, CarType.SEDAN );

        //when
        CarDto carDto = CarMapper.INSTANCE.carToCarDto( car );

        //then
        assertThat( carDto ).isNotNull();
        assertThat( carDto.getManufacturer() ).isEqualTo( "Morris" );
        assertThat( carDto.getSeatCount() ).isEqualTo( 5 );
        assertThat( carDto.getType() ).isEqualTo( "SEDAN" );
    }

    @Test
    void updateMethod() {
        Car car = new Car( "Morris", 5, CarType.SEDAN );
        CarDto carDto = new CarDto("Coli", 3, CarType.SUV.name());

        //when
        CarMapper.INSTANCE.updateCarFromDto(carDto, car);

        //then
        assertThat( car.getMake()).isEqualTo(carDto.getManufacturer());
        assertThat( car.getNumberOfSeats() ).isEqualTo( carDto.getSeatCount() );
        assertThat( car.getType().name() ).isEqualTo("SUV");
    }
}
