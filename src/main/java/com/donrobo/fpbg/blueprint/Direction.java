package com.donrobo.fpbg.blueprint;

public class Direction {

    public static final int UP = 4;
    public static final int DOWN = 0;
    public static final int LEFT = 2;
    public static final int RIGHT = 6;

    public static int reverseDirection(int direction) {
        switch (direction) {
            case UP:
                return DOWN;
            case DOWN:
                return UP;
            case LEFT:
                return RIGHT;
            case RIGHT:
                return LEFT;
        }
        throw new RuntimeException("Unknown direction: " + direction);
    }
}
