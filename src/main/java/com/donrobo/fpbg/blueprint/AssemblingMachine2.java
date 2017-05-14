package com.donrobo.fpbg.blueprint;

import net.sf.json.JSONObject;

public class AssemblingMachine2 extends AbstractBuilding {

    private final String recipe;

    public AssemblingMachine2(String recipe, int x, int y) {
        super(x, y);
        this.recipe = recipe;
    }

    @Override
    public String getName() {
        return "assembling-machine-2";
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
    public AssemblingMachine2 move(int x, int y) {
        return new AssemblingMachine2(getRecipe(), getX() + x, getY() + y);
    }

    public String getRecipe() {
        return recipe;
    }

    @Override
    protected int getBlueprintXOffset() {
        return 1;
    }

    @Override
    protected int getBlueprintYOffset() {
        return 1;
    }

    @Override
    protected void addCustomPropertiesToJson(JSONObject json) {
        json.put("recipe", recipe);
    }

    public static double getCraftingSpeed() {
        return 0.75;
    }
}
