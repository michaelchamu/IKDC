package com.example.icarus.ikdc.database;

/**
 * Created by donovan on 3/10/15.
 */
public class Story {
    private int id;
    private String name;

    public Story(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
