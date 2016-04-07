package com.shanekevinsam.pegs;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ScoreActivity extends AppCompatActivity {

    SQLiteDatabase theDB;
    Long rowid;
    long currentRow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_score);
       /* if (getIntent().hasExtra("rowid")) {
            rowid = getIntent().getLongExtra("rowid", 0);
            ContentResolver cr = getContentResolver();

            Cursor c = cr.query(DbContentProvider.CONTENT_URI.buildUpon().appendPath(Long.toString(rowid)).build(),
                    new String[] {"title","setup","punchline"},null, null, null);

            if (!c.moveToFirst()) {
                this.setTitle("Add new joke");
                Toast.makeText(this, "Error retrieving joke.  Adding new joke, instead.", Toast.LENGTH_LONG).show();
                rowid = null;
            }
            else {
                this.setTitle("Edit joke");
                ((EditText) findViewById(R.id.playerName)).setText(c.getString(0));
                ((EditText) findViewById(R.id.gamename)).setText(c.getString(1));
                ((EditText) findViewById(R.id.val)).setText(c.getString(2));
            }
            c.close();
        }
*/

    }


    @Override
    protected void onResume() {
        super.onResume();
       /* HighScoreDB.getInstance(this).getWritableDatabase(new HighScoreDB.OnDBReadyListener() {
            @Override
            public void onDBReady(SQLiteDatabase DB) {
                // Will this.theDB work?
                theDB = DB;
            }
        });

        ContentResolver cr = this.getContentResolver();
        Cursor c = cr.query(DbContentProvider.CONTENT_URI.buildUpon().build(),
                new String[]{"_id", "playerName", "g", "val"}, null, null, null);
        String pname, gameString,valString;


        StringBuffer sb = new StringBuffer();
        String[] columns = {"_id", "playerName", "g","val"};



        while (c.moveToNext()) {
            sb.append("id: " + c.getLong(c.getColumnIndexOrThrow("_id")));
            sb.append(", player: " + c.getString(c.getColumnIndexOrThrow("playerName")));
            sb.append(", game: " + c.getString(c.getColumnIndexOrThrow("g")));
            sb.append(", val: " + c.getInt(c.getColumnIndexOrThrow("val")) + "\n");
        }
        c.close();*/
    }

}
