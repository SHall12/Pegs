package com.shanekevinsam.pegs;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.SQLException;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GameActivity extends AppCompatActivity {

    private Game game;
    private Map<Coordinate, Integer> coordToButtonID;
    private Map<Integer, Coordinate> buttonIDToCoord;
    private Coordinate startCoord;
    private Coordinate endCoord;
    private static MediaPlayer mediaPlayer = null;
    Long rowid;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        if (savedInstanceState != null) {
            game = new Game((boolean[][])savedInstanceState.getSerializable("board"));
        } else {
            initializeGame();
        }
        initializeMaps();
        updateBoard();
        initializeMusic();
        // TODO Set listener to play sound on successful moves
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            game = new Game((boolean[][])savedInstanceState.getSerializable("board"));
        } else {
            game = new Game();
        }
        updateBoard();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeMusic();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("board", game.getBoard());
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void initializeGame() {
        // TODO Check settings for default peg to remove
        game = new Game();
    }

    private void initializeMusic() {
        // TODO use singleton pattern so only 1 mediaPlayer used at a time
        // TODO check shared preferences if music selected
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.bensound_theelevatorbossanova);
            mediaPlayer.start();
        }
    }

    /**
     * Resets game to initial conditions
     */
    private void restartGame() {
        initializeGame();
        updateBoard();
    }

    /**
     * Open dialog with game info, prompt user to play again
     */
    private void endGame() {
        if (game.getNumPegsLeft() == 1) {
            this.updateHighSchores();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton(R.string.game_restart, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                restartGame();
            }
        });
        builder.setNegativeButton(R.string.game_quit, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO go back to the main screen
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

        Toast.makeText(getApplicationContext(), game.getNumPegsLeft() + " pegs left", Toast.LENGTH_LONG).show();

    }

    public void boardClicked(View v) {

        // TODO handle null pointer exceptions
        if (startCoord == null) {
            Coordinate coord = buttonIDToCoord.get(v.getId());
            if (game.isPegAt(coord)) {
                startCoord = coord;
                (findViewById(v.getId())).setEnabled(false);
            }
        } else {
            endCoord = buttonIDToCoord.get(v.getId());
            if (game.move(startCoord, endCoord)) {
                updateBoard();
                // TODO Make pop sound
                if (!game.checkForRemainingMoves()) {
                    endGame();
                }
            } else {
                illegalMove();
            }
            findViewById(coordToButtonID.get(startCoord)).setEnabled(true);
            startCoord = null;
            endCoord = null;
        }
    }

    /**
     * Updates views to represent board state
     */
    private void updateBoard() {
        // TODO handle null pointer exceptions
        // For each peg in board, highlight boardView
        // Update number of pegs left
        for (int y = 0; y <= 4; ++y) {
            for (int x = 0; x <= 4 - y; ++x) {
                Coordinate coord = new Coordinate(x, y);
                if (game.isPegAt(coord)) {
                    ((Button) findViewById(coordToButtonID.get(coord))).setText("P");
                } else {
                    ((Button) findViewById(coordToButtonID.get(coord))).setText("");
                }
            }
        }

        TextView pegsLeft = ((TextView) findViewById(R.id.txt_PegsLeft));
        pegsLeft.setText(Integer.toString(game.getNumPegsLeft()));
    }

    /**
     * Toast illegal moves
     */
    private void illegalMove() {
        Toast.makeText(getApplicationContext(), R.string.tst_illegal_move, Toast.LENGTH_SHORT).show();
    }

    /**
     * Initializes map between objects (views in this case) to a coordinate on the board
     */
    private void initializeMaps() {
        coordToButtonID = new HashMap<>();
        buttonIDToCoord = new HashMap<>();

        for (int y = 0; y <= 4; ++y) {
            for (int x = 0; x <= 4 - y; ++x) {
                int btnId = getResources().getIdentifier(
                        "btn_" + Integer.toString(x) + Integer.toString(y), "id", "com.shanekevinsam.pegs");
                coordToButtonID.put(new Coordinate(x, y), btnId);
                buttonIDToCoord.put(btnId, new Coordinate(x, y));
            }
        }
    }

    private void updateHighSchores() {
        //TODO figure out why this is throwing an error
        ContentResolver cr = getContentResolver();
        Date date = new Date();
        ContentValues values = new ContentValues();
        values.put("playerName", "kevin");
        values.put("date", date.toString());
        try {
            cr.insert(DbContentProvider.CONTENT_URI, values);
            finish();
        } catch (SQLException e) {
            Toast.makeText(this, "Error updating database.", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Game Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.shanekevinsam.pegs/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Game Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.shanekevinsam.pegs/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
