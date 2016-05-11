package com.example.icarus.ikdc.database;

/**
 * Created by donovan on 3/10/15.
 */
public class Activity {

    private int id;
    private long storage_id;
    private long story_id;

    public void setId(int id) {
        this.id = id;
    }

    public void setStorage_id(long storage_id) {
        this.storage_id = storage_id;
    }

    public void setStory_id(long story_id) {
        this.story_id = story_id;
    }

    public int getId() {
        return id;
    }

    public long getStorage_id() {
        return storage_id;
    }

    public long getStory_id() {
        return story_id;
    }
}
