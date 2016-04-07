package com.shanekevinsam.pegs;

import android.util.Log;

import java.util.Random;

/**
 * Controls logic for peg game
 *
 * Created by me on 4/6/16.
 */
public class Game {
    private static final String TAG = "Game";
    private Board board = null;

    /*
        Check if move is legit (Start, end)
            start = full, (start.x+end.x)/2 (start.y+end.y)/2) = full, end = empty
        perform move -> remove start, mid, and add end
        Check for no more legal moves after each legit move
        Keep track of number of pegs left
        Reset game
     */

    /**
     * Instantiates game board with random peg removed
     */
    public Game(){
        Random rand = new Random();
        int x = rand.nextInt(4);
        int y = rand.nextInt(4-x);
        if (x == 1 && y == 1){
            y = 0;
        }
        board = new Board(new Coordinate(x,y));
    }

    /**
     * Instantiates game board with select peg removed
     *
     * @param coord
     */
    public Game(Coordinate coord){
        board = new Board(coord);
    }

    public Game(boolean[][] boardArray){
        try{
            board = new Board(boardArray);
        } catch (IllegalArgumentException e){
            Log.d(TAG, "Bad array passed to Game()", e);
            Random rand = new Random();
            int x = rand.nextInt(4);
            int y = rand.nextInt(4-x);
            if (x == 1 && y == 1){
                y = 0;
            }
            board = new Board(new Coordinate(x,y));
        }
    }

    /**
     * Returns boolean array with coordinates representing pegs
     *
     * @return
     */
    public boolean[][] getBoard(){
        return board.getBoard();
    }

    /**
     * Checks if move is correct and performs changes to board
     *
     * @param start
     * @param end
     * @return true if move successful, false otherwise
     */
    public boolean move(Coordinate start, Coordinate end){
        if(validateMove(start, end)){
            Coordinate midCoord = calcPegBetween(start, end);
            board.removePeg(start);
            board.removePeg(midCoord);
            board.addPeg(end);
            return true;
        } else{
            return false;
        }
    }

    /**
     * Checks if a move is valid.
     * Valid if peg at start, no peg at end, correct distance and peg between
     *
     * @param start Position at start of move
     * @param end Position at end of move
     * @return true if valid, false otherwise
     */
    private boolean validateMove(Coordinate start, Coordinate end) {
        Log.d(TAG, "Validating move from " + start + " to " + end);
        try {
            return (board.checkPeg(start)
                    && !board.checkPeg(end)
                    && checkCorrectDistance(start, end)
                    && checkPegBetween(start, end));
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Start or End out of bounds", e);
            return false;
        }
    }

    /**
     * Checks if start coordinates and end coordinate are a peg apart.
     * If they are on the same row or column in the board, they should be 2 away
     * If they aren't they have to be diagonal, where somehow you get 4 from the math.
     *
     * @param start
     * @param end
     * @return
     */
    private boolean checkCorrectDistance( Coordinate start, Coordinate end){
        if (start.getX() == end.getX()  && start.getY() == end.getY()){
            return false;
        } else if(start.getX() == end.getX() || start.getY() == end.getY()){
            return Math.abs(start.getX() - end.getX()) + Math.abs(start.getY() - end.getY()) == 2;
        } else {
            return Math.abs(start.getX() - end.getX()) + Math.abs(start.getY() - end.getY()) == 4;
        }
    }

    /**
     * Checks if peg is between 2 coords.
     * Assumes correctDistance is true!!!
     *
     * @param start
     * @param end
     * @return True if peg present, false otherwise
     */
    private boolean checkPegBetween(Coordinate start, Coordinate end){
        return board.checkPeg(calcPegBetween(start, end));
    }

    /**
     * Calculates the peg between 2 coordinates
     * Assumes correctDistance is true!!!
     *
     * @param start
     * @param end
     * @return {x, y}
     */
    private Coordinate calcPegBetween(Coordinate start, Coordinate end) {
        int x = Math.abs(start.getX() + end.getX()) / 2;
        int y = Math.abs(start.getY() + end.getY()) / 2;
        Coordinate coord = new Coordinate(x, y);
        Log.d(TAG, "MID(" + start + " " + end + ") = " + coord);
        return coord;
    }

    /**
     * Checks if a move on the board is possible.
     *
     * @return true if there are remaining moves, false otherwise
     */
    public boolean checkForRemainingMoves(){
        for (int y = 0; y <= 3; ++y) {
            for (int x = 0; x <= 3 - y; ++x) {
                if (checkValidMovesFromCoord(new Coordinate(x, y))){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * If the board is broken into a bottom left, bottom right, and top,
     *  only 2 directions have to be checked at a time
     * If bottom left:  Check right and up to right
     * If bottom right: Check left and up to left
     * If top:          check bottom left and bottom right
     *
     * Note: move cannot start from center
     *
     * @param coord
     * @return if there is a valid move from coordinate
     */
    private boolean checkValidMovesFromCoord(Coordinate coord){
        Log.d(TAG, "Checking valid moves from " + coord);
        int x = coord.getX();
        int y = coord.getY();
        if (!(x == 1 && y == 1)) { //if not center
            if (x <= 1 && y <=1){
                if(validateMove(coord, new Coordinate(x+2, y)) || validateMove(coord, new Coordinate(x, y+2))){
                    return true;
                }
            } else if ( x >= 2 && y <= 1){
                if(validateMove(coord, new Coordinate(x-2, y)) || validateMove(coord, new Coordinate(x-2, y+2))){
                    return true;
                }
            } else {
                if(validateMove(coord, new Coordinate(x, y-2)) || validateMove(coord, new Coordinate(x+2, y-2))){
                    return true;
                }
            }
        }
        return false;
    }

    public int getNumPegsLeft(){
        return board.getNumPegs();
    }

    public boolean isPegAt(Coordinate coord){
        return board.checkPeg(coord);
    }
}
