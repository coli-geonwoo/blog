package com.example.blog.decide_constructor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Vehicle {
    private String color;

    protected Vehicle() { }

    // 유일한 public 생성자 채택
    public Vehicle(String color) {
        this.color = color;
    }
}
