package com.spectre.dependencies.setter;

import com.spectre.dependencies.constructor.ThingTwo;

public class ThingFour {
    private ThingTwo thingTwo;

    private String s;

    public ThingTwo getThingTwo() {
        return thingTwo;
    }

    public void setThingTwo(ThingTwo thingTwo) {
        this.thingTwo = thingTwo;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    @Override
    public String toString() {
        return "ThingFour{" +
                "thingTwo=" + thingTwo +
                ", s='" + s + '\'' +
                '}';
    }
}
