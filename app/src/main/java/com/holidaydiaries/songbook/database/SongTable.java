package com.holidaydiaries.songbook.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by dick on 26/04/2015.
 */
public class SongTable {
    public static final String TABLE_SONG = "Songs";
    public static final String COLUMN_ID = "_ID";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_FILENAME = "song_filename";
    public static final String COLUMN_SONGNUM = "song_number";
    public static final String INDEX_TITLE_IDX = "idx_title";

    private static final String DATABASE_CREATE = "create table "
            + TABLE_SONG
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TITLE + " text not null, "
            + COLUMN_FILENAME + " text not null, "
            + COLUMN_SONGNUM + " text not null "
            + ");";

    private static final String INDEX_CREATE = "create index "
            + INDEX_TITLE_IDX
            + "on" + TABLE_SONG + "(" + COLUMN_FILENAME  + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
        database.execSQL(INDEX_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(SongTable.class.getName(), "Upgrading com.holidaydiaries.songbook.database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_SONG);
        onCreate(database);
    }

}
