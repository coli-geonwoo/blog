package com.example.blog.decide_constructor;



public class Truck {

    private String make;
    private String color;

    public Truck() { }

    // @Default가 붙어있으므로 채택
    @Default
    public Truck(String make, String color) {
        this.make = make;
        this.color = color;
    }
}
