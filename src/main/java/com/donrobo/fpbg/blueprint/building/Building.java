package com.donrobo.fpbg.blueprint.building;

import com.donrobo.fpbg.blueprint.Direction;
import com.donrobo.fpbg.data.Int2;
import net.sf.json.JSONObject;

public interface Building {
    JSONObject toJson();

    int getWidth();

    int getHeight();

    int getX();

    int getY();

    Building move(int x, int y);

    String getName();

    static <T extends Building> T move(T building, int offset, Direction direction) {
        Int2 offsetInt2 = direction.move(new Int2(0, 0), offset);

        Building movedBuilding = building.move(offsetInt2.getX(), offsetInt2.getY());
        return (T) movedBuilding;
    }

    Int2 getPosition();

    char getVisualizationCharacter();
}
