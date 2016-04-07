package com.shanekevinsam.pegs;

import android.util.Log;

// TODO GAME HAS 5 SIDES, NOT 4!!!

/**
 * Stores game state for peg game in 2d boolean array
 *  True: Peg
 *  False: Empty
 *
 * Created by Shane on 4/6/16.
 */
public class Board {
    /*
                 0,3
              0,2   1,2
           0,1   1,1   2,1
        0,0   1,0   2,0   3,0
     */
    private boolean[][] board;
    private int pegsLeft;
    private static String TAG = "Board";

    /**Initialize board with given coord's peg removed
     *
     * @param coord peg to be removed
     * @throws IllegalArgumentException
     */
    public Board(Coordinate coord){
        this.board = new boolean[4][4];
        if (isLegitCoord(coord)) {
            for (int i = 0; i <= 3; ++i) {
                for (int j = 0; j <= 3 - i; ++j) {
                    this.board[i][j] = true;
                }
            }
            removePeg(coord);
            pegsLeft = 9;
        } else {
            throw new IllegalArgumentException("Coordinate out of bounds.");
        }
    }

    /**Initialize board given a state
     *
     * @param board 3x3 boolean array
     * @throws IllegalArgumentException
     */
    public Board(boolean[][] board){
        if (board.length >= 4 && board[0].length >= 4 && board[1].length >=3
                && board[2].length >=2 && board[3].length >= 1) {
            this.board = board;
        } else {
            throw new IllegalArgumentException("Board not large enough.");
        }
    }

    /**Determines if coord is in bound of game board
     *
     * @param coord
     * @return
     */
    public boolean isLegitCoord(Coordinate coord){
        int x = coord.getX();
        int y = coord.getY();
        return (x >= 0 && y >= 0 && x <= 3 && y <= 3 && (x+y) <= 3);
    }

    /**Adds peg to board if in bound and no peg is present
     *
     * @param coord
     * @return true if successful
     * @throws IllegalArgumentException
     */
    public boolean addPeg(Coordinate coord){
        if (isLegitCoord(coord)){
            Log.d(TAG, "Add peg: " + coord.toString());
            board[coord.getX()][coord.getY()] = true;
            ++pegsLeft;
            return true;
        } else {
            throw new IllegalArgumentException("Coordinate out of bounds.");
        }
    }

    /**Removes peg from board if in bound
     *
     * @param coord
     * @return true if successful
     * @throws IllegalArgumentException
     */
    public boolean removePeg(Coordinate coord){
        if (isLegitCoord(coord)){
            Log.d(TAG, "Remove peg: " + coord.toString());
            board[coord.getX()][coord.getY()] = false;
            --pegsLeft;
            return true;
        } else{
            throw new IllegalArgumentException("Coordinate out of bounds.");
        }
    }

    /**Check if peg is at coordinate
     *
     * @param coord
     * @return true if present, false otherwise
     * @throws IllegalArgumentException
     */
    public boolean checkPeg(Coordinate coord){
        if (isLegitCoord(coord)) {
            //Log.d(TAG, "Check peg: " + coord.toString());
            return board[coord.getX()][coord.getY()];
        } else {
            throw new IllegalArgumentException("Coordinate out of bounds.");
        }
    }

    public int getNumPegs(){
        return pegsLeft;
    }

    public boolean[][] getBoard() {
        return board;
    }
}
