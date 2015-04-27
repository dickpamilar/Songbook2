package com.holidaydiaries.songbook.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Created by dick on 26/04/2015.
 */
public class SongBookDBHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "songs.db";
    private static final int DATABASE_VERSION = 1;
    private static String DB_PATH = "/data/data/com.holidaydiaries.songbook/databases/";

    private static String DB_NAME = "SongDB";
    private final Context myContext;


    public SongBookDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext = context;
    }

    // Method is called during creation of the com.holidaydiaries.songbook.database
    @Override
    public void onCreate(SQLiteDatabase database) {
        //boolean dbExist = checkDataBase();


//        SongTable.onCreate(database);
    }

    // Method is called during an upgrade of the com.holidaydiaries.songbook.database,
    // e.g. if you increase the com.holidaydiaries.songbook.database version
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                          int newVersion) {
        SongTable.onUpgrade(database, oldVersion, newVersion);
    }
    /**
     * Creates a empty com.holidaydiaries.songbook.database on the system and rewrites it with your own com.holidaydiaries.songbook.database.
     * */
    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if(dbExist){
            //do nothing - com.holidaydiaries.songbook.database already exist
        }else{

            //By calling this method and empty com.holidaydiaries.songbook.database will be created into the default system path
            //of your application so we are gonna be able to overwrite that com.holidaydiaries.songbook.database with our com.holidaydiaries.songbook.database.
            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException e) {

                throw new Error("Error copying com.holidaydiaries.songbook.database");

            }
        }

    }

    /**
     * Check if the com.holidaydiaries.songbook.database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;

        File dbFile = myContext.getDatabasePath(DB_NAME);
        return dbFile.exists();
    }

    /**
     * Copies your com.holidaydiaries.songbook.database from your local assets-folder to the just created empty com.holidaydiaries.songbook.database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{

        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DATABASE_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

}
