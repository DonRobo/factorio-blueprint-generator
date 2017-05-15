package com.donrobo.fpbg.blueprint;

import com.donrobo.fpbg.blueprint.building.Building;
import com.donrobo.fpbg.data.Int2;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.zip.DeflaterOutputStream;

public class Blueprint {

    private final List<Building> buildings = new ArrayList<>();

    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        JSONObject blueprintJson = new JSONObject();
        JSONArray entitiesArray = new JSONArray();

        int i = 0;
        for (Building building : buildings) {
            i++;
            JSONObject buildingJson = building.toJson();
            buildingJson.put("entity_number", i);
            entitiesArray.add(buildingJson);
        }

        blueprintJson.put("entities", entitiesArray);
        json.put("blueprint", blueprintJson);
        json.put("item", "blueprint");

        return json;
    }

    public static String toBlueprintString(String input) {
        ByteArrayOutputStream baos = null;
        DeflaterOutputStream dos = null;

        try {
            baos = new ByteArrayOutputStream();
            dos = new DeflaterOutputStream(baos);

            dos.write(input.getBytes(Charset.forName("UTF-8")));
            IOUtils.closeQuietly(dos);

            return "0" + Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException ex) {
            throw new RuntimeException("Shouldn't happen here", ex);
        } finally {
            IOUtils.closeQuietly(baos);
            IOUtils.closeQuietly(dos);
        }
    }

    public String toBlueprintString() {
        return toBlueprintString(toJson().toString());
    }

    public void addBuilding(Building building) {
        if (isOccupied(building)) {
            throw new RuntimeException("Trying to place building on top of building!");
        }
        buildings.add(building);
    }

    public boolean isOccupied(Int2 pos) {
        return isOccupied(pos.getX(), pos.getY());
    }

    public boolean isOccupied(int x, int y) {
        return get(x, y) != null;
    }

    public boolean isOccupied(int x, int y, int width, int height) {
        for (int xI = x; xI < x + width; xI++) {
            for (int yI = y; yI < y + height; yI++) {
                if (isOccupied(xI, yI)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isOccupied(Building building) {
        return isOccupied(building.getX(), building.getY(), building.getWidth(), building.getHeight());
    }

    public Building get(int x, int y) {
        return buildings.stream().filter(building -> {
            boolean xFits = building.getX() <= x && building.getX() + building.getWidth() - 1 >= x;
            boolean yFits = building.getY() <= y && building.getY() + building.getHeight() - 1 >= y;

            return xFits && yFits;
        }).findAny().orElse(null);
    }

    public Building remove(int x, int y) {
        Building building = get(x, y);
        if (building != null) {
            buildings.remove(building);
        }
        return building;
    }

    public void addBlueprint(Blueprint blueprint, int xOffset, int yOffset) {
        blueprint.buildings.stream().map(b -> b.move(xOffset, yOffset)).forEach(this::addBuilding);
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

    public boolean canPlace(Blueprint blueprint, int x, int y) {
        for (Building building : blueprint.buildings) {
            if (isOccupied(building.move(x, y))) {
                return false;
            }
        }
        return true;
    }

    public List<Building> getBuildings() {
        return buildings;
    }

    public Building get(Int2 pos) {
        return get(pos.getX(), pos.getY());
    }
}
