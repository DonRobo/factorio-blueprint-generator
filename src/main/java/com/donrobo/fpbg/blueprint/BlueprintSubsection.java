package com.donrobo.fpbg.blueprint;

import java.util.ArrayList;
import java.util.List;

public class BlueprintSubsection {

    private final List<Building> buildings = new ArrayList<>();

    public void addBuilding(Building building) {
        buildings.add(building);
    }

    public void addToBlueprint(int xOffset, int yOffset, Blueprint blueprint) {
        buildings.stream().map(b -> b.move(xOffset, yOffset)).forEach(blueprint::addBuilding);
    }

    public void addToBlueprintSubsection(int xOffset, int yOffset, BlueprintSubsection subsection) {
        buildings.stream().map(b -> b.move(xOffset, yOffset)).forEach(subsection::addBuilding);
    }

    public int getMinimumX() {
        return buildings.stream().mapToInt(Building::getX).min().orElse(0);
    }

    public int getMaximumX() {
        return buildings.stream().mapToInt(building -> building.getX() + building.getWidth() - 1).max().orElse(0);
    }

    public int getMinimumY() {
        return buildings.stream().mapToInt(Building::getY).min().orElse(0);
    }

    public int getMaximumY() {
        return buildings.stream().mapToInt(building -> building.getY() + building.getHeight() - 1).max().orElse(0);
    }

    public int getWidth() {
        return getMaximumX() - getMinimumX();
    }

    public int getHeight() {
        return getMaximumY() - getMinimumY();
    }

}
