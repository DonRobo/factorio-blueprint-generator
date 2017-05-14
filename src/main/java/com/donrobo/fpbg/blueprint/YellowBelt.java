package com.donrobo.fpbg.blueprint;

import net.sf.json.JSONObject;

public class YellowBelt extends AbstractBuilding {

    private final int direction;

    public YellowBelt(int x, int y, int direction) {
        super(x, y);
        this.direction = direction;
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
    public YellowBelt move(int x, int y) {
        return new YellowBelt(getX() + x, getY() + y, direction);
    }

    @Override
    public String getName() {
        return "transport-belt";
    }

    @Override
    protected void addCustomPropertiesToJson(JSONObject json) {
        json.put("direction", Direction.reverseDirection(direction));
    }

    @Override
    protected int getBlueprintXOffset() {
        return 0;
    }

    @Override
    protected int getBlueprintYOffset() {
        return 0;
    }
}
