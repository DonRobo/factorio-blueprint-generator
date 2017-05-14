package com.donrobo.fpbg.blueprint;

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
        buildings.add(building);
    }

    public boolean isOccupied(int x, int y) {
        return buildings.stream().anyMatch(building -> {
            boolean xFits = building.getX() <= x && building.getX() + building.getWidth() - 1 >= x;
            boolean yFits = building.getY() <= y && building.getY() + building.getHeight() - 1 >= y;

            return xFits && yFits;
        });
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
}
