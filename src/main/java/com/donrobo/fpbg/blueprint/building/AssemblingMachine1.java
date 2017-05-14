package com.donrobo.fpbg.blueprint.building;

import net.sf.json.JSONObject;

public class AssemblingMachine1 extends AbstractBuilding {

    private final String recipe;

    public AssemblingMachine1(String recipe, int x, int y) {
        super(x, y);
        this.recipe = recipe;
    }

    @Override
    public int getWidth() {
        return 3;
    }

    @Override
    public int getHeight() {
        return 3;
    }

    @Override
    public AssemblingMachine1 move(int x, int y) {
        return new AssemblingMachine1(recipe, getX() + x, getY() + y);
    }

    @Override
    public String getName() {
        return "assembling-machine-1";
    }

    public String getRecipe() {
        return recipe;
    }

    @Override
    protected void addCustomPropertiesToJson(JSONObject json) {
        json.put("recipe", recipe);
    }

    @Override
    protected double getBlueprintXOffset() {
        return 1;
    }

    @Override
    protected double getBlueprintYOffset() {
        return 1;
    }
}
