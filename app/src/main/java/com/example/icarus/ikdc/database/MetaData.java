package com.example.icarus.ikdc.database;

/**
 * Created by donovan on 3/10/15.
 */
public class MetaData {

    private int id;
    private String media_type;
    private String timestamp;
    private int gps_id;
    private int storage_id;
    private int activity_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMedia_type() {
        return media_type;
    }

    public void setMedia_type(String media_type) {
        this.media_type = media_type;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getGps_id() {
        return gps_id;
    }

    public void setGps_id(int gps_id) {
        this.gps_id = gps_id;
    }

    public int getStorage_id() {
        return storage_id;
    }

    public void setStorage_id(int storage_id) {
        this.storage_id = storage_id;
    }

    public int getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(int activity_id) {
        this.activity_id = activity_id;
    }
}
