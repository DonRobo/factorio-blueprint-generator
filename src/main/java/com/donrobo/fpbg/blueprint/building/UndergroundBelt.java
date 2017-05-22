package com.donrobo.fpbg.blueprint.building;

import com.donrobo.fpbg.blueprint.Direction;
import net.sf.json.JSONObject;

public class UndergroundBelt extends AbstractBuilding {

    private final Direction direction;
    private final boolean input;

    public UndergroundBelt(int x, int y, Direction direction, boolean input) {
        super(x, y);
        this.direction = direction;
        this.input = input;
    }

    @Override
    public int getWidth() {
        return 1;
    }

    @Override
    public int getHeight() {
        return 1;
    }

    @Override
    public Building move(int x, int y) {
        return new UndergroundBelt(getX() + x, getY() + y, direction, input);
    }

    @Override
    public String getName() {
        return "underground-belt";
    }

    @Override
    public char getVisualizationCharacter() {
        return isInput() ? 'u' : 'U';
    }

    @Override
    protected double getBlueprintXOffset() {
        return 0;
    }

    @Override
    protected double getBlueprintYOffset() {
        return 0;
    }

    @Override
    protected void addCustomPropertiesToJson(JSONObject json) {
        json.put("direction", direction.reverseDirection().getDirectionValue());
        json.put("type", input ? "input" : "output");
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean isInput() {
        return input;
    }

    public boolean isOutput() {
        return !input;
    }
}
