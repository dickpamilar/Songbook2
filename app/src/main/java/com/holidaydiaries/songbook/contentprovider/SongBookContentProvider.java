package com.holidaydiaries.songbook.contentprovider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.holidaydiaries.songbook.database.SongBookDBHelper;
import com.holidaydiaries.songbook.database.SongTable;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by dick on 26/04/2015.
 */
public class SongBookContentProvider extends ContentProvider {

    // com.holidaydiaries.songbook.database
    private SongBookDBHelper database;

    // used for the UriMacher
    private static final int SONGS = 10;
    private static final int SONG_ID = 20;

    private static final String AUTHORITY = "com.holidaydiaries.songbook.contentprovider";

    private static final String BASE_PATH = "songs";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + BASE_PATH);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/songbook";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/songbook";

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, SONGS);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", SONG_ID);
    }

    @Override
    public boolean onCreate() {
        database = new SongBookDBHelper(getContext());
        try {
            database.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        // Uisng SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // check if the caller has requested a column which does not exists
        checkColumns(projection);

        // Set the table
        queryBuilder.setTables(SongTable.TABLE_SONG);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case SONGS:
                break;
            case SONG_ID:
                // adding the ID to the original query
                queryBuilder.appendWhere(SongTable.COLUMN_ID + "="
                        + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted = 0;
        long id = 0;
        switch (uriType) {
            case SONGS:
                id = sqlDB.insert(SongTable.TABLE_SONG, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case SONGS:
                rowsDeleted = sqlDB.delete(SongTable.TABLE_SONG, selection,
                        selectionArgs);
                break;
            case SONG_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(SongTable.TABLE_SONG,
                            SongTable.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(SongTable.TABLE_SONG,
                            SongTable.COLUMN_ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case SONGS:
                rowsUpdated = sqlDB.update(SongTable.TABLE_SONG,
                        values,
                        selection,
                        selectionArgs);
                break;
            case SONG_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(SongTable.TABLE_SONG,
                            values,
                            SongTable.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(SongTable.TABLE_SONG,
                            values,
                            SongTable.COLUMN_ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    private void checkColumns(String[] projection) {
        String[] available = { SongTable.COLUMN_ID,
                SongTable.COLUMN_TITLE, SongTable.COLUMN_FILENAME,
                SongTable.COLUMN_SONGNUM };
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
            // check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }
}
