package com.example.blog.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Car {

    private String make;
    private int numberOfSeats;
    private CarType type;

    //constructor, getters, setters etc.
}
