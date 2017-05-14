package com.donrobo.fpbg.blueprint.building;

import net.sf.json.JSONObject;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AbstractBuilding that = (AbstractBuilding) o;

        return new EqualsBuilder()
                .append(getX(), that.getX())
                .append(getY(), that.getY())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getX())
                .append(getY())
                .toHashCode();
    }
}
