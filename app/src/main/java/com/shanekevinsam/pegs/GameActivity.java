package com.shanekevinsam.pegs;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GameActivity extends AppCompatActivity {

    private String TAG = "GameActivity";
    private Game game;
    private Map<Coordinate, Integer> coordToButtonID;
    private Map<Integer, Coordinate> buttonIDToCoord;
    private Coordinate startCoord;
    private Coordinate endCoord;
    private MediaPlayer mediaPlayer = null;
    private SoundPool soundPool = null;
    private int popId;
    private Drawable drawEmptyPeg;
    private Drawable drawSelectedPeg;
    private Drawable drawPeg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        if (savedInstanceState != null) {
            game = new Game((boolean[][])savedInstanceState.getSerializable("board"));
            if(!game.checkForRemainingMoves()){
                endGame();
            }
        } else {
            initializeGame();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawEmptyPeg = getResources().getDrawable(R.drawable.emptypeg, getApplicationContext().getTheme());
            drawSelectedPeg = getResources().getDrawable(R.drawable.selectedpeg, getApplicationContext().getTheme());
            drawPeg = getResources().getDrawable(R.drawable.otherpeg, getApplicationContext().getTheme());
        } else {
            drawEmptyPeg = getResources().getDrawable(R.drawable.emptypeg);
            drawSelectedPeg = getResources().getDrawable(R.drawable.selectedpeg);
            drawPeg = getResources().getDrawable(R.drawable.otherpeg);
        }
        initializeMaps();
        updateBoard();
        initializeMusic();
        initializeSound();
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
        initializeSound();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (soundPool != null){
            soundPool.release();
            soundPool = null;
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
        if (soundPool != null){
            soundPool.release();
            soundPool = null;
        }
    }

    private void initializeGame() {
        // TODO Check settings for default peg to remove
        game = null;
        game = new Game();
    }

    private void initializeMusic() {
        if (mediaPlayer == null) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(GameActivity.this);
            if(sharedPref.getBoolean(getString(R.string.pref_music_key), true )){
                mediaPlayer = MediaPlayer.create(this, R.raw.bensound_theelevatorbossanova);
                mediaPlayer.start();
            }
        }
    }

    private void initializeSound(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(GameActivity.this);
        if(sharedPref.getBoolean(getString(R.string.pref_sound_effects_key), true )) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                createNewSoundPool();
            } else {
                createOldSoundPool();
            }
            popId = soundPool.load(this, R.raw.pop, 1);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void createNewSoundPool(){
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .build();
    }

    @SuppressWarnings("deprecation")
    protected void createOldSoundPool(){
        soundPool = new SoundPool(5,AudioManager.STREAM_MUSIC,0);
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
            showCongratsDialog();
        } else {
            showTryAgainDialog();
        }
        initializeMusic();
    }

    private void showTryAgainDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Finished with " + game.getNumPegsLeft() + " pegs left");
        builder.setPositiveButton(R.string.game_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                restartGame();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showCongratsDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.game_dialog_congrats);
        builder.setMessage(R.string.game_congrats_message);

        final EditText input = new EditText(this);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(GameActivity.this);
        input.setText(sharedPref.getString("name",""));
        builder.setView(input);

        builder.setPositiveButton(R.string.game_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                updateHighScores(input.getText().toString().trim());
                restartGame();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void boardClicked(View v) {
        // TODO handle null pointer exceptions
        if (startCoord == null) {
            Coordinate coord = buttonIDToCoord.get(v.getId());
            if (game.isPegAt(coord)) {
                startCoord = coord;
                (findViewById(v.getId())).setEnabled(false);
                ((ImageView) findViewById(coordToButtonID.get(coord))).setImageDrawable(drawSelectedPeg);
            }
        } else {
            endCoord = buttonIDToCoord.get(v.getId());
            if (game.move(startCoord, endCoord)) {
                updateBoard();
                if (soundPool != null) {
                    soundPool.play(popId, 1, 1, 0, 0, 1);
                }
                if (!game.checkForRemainingMoves()) {
                    endGame();
                }
            } else {
                illegalMove();
                ((ImageView) findViewById(coordToButtonID.get(startCoord))).setImageDrawable(drawPeg);
            }
            findViewById(coordToButtonID.get(startCoord)).setEnabled(true);

            startCoord = null;
            endCoord = null;
        }
    }

    public void notBoardClicked(View v) {
        if (startCoord != null){
            findViewById(coordToButtonID.get(startCoord)).setEnabled(true);
            ((ImageView) findViewById(coordToButtonID.get(startCoord))).setImageDrawable(drawPeg);
            startCoord = null;
        }
    }

    /**
     * Updates views to represent board state
     */
    private void updateBoard() {
        // For each peg in board, highlight boardView
        // Update number of pegs left
        for (int y = 0; y <= 4; ++y) {
            for (int x = 0; x <= 4 - y; ++x) {
                Coordinate coord = new Coordinate(x, y);
                if (game.isPegAt(coord)) {
                    ((ImageView) findViewById(coordToButtonID.get(coord))).setImageDrawable(drawPeg);
                } else {
                    ((ImageView) findViewById(coordToButtonID.get(coord))).setImageDrawable(drawEmptyPeg);
                }
            }
        }
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

    private void updateHighScores(String name) {
        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        Date currentDate = new Date();
        String formattedDate = new SimpleDateFormat("MMM d ''yy").format(currentDate);
        values.put("playerName", name);
        values.put("date", formattedDate);
        try {
                cr.insert(DbContentProvider.CONTENT_URI, values);
                Log.d(TAG, "Added " + name + " and " + formattedDate + " to database");
        } catch (SQLException e) {
            Log.d(TAG, "Error updating database", e);
        }
    }
}
