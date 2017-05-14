package com.donrobo.fpbg.blueprint.building;

import net.sf.json.JSONObject;

public interface Building {
    JSONObject toJson();

    int getWidth();

    int getHeight();

    int getX();

    int getY();

    Building move(int x, int y);

    String getName();
}
