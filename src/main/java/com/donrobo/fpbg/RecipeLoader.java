package com.donrobo.fpbg;

import com.donrobo.fpbg.data.Item;
import com.donrobo.fpbg.data.ItemStack;
import com.donrobo.fpbg.data.Recipe;
import com.donrobo.fpbg.parser.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class RecipeLoader {

    public static List<Recipe> loadRecipes(File recipeFolder) {
        List<Recipe> recipes = new ArrayList<>();
        File[] files = recipeFolder.listFiles();

        assert files != null;

        Arrays.stream(files).filter(File::isDirectory).map(RecipeLoader::loadRecipes).forEach(recipes::addAll);
        Arrays.stream(files).filter(File::isFile).filter(f -> f.getName().toLowerCase().endsWith(".lua")).map(RecipeLoader::loadRecipesFromFile).forEach(recipes::addAll);

        return recipes;
    }

    public static List<Recipe> loadRecipesFromFile(File recipeFile) {
        return loadRecipesFromFile(recipeFile, "normal");
    }

    public static List<Recipe> loadRecipesFromFile(File recipeFile, String mode) {
        try {
            List<Recipe> recipes = new ArrayList<>();

            ArrayElement arrayElement = (ArrayElement) Parser.parseFile(recipeFile);
            if (arrayElement != null) {
                for (int i = 0; i < arrayElement.size(); i++) {
                    if (!(arrayElement.get(i) instanceof MapElement)) {
                        continue;
                    }
                    MapElement recipeElement = (MapElement) arrayElement.get(i);
                    if (!recipeElement.containsKey("type") || !recipeElement.getString("type").equals("recipe")) {
                        continue;
                    }

                    if (recipeElement.containsKey(mode)) {
                        recipeElement = new MapElement(recipeElement, (MapElement) recipeElement.get(mode));
                    }

                    String name = recipeElement.getString("name");
                    Boolean enabled = recipeElement.getBoolean("enabled");
                    if (enabled == null) {
                        enabled = false;
                    }
                    List<ItemStack> ingredients = getIngredients(recipeElement);
                    ItemStack result = getResult(recipeElement);
                    Double energyRequired = recipeElement.getDouble("energy_required");
                    HashMap<String, Object> extra = recipeElement.toRawJavaObject();
                    if (result != null) {
                        if (energyRequired != null) {
                            recipes.add(new Recipe(name, enabled, ingredients, result, energyRequired, extra));
                        } else {
                            recipes.add(new Recipe(name, enabled, ingredients, result, extra));
                        }
                    }
                }
            }

            return recipes;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (ParsingError ex) {
            System.out.println("Skipping " + recipeFile);
            return new ArrayList<>();
        }
    }

    private static ItemStack getResult(MapElement recipeElement) {
        assert recipeElement.containsKey("result") != recipeElement.containsKey("results");

        if (recipeElement.containsKey("result")) {
            String resultName = recipeElement.getString("result");
            int count = recipeElement.containsKey("result_count") ? recipeElement.getInt("result_count") : 1;

            return new ItemStack(count, new Item(resultName));
        } else if (recipeElement.containsKey("results")) {
            List<ItemStack> itemStacks = new ArrayList<>();

            ArrayElement resultsElement = (ArrayElement) recipeElement.get("results");
            for (int i = 0; i < resultsElement.size(); i++) {
                MapElement resultElement = (MapElement) resultsElement.get(i);
                String name = resultElement.getString("name");
                int count = resultElement.getInt("amount");

                itemStacks.add(new ItemStack(count, new Item(name)));
            }

            if (itemStacks.size() > 1) {
                return null;
//                throw new UnsupportedOperationException("Multiple results not supported");
            } else if (itemStacks.size() == 1) {
                return itemStacks.get(0);
            } else {
                return null;
            }
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
