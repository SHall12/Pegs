package com.shanekevinsam.pegs;

import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class GameActivity extends AppCompatActivity {

    private Game game;

    private Map<Coordinate, Integer> coordToButtonID;
    private Map<Integer, Coordinate> buttonIDToCoord;

    Coordinate startCoord;
    Coordinate endCoord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        if(savedInstanceState != null){
            game = new Game((boolean[][])savedInstanceState.getSerializable("board"));
        } else {
            initializeGame();
        }
        initializeMaps();
        updateBoard();
        // TODO Set listeners to set game info
        // TODO Set listeners to update views

        // TODO Check if music selected, then play it
        // TODO Set listener to play sound on successful moves
    }

    /* // TODO
        Get game data
        store board into bundle and read it back (Serializable)
        Open dialog if leaving
        Store pegs left, name and date in DB
        Play music
        make pop sound on each move
     */

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null){
            game = new Game((boolean[][])savedInstanceState.getSerializable("board"));
        } else {
            game = new Game();
        }
        updateBoard();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("board", game.getBoard());
    }

    private void initializeGame(){
        // TODO Check settings for default peg to remove
        game = new Game();
    }

    /**
     * Resets game to initial conditions
     */
    private void restartGame(){
        initializeGame();
        updateBoard();
    }

    /**
     * Open dialog with game info, prompt user to play again
     */
    private void endGame(){
        // TODO Store name + game info + date into database
        // TODO Prompt to restart game
        Toast.makeText(getApplicationContext(), "No moves left", Toast.LENGTH_LONG).show();
    }

    // TODO Implement boardClicked
    public void boardClicked(View v){
        if (startCoord == null){
            Coordinate coord = buttonIDToCoord.get(v.getId());
            if (game.isPegAt(coord)) {
                startCoord = coord;
                (findViewById(v.getId())).setEnabled(false);
            }
        } else {
            endCoord = buttonIDToCoord.get(v.getId());
            if(game.move(startCoord, endCoord)){
                updateBoard();
                // TODO Make pop sound
                if(!game.checkForRemainingMoves()){
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
    private void updateBoard(){
        // TODO Fill view with game info
        // For each peg in board, highlight boardView
        // Update number of pegs left
        for (int y = 0; y <= 3; ++y){
            for (int x = 0; x <= 3 - y; ++x){
                Coordinate coord = new Coordinate(x, y);
                if(game.isPegAt(coord)){
                    ((Button)findViewById(coordToButtonID.get(coord))).setText("1");
                } else {
                    ((Button)findViewById(coordToButtonID.get(coord))).setText("0");
                }
            }
        }

        //TODO update numPegsLeft when updating board (fix null pointer)
        TextView pegsLeft = ((TextView)findViewById(R.id.txt_PegsLeft));
        pegsLeft.setText(Integer.toString(game.getNumPegsLeft()));
    }

    /**
     * Toast illegal moves
     */
    private void illegalMove(){
        Toast.makeText(getApplicationContext(), R.string.tst_illegal_move, Toast.LENGTH_SHORT).show();
    }

    /**
     * Initializes map between objects (views in this case) to a coordinate on the board
     */
    private void initializeMaps(){
        coordToButtonID = new HashMap<>();
        coordToButtonID.put(new Coordinate(0,0), R.id.btn_00);
        coordToButtonID.put(new Coordinate(1,0), R.id.btn_10);
        coordToButtonID.put(new Coordinate(2,0), R.id.btn_20);
        coordToButtonID.put(new Coordinate(3,0), R.id.btn_30);
        coordToButtonID.put(new Coordinate(0,1), R.id.btn_01);
        coordToButtonID.put(new Coordinate(1,1), R.id.btn_11);
        coordToButtonID.put(new Coordinate(2,1), R.id.btn_21);
        coordToButtonID.put(new Coordinate(0,2), R.id.btn_02);
        coordToButtonID.put(new Coordinate(1, 2), R.id.btn_12);
        coordToButtonID.put(new Coordinate(0, 3), R.id.btn_03);

        buttonIDToCoord = new HashMap<> ();
        buttonIDToCoord.put(R.id.btn_00, new Coordinate(0,0));
        buttonIDToCoord.put(R.id.btn_10, new Coordinate(1,0));
        buttonIDToCoord.put(R.id.btn_20, new Coordinate(2,0));
        buttonIDToCoord.put(R.id.btn_30, new Coordinate(3,0));
        buttonIDToCoord.put(R.id.btn_01, new Coordinate(0,1));
        buttonIDToCoord.put(R.id.btn_11, new Coordinate(1,1));
        buttonIDToCoord.put(R.id.btn_21, new Coordinate(2,1));
        buttonIDToCoord.put(R.id.btn_02, new Coordinate(0,2));
        buttonIDToCoord.put(R.id.btn_12, new Coordinate(1,2));
        buttonIDToCoord.put(R.id.btn_03, new Coordinate(0,3));
    }

}
