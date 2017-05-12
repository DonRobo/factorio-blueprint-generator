package com.donrobo.fpbg;

import com.donrobo.fpbg.data.Item;
import com.donrobo.fpbg.data.ItemStack;
import com.donrobo.fpbg.data.Recipe;
import com.donrobo.fpbg.parser.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class RecipeLoader {

    public static List<Recipe> loadRecipes(File recipeFolder) {
        List<Recipe> recipes = new ArrayList<>();
        File[] files = recipeFolder.listFiles();

        assert files != null;

        Arrays.stream(files).map(RecipeLoader::loadRecipesFromFile).forEach(recipes::addAll);

        return recipes;
    }

    public static List<Recipe> loadRecipesFromFile(File recipeFile) {
        return loadRecipesFromFile(recipeFile, "normal");
    }

    public static List<Recipe> loadRecipesFromFile(File recipeFile, String mode) {
        try {
            List<Recipe> recipes = new ArrayList<>();

            ArrayElement arrayElement = (ArrayElement) Parser.parseFile(recipeFile);
            for (int i = 0; i < arrayElement.size(); i++) {
                MapElement recipeElement = (MapElement) arrayElement.get(i);
                if (!recipeElement.getString("type").equals("recipe")) {
                    continue;
                }

                if (recipeElement.containsKey(mode)) {
                    recipeElement = new MapElement(recipeElement, (MapElement) recipeElement.get(mode));
                }

                String name = recipeElement.getString("name");
                Boolean enabled = recipeElement.getBoolean("enabled");
                List<ItemStack> ingredients = getIngredients(recipeElement);
                List<ItemStack> result = getResult(recipeElement);
                Double energyRequired = recipeElement.getDouble("energy_required");
                HashMap<String, Object> extra = recipeElement.toRawJavaObject();
                recipes.add(new Recipe(name, enabled, ingredients, result, energyRequired, extra));
            }

            return recipes;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static List<ItemStack> getResult(MapElement recipeElement) {
        assert recipeElement.containsKey("result") != recipeElement.containsKey("results");


        if (recipeElement.containsKey("result")) {
            String resultName = recipeElement.getString("result");
            int count = recipeElement.containsKey("result_count") ? recipeElement.getInt("result_count") : 1;

            return Collections.singletonList(new ItemStack(count, new Item(resultName)));
        } else if (recipeElement.containsKey("results")) {
            List<ItemStack> itemStacks = new ArrayList<>();

            ArrayElement resultsElement = (ArrayElement) recipeElement.get("results");
            for (int i = 0; i < resultsElement.size(); i++) {
                MapElement resultElement = (MapElement) resultsElement.get(i);
                String name = resultElement.getString("name");
                int count = resultElement.getInt("amount");

                itemStacks.add(new ItemStack(count, new Item(name)));
            }

            return itemStacks;
        } else {
            return null;
        }
    }

    private static List<ItemStack> getIngredients(MapElement recipeElement) {
        List<ItemStack> itemStacks = new ArrayList<>();

        ArrayElement ingredients = (ArrayElement) recipeElement.get("ingredients");
        for (int i = 0; i < ingredients.size(); i++) {
            Element element = ingredients.get(i);
            if (element instanceof ArrayElement) {
                ArrayElement arrayElement = (ArrayElement) element;
                String name = ((PrimitiveElement) arrayElement.get(0)).getStringValue();
                int count = ((PrimitiveElement) arrayElement.get(1)).getIntValue();

                itemStacks.add(new ItemStack(count, new Item(name)));
            } else if (element instanceof MapElement) {
                MapElement resultElement = (MapElement) element;
                String name = resultElement.getString("name");
                int count = resultElement.getInt("amount");

                itemStacks.add(new ItemStack(count, new Item(name)));
            }
        }

        return itemStacks;
    }
}
