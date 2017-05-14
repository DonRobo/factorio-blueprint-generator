package com.donrobo.fpbg;

import com.donrobo.fpbg.data.Item;
import com.donrobo.fpbg.data.Recipe;
import com.donrobo.fpbg.planner.FractionalItemStack;
import com.donrobo.fpbg.planner.ProductionLinePlanner;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class TestMain {

    public static void main(String[] args) throws IOException {
        File file = new File("D:\\Games\\Steam\\steamapps\\common\\Factorio\\");
        List<Recipe> recipes = RecipeLoader.loadRecipes(file);
        ProductionLinePlanner productionLinePlanner = new ProductionLinePlanner(recipes);
        System.out.println("Allowed items:");
        ProductionLinePlanner.getDefaultAllowedItems().forEach(System.out::println);
        productionLinePlanner.getProductionLineFor(new FractionalItemStack(1, new Item("electronic-circuit")),
                ProductionLinePlanner.getDefaultAllowedItems());
//        recipes.forEach(System.out::println);
//        Element element = Parser.parseFile(new File(file, "recipe.lua"));
//        System.out.println(element);
//        RecipeLoader.
    }
}
