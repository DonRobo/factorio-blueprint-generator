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
    val file = File("C:\\Games\\SteamLibrary\\steamapps\\common\\Factorio\\")
    //        File file = new File("D:\\Games\\Steam\\steamapps\\common\\Factorio\\");
    val recipes = RecipeLoader.loadRecipes(file)

//    println(recipes.map { it.name }.joinToString(","))

    val productionLinePlanner = ProductionLinePlanner(recipes)

    val productionLine = productionLinePlanner.getProductionLineFor(ProductionLinePlanner.getDefaultAllowedItems(),
            FractionalItemStack(1.0, Item("transport-belt")),
            FractionalItemStack(1.0, Item("splitter")),
            FractionalItemStack(1.0, Item("underground-belt")),
            FractionalItemStack(1.0, Item("fast-transport-belt")),
            FractionalItemStack(1.0, Item("fast-splitter")),
            FractionalItemStack(1.0, Item("fast-underground-belt"))
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

