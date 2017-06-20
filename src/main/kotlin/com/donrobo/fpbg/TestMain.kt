package com.donrobo.fpbg

import com.donrobo.fpbg.data.Item
import com.donrobo.fpbg.generator.ProductionLineGenerator
import com.donrobo.fpbg.planner.FractionalItemStack
import com.donrobo.fpbg.planner.ProductionLinePlanner
import org.apache.commons.io.FileUtils
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.File

fun main(args: Array<String>) {
    val file = File("G:\\Games\\SteamLibrary\\steamapps\\common\\Factorio\\")
    //        File file = new File("D:\\Games\\Steam\\steamapps\\common\\Factorio\\");
    val recipes = RecipeLoader.loadRecipes(file)

//    println(recipes.map { it.name }.joinToString(","))

    val productionLinePlanner = ProductionLinePlanner(recipes)

    //        ProductionLine productionLine = productionLinePlanner.getProductionLineFor(ProductionLinePlanner.getDefaultAllowedItems(),
    //                new FractionalItemStack(1, new Item("transport-belt")),
    //                new FractionalItemStack(1, new Item("splitter")),
    //                new FractionalItemStack(1, new Item("underground-belt")),
    //                new FractionalItemStack(1, new Item("fast-transport-belt")),
    //                new FractionalItemStack(1, new Item("fast-splitter")),
    //                new FractionalItemStack(1, new Item("fast-underground-belt"))
    //        );
    val productionLine = productionLinePlanner.getProductionLineFor(ProductionLinePlanner.getDefaultAllowedItems(),
            //                new FractionalItemStack(1, new Item("iron-gear-wheel")),
            //                new FractionalItemStack(1, new Item("copper-cable"))
            FractionalItemStack(1.0, Item("transport-belt"))
    )
    println(productionLine)

    val blueprint = ProductionLineGenerator(productionLine).generateBlueprint()

    FileUtils.writeStringToFile(File("src/main/resources/outputblueprint.json"), blueprint.toJson().toString(), "UTF-8")
    println(blueprint.toBlueprintString())
    val stringSelection = StringSelection(blueprint.toBlueprintString())
    Toolkit.getDefaultToolkit().systemClipboard.setContents(stringSelection, stringSelection)
    println(blueprint.visualizer().visualize())
    println("Copied to clipboard")
}

