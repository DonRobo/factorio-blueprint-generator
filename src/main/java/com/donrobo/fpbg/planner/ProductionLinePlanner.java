package com.donrobo.fpbg.planner;

import com.donrobo.fpbg.data.Item;
import com.donrobo.fpbg.data.Recipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductionLinePlanner {

    private final List<Recipe> recipes;

    public ProductionLinePlanner(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    public ProductionLine getProductionLineFor(FractionalItemStack is, List<Item> allowedInput) {
        Map<String, Double> required = new HashMap<>();

        List<Recipe> usefulRecipes = findRecipesThatProduce(is.getItem());

        return null; //TODO
    }

    private List<Recipe> findRecipesThatProduce(Item item) {
        return null;
    }
}
