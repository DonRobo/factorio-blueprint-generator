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

    public int getMaximumLeft() {
        return buildings.stream().mapToInt(Building::getX).min().orElse(0);
    }

    public int getMaximumRight() {
        return buildings.stream().mapToInt(building -> building.getX() + building.getWidth()).max().orElse(0);
    }

    public int getMaximumUp() {
        return buildings.stream().mapToInt(Building::getY).min().orElse(0);
    }

    public int getMaximumDown() {
        return buildings.stream().mapToInt(building -> building.getY() + building.getHeight()).max().orElse(0);
    }

    public int getWidth() {
        return getMaximumRight() - getMaximumLeft();
    }

    public int getHeight() {
        return getMaximumDown() - getMaximumUp();
    }

}
