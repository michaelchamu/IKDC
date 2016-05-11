package com.example.icarus.ikdc.database;

import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.util.Log;

/**
 * Created by donovan on 3/1/15.
 */
public class SQLiteHelper extends SQLiteOpenHelper{

    /*
     *==============Thumbnail Table Start============================
     */
    public static final String thmb_table = "THUMBNAIL_TBL";
    public static final String id_column = "ID";
    public static final String thmb_name_column = "THMB_NAME";
    public static final String story_id_column = "STORY_ID";
    /*
     *==============Thumbnail Table End==============================
     */

    /*
     *=================Story Table Start=============================
     */
    public static final String story_table = "STORIES";
    public static final String stry_id_column = "ID";
    public static final String stry_name = "STORY_NAME";
    /*
     *=================Story Table End===============================
     */

    /*
     *=================Activity Table Start==========================
     */
    public static final String activity_table = "ACTIVITIES";
    public static final String activity_id_column = "ID";
    public static final String story_id = "STORY_ID";
    public static final String storage_id_ref = "STORAGE_ID";
    /*
     *=================Activity Table End============================
     */

    /*
     *==============METADATA Table START=============================
     */
    public static final String meta_data_table = "META_DATA";
    public static final String meta_id_column = "ID";
    public static final String media_type = "MEDIA_TYPE";
    public static final String timestamp = "TIMESTAMP";
    public static final String gps_id = "GPS_ID";
    public static final String storage_id = "STORAGE_ID";
    public static final String activity_id = "ACTIVITY_ID";
    /*
     *==============METADATA Table END===============================
     */

    /*
     *==============GPS Table START==================================
     */
    public static final String gps_table = "GPS";
    public static final String gps_id_column = "GPS_ID";
    public static final String latitude = "LATITUDE";
    public static final String longitude = "LONGITUDE";
    /*
     *=================GPS Table END=================================
     */

    /*
     *==============STORAGE Table START==============================
     */
    public static final String storage_table = "STORAGE";
    public static final String storage_id_column = "STORAGE_ID";
    public static final String file_name = "FILE_NAME";
    public static final String file_path = "FILE_PATH";
    /*
     *=================STORAGE Table END==============================
     */

    /*
     *==============COMMON_IMAGES Table START==============================
     */
    public static final String commonImages_Table = "COMMON_IMAGES";
    public static final String commonimages_id_column = "ID";
    public static final String common_images_name = "FILE_NAME";
    public static final String common_images_location = "FILE_LOCATION";
    /*
     *=================COMMON_IMAGES Table END==============================
     */

    /*
     *==============COMMON_VIDEOS Table START==============================
     */
    public static final String commonVideos_Table = "COMMON_VIDEOS";
    public static final String commonvideos_id_column = "ID";
    public static final String common_videos_name = "FILE_NAME";
    public static final String common_videos_location = "FILE_LOCATION";
    /*
     *=================COMMON_VIDEOS Table END==============================
     */

    /*
     *==============COMMON_AUDIOS Table START==============================
     */
    public static final String commonAudios_Table = "COMMON_AUDIOS";
    public static final String commonAudios_id_column = "ID";
    public static final String common_audios_name = "FILE_NAME";
    public static final String common_audios_location = "FILE_LOCATION";
    /*
     *=================COMMON_AUDIOS Table END==============================
     */

    /*
     *==============COMMON_TEXTS Table START==============================
     */
    public static final String commonTexts_Table = "COMMON_TEXTS";
    public static final String commonTexts_id_column = "ID";
    public static final String common_texts_name = "FILE_NAME";
    public static final String common_texts_location = "FILE_LOCATION";
    /*
     *=================COMMON_TEXTS Table END==============================
     */

    public static final String ikdc_dat_name = "IKDC.dat";
    public static final int database_version = 1;

    private static final String STORY_CREATE =
            "create table "
            + story_table + "(" + stry_id_column
            + " integer primary key autoincrement, " + stry_name
            + " text not null);";
    private static final String STORAGE_CREATE =
            "create table "
            + storage_table + "(" + storage_id_column
            + " integer primary key autoincrement, " + file_name
            + " text not null, " + file_path
            + " text not null);";
    private static final String ACTIVITY_CREATE =
            "create table "
            + activity_table + "(" + activity_id_column
            + " integer primary key autoincrement, " + story_id
            + " integer not null, "+ storage_id_ref
            + " integer not null, "
            + " FOREIGN KEY(" + story_id + ") REFERENCES " + story_table + "(" + stry_id_column + "), "
            + " FOREIGN KEY(" + storage_id_ref + ") REFERENCES " + storage_table + "(" + storage_id_column + "));";
    private static final String GPS_CREATE =
            "create table "
            + gps_table + "(" + gps_id_column
            + " integer primary key autoincrement, " + latitude
            + " text not null, " + longitude
            + " text not null);";
    private static final String META_CREATE =
            "create table "
            + meta_data_table + "(" + meta_id_column
            + " integer primary key autoincrement, " + media_type
            + " text, "+ timestamp
            + " time, "+ gps_id
            + " integer, "+ storage_id
            + " integer, "+ activity_id
            + " integer, "
            + " FOREIGN KEY(" + gps_id + ") REFERENCES " + gps_table + "(" + gps_id_column + "), "
            + " FOREIGN KEY(" + storage_id + ") REFERENCES " + storage_table + "(" + storage_id_column + "), "
            + " FOREIGN KEY(" + activity_id + ") REFERENCES " + activity_table + "(" + activity_id_column + "));";
    private static final String THMB_CREATE =
            "create table "
                    + thmb_table + "(" + id_column
            + " integer primary key autoincrement, " + thmb_name_column
            + " text not null, "+ story_id_column
            + " integer not null);";
    private static final String CMMON_IMG_CREATE =
            "create table "
                    + commonImages_Table + "(" + commonimages_id_column
                    + " integer primary key autoincrement, " + common_images_name
                    + " text not null, "+ common_images_location
                    + " text not null);";
    private static final String CMMON_VID_CREATE =
            "create table "
                    + commonVideos_Table + "(" + commonvideos_id_column
                    + " integer primary key autoincrement, " + common_videos_name
                    + " text not null, "+ common_videos_location
                    + " text not null);";
    private static final String CMMON_TXT_CREATE =
            "create table "
                    + commonTexts_Table + "(" + commonTexts_id_column
                    + " integer primary key autoincrement, " + common_texts_name
                    + " text not null, "+ common_texts_location
                    + " text not null);";
    private static final String CMMON_AUD_CREATE =
            "create table "
                    + commonAudios_Table + "(" + commonAudios_id_column
                    + " integer primary key autoincrement, " + common_audios_name
                    + " text not null, "+ common_audios_location
                    + " text not null);";

    public SQLiteHelper(Context context) {
        super(context, thmb_table, null, database_version);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(STORY_CREATE);
        database.execSQL(STORAGE_CREATE);
        database.execSQL(ACTIVITY_CREATE);
        database.execSQL(GPS_CREATE);
        database.execSQL(META_CREATE);
        database.execSQL(THMB_CREATE);
        database.execSQL(CMMON_AUD_CREATE);
        database.execSQL(CMMON_IMG_CREATE);
        database.execSQL(CMMON_TXT_CREATE);
        database.execSQL(CMMON_VID_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + story_table);
        db.execSQL("DROP TABLE IF EXISTS " + storage_table);
        db.execSQL("DROP TABLE IF EXISTS " + activity_table);
        db.execSQL("DROP TABLE IF EXISTS " + gps_table);
        db.execSQL("DROP TABLE IF EXISTS " + meta_data_table);
        db.execSQL("DROP TABLE IF EXISTS " + thmb_table);
        db.execSQL("DROP TABLE IF EXISTS " + commonImages_Table);
        db.execSQL("DROP TABLE IF EXISTS " + commonAudios_Table);
        db.execSQL("DROP TABLE IF EXISTS " + commonTexts_Table);
        db.execSQL("DROP TABLE IF EXISTS " + commonVideos_Table);

        onCreate(db);
    }

}
