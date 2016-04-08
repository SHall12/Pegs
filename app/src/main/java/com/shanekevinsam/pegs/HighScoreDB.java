package com.shanekevinsam.pegs;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

/**
* Created by kib5469 on 4/7/16.
*/
public class HighScoreDB  extends SQLiteOpenHelper {
   public interface OnDBReadyListener {
        public void onDBReady(SQLiteDatabase theDB);
    }

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "score.db";

    private static HighScoreDB theDb;

    // Only way to instantiate this class is through getInstance!
    private HighScoreDB(Context context) { super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    public static synchronized HighScoreDB getInstance(Context context) {
        if (theDb == null) {
            // Make sure that we do not leak Activity's context
            theDb = new HighScoreDB(context.getApplicationContext());
        }
        return theDb;
    }
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE score (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "playerName TEXT, " +
                    "date DATE )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS score";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void getWritableDatabase(OnDBReadyListener listener) {
        new OpenDbAsyncTask().execute(listener);
    }


    private class OpenDbAsyncTask extends AsyncTask<OnDBReadyListener, Void, SQLiteDatabase> {
        OnDBReadyListener listener;

        @Override
        protected SQLiteDatabase doInBackground(OnDBReadyListener... params) {
            listener = params[0];
            return theDb.getWritableDatabase();
        }

        @Override
        protected void onPostExecute(SQLiteDatabase sqLiteDatabase) {
            listener.onDBReady(sqLiteDatabase);
        }


}











}
