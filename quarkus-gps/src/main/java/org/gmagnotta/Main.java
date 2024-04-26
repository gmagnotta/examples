package org.gmagnotta;

import io.quarkus.runtime.annotations.QuarkusMain;

import io.quarkus.runtime.Quarkus;

@QuarkusMain
public class Main {

    public static void main(String... args) {
        Quarkus.run(GpsApp.class, args);
    }
}
