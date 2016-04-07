package com.shanekevinsam.pegs;

/**
 * Stores pair of integers
 *
 * Created by me on 4/6/16.
 */
public class Coordinate {
    private int x;
    private int y;

    public Coordinate(int x, int y){
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Coordinate)){
            return false;
        }
        if (obj == this){
            return true;
        }

        Coordinate coord = (Coordinate)obj;
        return (this.x == coord.getX() && this.y == coord.getY());
    }

    @Override
    public int hashCode() {
        return 37*(37 + x) + y;
    }

    @Override
    public String toString() {
        return "(" + this.x + "," + this.y + ")";
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}
