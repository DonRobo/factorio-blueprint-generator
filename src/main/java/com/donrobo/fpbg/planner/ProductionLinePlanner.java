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

    public ProductionLine getProductionLineFor(List<Item> allowedInput, FractionalItemStack... requests) {
        ProductionLine productionLine = new ProductionLine();
        productionLine.addInputMaterials(allowedInput);

        Map<Recipe, Double> usedRecipes = new HashMap<>();

        Map<String, Double> required = new HashMap<>();
        Arrays.stream(requests).forEach(r -> required.put(r.getItem().getName(), r.getCount()));

        resolveRecipes(allowedInput, usedRecipes, required);

        System.out.println(String.format("Required items for %s: %s",
                String.join(",", Arrays.stream(requests).map(req -> req.getItem().getName()).collect(Collectors.toList())),
                required));
        System.out.println(String.format("Using the recipes: %s", usedRecipes.entrySet().stream().map(
                r -> String.format(Locale.ENGLISH, "%s*%.1f", r.getKey().getName(), r.getValue())
        ).collect(Collectors.toList()).toString()));

        usedRecipes.entrySet().stream().map(e -> new ProductionStep(e.getKey(), e.getValue())).forEach(productionLine::addProductionStep);

        productionLine.clearUnusedInputMaterials();
        return productionLine;
    }

    private void resolveRecipes(List<Item> allowedInput, Map<Recipe, Double> usedRecipes, Map<String, Double> required) {
        while (containsMoreThan(required, allowedInput)) {
            Optional<String> optionalItemToProduce = required.keySet().stream().filter(item -> itemNotInList(item, allowedInput)).findAny();
            if (optionalItemToProduce.isPresent()) {
                String itemToProduce = optionalItemToProduce.get();
                double requiredCount = required.get(itemToProduce);
                Recipe usefulRecipe = findRecipeThatProduces(itemToProduce);
                if (usefulRecipe == null) {
                    throw new RuntimeException("Couldn't find recipe for " + itemToProduce);
                }
                for (ItemStack ingredient : usefulRecipe.getIngredients()) {
                    Double requiredIngredientCount = required.get(ingredient.getItem().getName());
                    if (requiredIngredientCount == null) {
                        requiredIngredientCount = 0.0;
                    }
                    requiredIngredientCount += (ingredient.getCount() * requiredCount) / ((Integer) usefulRecipe.getResult().getCount()).doubleValue();
                    required.put(ingredient.getItem().getName(), requiredIngredientCount);
                }
                required.remove(itemToProduce);
                Double craftingSpeed = usefulRecipe.getEnergyRequired() / usefulRecipe.getResult().getCount();
                usedRecipes.put(usefulRecipe, craftingSpeed * requiredCount);
            } else {
                throw new RuntimeException("Should never get here!");
            }
        }
    }

    private boolean containsMoreThan(Map<String, Double> required, List<Item> allowedInput) {
        return required.keySet().stream().anyMatch( //is there any item in required where this is true:
                item -> itemNotInList(item, allowedInput)
        );
    }

    private boolean itemNotInList(String item, List<Item> list) {
        return list.stream().noneMatch(i -> i.getName().equals(item));
    }

    private Recipe findRecipeThatProduces(String item) {
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
        return recipes.stream().filter(r -> r.getResult().getItem().getName().equals(item)).collect(Collectors.toList());
    }

    public static List<Item> getDefaultAllowedItems() {
        String[] allowed = new String[]{
                "iron-plate",
                "copper-plate",
                "steel-plate",
                "plastic-bar",
                "coal",
                "crude-oil",
                "heavy-oil",
                "light-oil",
                "petroleum-gas",
                "water",
                "sulfuric-acid",
                "sulfuric"
        };
        return Arrays.stream(allowed).map(Item::new).collect(Collectors.toList());
    }
}
