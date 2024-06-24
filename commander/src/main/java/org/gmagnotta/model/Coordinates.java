package org.gmagnotta.model;

public class Coordinates {

    private double altitude, latitude, longitude;

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String toString() {
        return "altitude: " + altitude + "; latitude: " + latitude +
        "; longitude " + longitude;
    }
    
}
