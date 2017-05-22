package com.donrobo.fpbg.blueprint;

import com.donrobo.fpbg.data.Int2;

public enum Direction {

    UP(4),
    DOWN(0),
    LEFT(2),
    RIGHT(6);

    private final int directionValue;

    Direction(int directionValue) {
        this.directionValue = directionValue;
    }

    public int getDirectionValue() {
        return directionValue;
    }

    public Direction reverseDirection() {
        switch (this) {
            case UP:
                return DOWN;
            case DOWN:
                return UP;
            case LEFT:
                return RIGHT;
            case RIGHT:
                return LEFT;
            default:
                throw new RuntimeException("New direction added?");
        }
    }

    public Int2 move(Int2 start, int distance) {
        int actualX = start.getX();
        int actualY = start.getY();

        switch (this) {
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
