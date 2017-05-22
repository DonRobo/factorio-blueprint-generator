package com.donrobo.fpbg.blueprint.building;

import com.donrobo.fpbg.blueprint.Direction;
import net.sf.json.JSONObject;

import static com.donrobo.fpbg.blueprint.Direction.*;

public class Splitter extends AbstractBuilding {

    private final Direction direction;

    public Splitter(int x, int y, Direction direction) {
        super(x, y);
        this.direction = direction;
    }

    @Override
    public int getWidth() {
        return direction == UP || direction == DOWN ? 2 : 1;
    }

    @Override
    public int getHeight() {
        return direction == UP || direction == DOWN ? 1 : 2;
    }

    @Override
    public Building move(int x, int y) {
        return new Splitter(getX() + x, getY() + y, direction);
    }

    @Override
    public String getName() {
        return "splitter";
    }

    @Override
    public char getVisualizationCharacter() {
        return 'S';
    }

    @Override
    protected double getBlueprintXOffset() {
        return direction == UP || direction == DOWN ? 0.5 : 0;
    }

    @Override
    protected double getBlueprintYOffset() {
        return direction == LEFT || direction == RIGHT ? 0.5 : 0;
    }

    @Override
    protected void addCustomPropertiesToJson(JSONObject json) {
        json.put("direction", direction.reverseDirection().getDirectionValue());
    }
}
