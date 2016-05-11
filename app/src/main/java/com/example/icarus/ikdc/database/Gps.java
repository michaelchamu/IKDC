package com.example.icarus.ikdc.database;

/**
 * Created by donovan on 3/10/15.
 */
public class Gps {

    private int id;
    private long latitude;
    private long longitude;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }

    public long getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }
}
