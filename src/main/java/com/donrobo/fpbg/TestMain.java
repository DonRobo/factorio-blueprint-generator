package com.donrobo.fpbg;

import com.donrobo.fpbg.data.Recipe;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class TestMain {

    public static void main(String[] args) throws IOException {
        File file = new File("G:\\Games\\SteamLibrary\\steamapps\\common\\Factorio\\data\\base\\prototypes\\recipe");
        List<Recipe> recipes = RecipeLoader.loadRecipes(file);
        recipes.forEach(System.out::println);
//        Element element = Parser.parseFile(new File(file, "recipe.lua"));
//        System.out.println(element);
//        RecipeLoader.
    }
}
