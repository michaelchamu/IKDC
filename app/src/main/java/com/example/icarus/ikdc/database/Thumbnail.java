package com.example.icarus.ikdc.database;

/**
 * Created by donovan on 3/1/15.
 */
public class Thumbnail {
    private int id;
    private String thmb_name;
    private long stry_id;

    public int getID (){
        return id;
    }

    public String getThmb_name () {
        return thmb_name;
    }

    public long getStry_id () {
        return stry_id;
    }

    public void setID(int id_num){
        id = id_num;
    }

    public void setThmb_name(String name){
        thmb_name = name;
    }

    public void setStry_id(long loc){
        stry_id = loc;
    }
}
