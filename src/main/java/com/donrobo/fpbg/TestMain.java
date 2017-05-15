package com.donrobo.fpbg;

import com.donrobo.fpbg.blueprint.Blueprint;
import com.donrobo.fpbg.blueprint.BlueprintGenerator;
import com.donrobo.fpbg.data.Item;
import com.donrobo.fpbg.data.Recipe;
import com.donrobo.fpbg.planner.FractionalItemStack;
import com.donrobo.fpbg.planner.ProductionLine;
import com.donrobo.fpbg.planner.ProductionLinePlanner;
import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class TestMain {

    public static void main(String[] args) throws IOException {
        File file = new File("G:\\Games\\SteamLibrary\\steamapps\\common\\Factorio\\");
        List<Recipe> recipes = RecipeLoader.loadRecipes(file);

        System.out.println(String.join(",", recipes.stream().map(Recipe::getName).collect(Collectors.toList())));

        ProductionLinePlanner productionLinePlanner = new ProductionLinePlanner(recipes);

        ProductionLine productionLine = productionLinePlanner.getProductionLineFor(ProductionLinePlanner.getDefaultAllowedItems(),
                new FractionalItemStack(1, new Item("transport-belt")),
                new FractionalItemStack(1, new Item("splitter")),
                new FractionalItemStack(1, new Item("underground-belt")),
                new FractionalItemStack(1, new Item("fast-transport-belt")),
                new FractionalItemStack(1, new Item("fast-splitter")),
                new FractionalItemStack(1, new Item("fast-underground-belt"))
        );
        System.out.println(productionLine);

        Blueprint blueprint = BlueprintGenerator.generateBlueprint(productionLine);
        FileUtils.writeStringToFile(new File("src/main/resources/outputblueprint.json"), blueprint.toJson().toString(), "UTF-8");
        System.out.println(blueprint.toBlueprintString());
        StringSelection stringSelection = new StringSelection(blueprint.toBlueprintString());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, stringSelection);
        System.out.println("Copied to clipboard");
    }
}
