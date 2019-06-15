package com.spectre.dependencies.constructor;

public class ThingOne {

    public ThingOne(ThingTwo thingTwo, ThingThree thingThree) {
        System.out.println(thingTwo.toString() + "," + thingThree.toString());
    }

    public ThingOne(String s, int i) {
        System.out.println(s + "," + i);
    }
}
