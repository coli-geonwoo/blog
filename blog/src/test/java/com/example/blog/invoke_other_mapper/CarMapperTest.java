package com.example.blog.invoke_other_mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class CarMapperTest {

    @Test
    void toDto() {
        Car car = new Car(Date.valueOf(LocalDate.now()));

        CarDto carDto = CarMapper.INSTANCE.toDto(car);
        String expectedDate = new SimpleDateFormat("yyyy-MM-dd").format(car.getManufacturingDate());

        assertThat(carDto.getManufacturingDate()).isEqualTo(expectedDate);
    }
}
