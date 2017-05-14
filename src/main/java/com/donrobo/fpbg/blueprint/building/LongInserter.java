package com.donrobo.fpbg.blueprint.building;

import net.sf.json.JSONObject;

public class LongInserter extends AbstractBuilding {
    private final int direction;

    public LongInserter(int x, int y, int direction) {
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
    public Building move(int x, int y) {
        return new LongInserter(getX() + x, getY() + y, direction);
    }

    @Override
    public String getName() {
        return "long-handed-inserter";
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
        json.put("direction", direction);
    }
}
