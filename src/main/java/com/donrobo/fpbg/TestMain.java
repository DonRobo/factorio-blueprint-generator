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

public class TestMain {

    public static void main(String[] args) throws IOException {
        File file = new File("D:\\Games\\Steam\\steamapps\\common\\Factorio\\");
        List<Recipe> recipes = RecipeLoader.loadRecipes(file);

        ProductionLinePlanner productionLinePlanner = new ProductionLinePlanner(recipes);

        ProductionLine productionLine = productionLinePlanner.getProductionLineFor(new FractionalItemStack(1, new Item("assembling-machine-2")),
                ProductionLinePlanner.getDefaultAllowedItems());
        System.out.println(productionLine);

        Blueprint blueprint = BlueprintGenerator.generateBlueprint(productionLine);
        FileUtils.writeStringToFile(new File("src/main/resources/outputblueprint.json"), blueprint.toJson().toString(), "UTF-8");
        System.out.println(blueprint.toBlueprintString());
        StringSelection stringSelection = new StringSelection(blueprint.toBlueprintString());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, stringSelection);
        System.out.println("Copied to clipboard");
    }
}
