package com.donrobo.fpbg.blueprint;

import com.donrobo.fpbg.data.Int2;

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

    public static Int2 move(Int2 start, int distance, int direction) {
        int actualX = start.getX();
        int actualY = start.getY();

        switch (direction) {
            case UP:
                actualY -= distance;
                break;
            case DOWN:
                actualY += distance;
                break;
            case LEFT:
                actualX -= distance;
                break;
            case RIGHT:
                actualX += distance;
                break;
        }

        return new Int2(actualX, actualY);
    }
}
