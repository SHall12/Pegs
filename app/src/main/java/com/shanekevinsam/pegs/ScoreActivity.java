package com.shanekevinsam.pegs;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ScoreActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private SimpleCursorAdapter mAdapter;
    long currentRow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_score);
        mAdapter = new SimpleCursorAdapter(this, R.layout.list_item, null,
                new String[]{"playerName","date"},new int[]{R.id.score_name_info,R.id.score_date_info}, 0);

        ListView listView = (ListView) findViewById(R.id.Score_list);
        listView.setAdapter(mAdapter);
        getLoaderManager().initLoader(1, null, this);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Create a new CursorLoader with the following query parameters.
        String where = null;

        return new CursorLoader(this, DbContentProvider.CONTENT_URI,
                new String[]{"_id", "playerName","date"}, where, null, null);
    }

    @Override
    protected void onResume() {
        super.onResume();}


}
