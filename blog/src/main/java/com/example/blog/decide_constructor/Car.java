package com.example.blog.decide_constructor;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Car {

    private String make;
    private String color;

    //기본 생성자가 열려 있을 경우 > 기본 생성자 활용
    public Car() { }

    public Car(String make, String color) {
        this.make = make;
        this.color = color;
    }
}
