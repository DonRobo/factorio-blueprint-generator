package com.donrobo.fpbg.blueprint;

import net.sf.json.JSONObject;

public abstract class AbstractBuilding implements Building {
    private final int x;
    private final int y;

    public AbstractBuilding(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        json.put("name", getName());
        JSONObject posJson = new JSONObject();
        posJson.put("x", getX() + getBlueprintXOffset());
        posJson.put("y", getY() + getBlueprintYOffset());
        json.put("position", posJson);

        addCustomPropertiesToJson(json);

        return json;
    }

    protected void addCustomPropertiesToJson(JSONObject json) {
    }

    protected abstract double getBlueprintXOffset();

    protected abstract double getBlueprintYOffset();
}
