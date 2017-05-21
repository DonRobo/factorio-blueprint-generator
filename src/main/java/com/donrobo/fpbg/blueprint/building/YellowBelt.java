package com.donrobo.fpbg.blueprint.building;

import com.donrobo.fpbg.blueprint.Direction;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
    protected double getBlueprintXOffset() {
        return 0;
    }

    @Override
    protected double getBlueprintYOffset() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        YellowBelt that = (YellowBelt) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(direction, that.direction)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(direction)
                .toHashCode();
    }

    public int getDirection() {
        return direction;
    }
}