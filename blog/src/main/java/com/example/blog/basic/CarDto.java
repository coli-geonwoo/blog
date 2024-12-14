package com.example.blog.basic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CarDto {

    private String manufacturer;
    private int seatCount;
    private String type;

    //constructor, getters, setters etc.
}
