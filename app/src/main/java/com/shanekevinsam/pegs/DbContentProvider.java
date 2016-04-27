package com.shanekevinsam.pegs;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * Created by kib5469 on 4/7/16.
 */
public class DbContentProvider extends ContentProvider {

    private HighScoreDB theDB;
    private static final String AUTHORITY = "com.shanekevinsam.pegs";
    private static final String BASE_PATH = "score";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int GAMES = 1;
    private static final int GAMES_ID = 2;
    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, GAMES);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", GAMES_ID);
    }


    @Override
    public boolean onCreate() {
        theDB = HighScoreDB.getInstance(getContext());
        return true;
    }
    private String appendIdToSelection(String selection, String sId) {
        int id = Integer.valueOf(sId);

        if (selection == null || selection.trim().equals(""))
            return "_ID = " + id;
        else
            return selection + " AND _ID = " + id;
    }

    public void onReset(Context context)
    {
        theDB.onUpgrade(context);
    }


    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = theDB.getReadableDatabase();
        Cursor cursor;

        switch (uriMatcher.match(uri)) {
            case GAMES:
                cursor = db.query("score", projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case GAMES_ID:
                cursor = db.query("score", projection,
                        appendIdToSelection(selection, uri.getLastPathSegment()),
                        selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        long id;
        SQLiteDatabase db = theDB.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case GAMES:
                id = db.insert("score", null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int count;
        SQLiteDatabase db = theDB.getWritableDatabase();

        switch (uriMatcher.match(uri)){
            case GAMES:
                count = db.delete("score", selection, selectionArgs);
                break;
            case GAMES_ID:
                count = db.delete("score",
                        appendIdToSelection(selection, uri.getLastPathSegment()),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int count;
        SQLiteDatabase db = theDB.getWritableDatabase();

        switch (uriMatcher.match(uri)){
            case GAMES:
                count = db.update("score", values, selection, selectionArgs);
                break;
            case GAMES_ID:
                count = db.update("score", values,
                        appendIdToSelection(selection, uri.getLastPathSegment()),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

}
