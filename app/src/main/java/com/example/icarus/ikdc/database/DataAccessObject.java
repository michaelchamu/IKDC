package com.example.icarus.ikdc.database;

/**
 * Created by donovan on 3/1/15.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DataAccessObject {

    private SQLiteDatabase database;
    private SQLiteHelper dataHelper;
    private String[] allColumns = {
            dataHelper.id_column,
            dataHelper.thmb_name_column,
            dataHelper.story_id_column
    };

    public DataAccessObject(Context context){
        dataHelper = new SQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dataHelper.getWritableDatabase();
    }

    public void close() {
        dataHelper.close();
    }

    public Thumbnail createThmb(String thmb_name, long stry_id) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.thmb_name_column, thmb_name);
        values.put(SQLiteHelper.story_id_column, stry_id);
        long insertId = database.insert(SQLiteHelper.thmb_table, null,
                values);
        Cursor cursor = database.query(SQLiteHelper.thmb_table,
                allColumns, SQLiteHelper.id_column + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Thumbnail newThmb = cursorToThumb(cursor);
        cursor.close();
        return newThmb;
    }

    public long createCImage(String name, String loc) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.common_images_name, name);
        values.put(SQLiteHelper.common_images_location, loc);
        long insertId = database.insert(SQLiteHelper.commonImages_Table, null,
                values);
        return insertId;
    }

    public int retrieveImageNum(){
        Cursor storyNum = database.rawQuery("SELECT MAX(ID) FROM " + SQLiteHelper.commonImages_Table, null);
        storyNum.moveToFirst();
        if(storyNum != null){
            int num = storyNum.getInt(0);
            storyNum.close();
            return num;
        }else{
            storyNum.close();
            return 0;
        }
    }

    public long createCVideo(String name, String loc) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.common_videos_name, name);
        values.put(SQLiteHelper.common_videos_location, loc);
        long insertId = database.insert(SQLiteHelper.commonVideos_Table, null,
                values);
        return insertId;
    }

    public int retrieveVideoNum(){
        Cursor storyNum = database.rawQuery("SELECT MAX(ID) FROM " + SQLiteHelper.commonVideos_Table, null);
        storyNum.moveToFirst();
        if(storyNum != null){
            int num = storyNum.getInt(0);
            storyNum.close();
            return num;
        }else{
            storyNum.close();
            return 0;
        }
    }

    public long createCAudio(String name, String loc) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.common_audios_name, name);
        values.put(SQLiteHelper.common_audios_location, loc);
        long insertId = database.insert(SQLiteHelper.commonAudios_Table, null,
                values);
        return insertId;
    }

    public int retrieveAudioNum(){
        Cursor storyNum = database.rawQuery("SELECT MAX(ID) FROM " + SQLiteHelper.commonAudios_Table, null);
        storyNum.moveToFirst();
        if(storyNum != null){
            int num = storyNum.getInt(0);
            storyNum.close();
            return num;
        }else{
            storyNum.close();
            return 0;
        }
    }

    public long createCText(String name, String loc) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.common_texts_name, name);
        values.put(SQLiteHelper.common_texts_location, loc);
        long insertId = database.insert(SQLiteHelper.commonTexts_Table, null,
                values);
        return insertId;
    }

    public int retrieveTextNum(){
        Cursor storyNum = database.rawQuery("SELECT MAX(ID) FROM " + SQLiteHelper.commonTexts_Table, null);
        storyNum.moveToFirst();
        if(storyNum != null){
            int num = storyNum.getInt(0);
            storyNum.close();
            return num;
        }else{
            storyNum.close();
            return 0;
        }
    }

    public void deleteThmb(Thumbnail thmb) {
        int id = thmb.getID();
        System.out.println("Thumbnail deleted with id: " + id);
        database.delete(SQLiteHelper.thmb_table, SQLiteHelper.id_column
                + " = " + id, null);
    }

    public List<Thumbnail> getAllThumbnails() {
        List<Thumbnail> thmbs = new ArrayList<Thumbnail>();

        Cursor cursor = database.query(SQLiteHelper.thmb_table,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Thumbnail thmb = cursorToThumb(cursor);
            thmbs.add(thmb);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return thmbs;
    }

    private Thumbnail cursorToThumb(Cursor cursor) {
        Thumbnail thumb = new Thumbnail();
        thumb.setID(cursor.getInt(0));
        thumb.setThmb_name(cursor.getString(1));
        thumb.setStry_id(cursor.getInt(2));
        return thumb;
    }

    public int retrieveStoryNum(){
        Cursor storyNum = database.rawQuery("SELECT MAX(ID) FROM " + SQLiteHelper.story_table, null);
        storyNum.moveToFirst();
        if(storyNum != null){
            int num = storyNum.getInt(0);
            storyNum.close();
            return num;
        }else{
            storyNum.close();
            return 0;
        }
    }

    public int retrieveThumbNum(){
        Cursor storyNum = database.rawQuery("SELECT MAX(ID) FROM " + SQLiteHelper.thmb_table, null);
        storyNum.moveToFirst();
        if(storyNum != null){
            int num = storyNum.getInt(0);
            storyNum.close();
            return num;
        }else{
            storyNum.close();
            return 0;
        }
    }

    public int getActivityCount(int stry_id){
        Cursor storyNum = database.rawQuery("SELECT COUNT(ID) FROM " +
                SQLiteHelper.activity_table + " WHERE " + SQLiteHelper.story_id + " = " + stry_id, null);
        storyNum.moveToFirst();
        if(storyNum != null){
            int num = storyNum.getInt(0);
            storyNum.close();
            return num;
        }else{
            storyNum.close();
            return 0;
        }
    }

    public long createStory(Story thisStory){
        try{
            ContentValues values = new ContentValues();
            values.put(SQLiteHelper.stry_name, thisStory.getName());
            long insertId = database.insert(SQLiteHelper.story_table, null,
                    values);
            return insertId;
        }
        catch(Exception e){
            return 0;
        }
    }

    public Story getStory(int id){

        try{
            Story returnStory;

            Cursor story = database.rawQuery("SELECT * FROM " +
                    SQLiteHelper.story_table + " WHERE " + SQLiteHelper.stry_id_column + " = " + id, null);
            story.moveToFirst();
            if(story != null){
                returnStory = new Story(story.getInt(0), story.getString(1));
                story.close();
                return returnStory;
            }else{
                story.close();

                return null;
            }
        } catch (Exception e){
            return null;
        }
    }

    public long createActivity(Activity thisActivity){
        try{
            ContentValues values = new ContentValues();
            values.put(SQLiteHelper.story_id, thisActivity.getStory_id());
            values.put(SQLiteHelper.storage_id, thisActivity.getStorage_id());
            long insertId = database.insert(SQLiteHelper.activity_table, null,
                    values);
            return insertId;
        }
        catch(Exception e){
            return 0;
        }
    }

    public long createStorage(Storage thisStorage){
        try{
            ContentValues values = new ContentValues();
            values.put(SQLiteHelper.file_name, thisStorage.getFile_name());
            values.put(SQLiteHelper.file_path, thisStorage.getFile_path());
            long insertId = database.insert(SQLiteHelper.storage_table, null,
                    values);
            return insertId;
        }
        catch(Exception e){
            return 0;
        }
    }

    public long createMetaData(MetaData thisMetaData){
        try{
            ContentValues values = new ContentValues();
            values.put(SQLiteHelper.media_type, thisMetaData.getMedia_type());
            values.put(SQLiteHelper.timestamp, thisMetaData.getTimestamp());
            values.put(SQLiteHelper.gps_id, thisMetaData.getGps_id());
            values.put(SQLiteHelper.storage_id, thisMetaData.getStorage_id());
            values.put(SQLiteHelper.activity_id, thisMetaData.getActivity_id());
            long insertId = database.insert(SQLiteHelper.meta_data_table, null,
                    values);
            return insertId;
        }
        catch(Exception e){
            return 0;
        }
    }

    public long createGps(Gps thisGps){
        try{
            ContentValues values = new ContentValues();
            values.put(SQLiteHelper.latitude, thisGps.getLatitude());
            values.put(SQLiteHelper.longitude, thisGps.getLongitude());
            long insertId = database.insert(SQLiteHelper.gps_table, null,
                    values);
            return insertId;
        }
        catch(Exception e){
            return 0;
        }
    }

}
