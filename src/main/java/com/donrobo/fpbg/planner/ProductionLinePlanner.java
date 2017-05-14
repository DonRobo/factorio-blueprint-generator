package com.donrobo.fpbg.planner;

import com.donrobo.fpbg.data.Item;
import com.donrobo.fpbg.data.ItemStack;
import com.donrobo.fpbg.data.Recipe;

import java.util.*;
import java.util.stream.Collectors;

public class ProductionLinePlanner {

    private final List<Recipe> recipes;

    public ProductionLinePlanner(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    public ProductionLine getProductionLineFor(FractionalItemStack is, List<Item> allowedInput) {
        Set<Recipe> usedRecipes = new HashSet<>();

        Map<String, Double> required = new HashMap<>();
        required.put(is.getItem().getName(), is.getCount());

        while (containsMoreThan(required, allowedInput)) {
            Optional<String> optionalItemToProduce = required.keySet().stream().filter(item -> itemNotInList(item, allowedInput)).findAny();
            if (optionalItemToProduce.isPresent()) {
                String itemToProduce = optionalItemToProduce.get();
                Recipe usefulRecipe = findRecipeThatProduce(itemToProduce);
                if (usefulRecipe == null) {
                    throw new RuntimeException("Couldn't find recipe for " + itemToProduce);
                }
                for (ItemStack ingredient : usefulRecipe.getIngredients()) {
                    Double requiredCount = required.get(ingredient.getItem().getName());
                    if (requiredCount == null) {
                        requiredCount = 0.0;
                    }
                    requiredCount += ingredient.getCount();
                    required.put(ingredient.getItem().getName(), requiredCount);
                }
                required.remove(itemToProduce);
                usedRecipes.add(usefulRecipe);
            } else {
                throw new RuntimeException("Should never get here!");
            }
        }

        System.out.println(String.format("Required items for %s: %s", is.getItem(), required.keySet()));
        System.out.println(String.format("Using the recipes: %s", usedRecipes.stream().map(Recipe::getName).collect(Collectors.toList()).toString()));
        return null; //TODO
    }

    private boolean containsMoreThan(Map<String, Double> required, List<Item> allowedInput) {
        return required.keySet().stream().anyMatch( //is there any item in required where this is true:
                item -> itemNotInList(item, allowedInput)
        );
    }

    private boolean itemNotInList(String item, List<Item> list) {
        return list.stream().noneMatch(i -> i.getName().equals(item));
    }

    private Recipe findRecipeThatProduce(String item) {
        List<Recipe> recipes = findRecipesThatProduce(item);

        if (recipes.size() == 1) {
            return recipes.get(0);
        } else if (recipes.size() > 1) {
            return recipes.get(0);//TODO
        } else {
            return null;
        }
    }

    private List<Recipe> findRecipesThatProduce(String item) {
        return recipes.stream().filter(r -> r.isProducingItem(item)).collect(Collectors.toList());
    }

    public static List<Item> getDefaultAllowedItems() {
        String[] allowed = new String[]{
                "iron-plate",
                "copper-plate",
                "steel-plate",
                "platic-bar",
                "coal",
                "crude-oil",
                "heavy-oil",
                "light-oil",
                "petroleum-gas"
        };
        return Arrays.stream(allowed).map(Item::new).collect(Collectors.toList());
    }
}
