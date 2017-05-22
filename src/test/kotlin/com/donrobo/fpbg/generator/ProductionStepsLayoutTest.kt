package com.donrobo.fpbg.generator

import com.donrobo.fpbg.data.BeltIoType
import com.donrobo.fpbg.data.BeltSide
import com.donrobo.fpbg.data.PositionalBeltIo
import com.donrobo.fpbg.planner.ProductionStep
import com.donrobo.fpbg.test.ProductionStepGenerator
import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

class ProductionStepsLayoutTest : StringSpec() {

    private val productionStepGenerator = ProductionStepGenerator()

    init {
        "Width calculation" {
            val width1 = 2 + 2 + 3
            val width2 = width1
            val width3 = width2 + 1
            val width4 = width3

            ProductionStepsLayout(aProductionStepList(3)).width shouldBe width3
            ProductionStepsLayout(aProductionStepList(3, 4)).width shouldBe width3 + width4
            ProductionStepsLayout(aProductionStepList(3, 1)).width shouldBe width3 + width1
            ProductionStepsLayout(aProductionStepList(3, 1, 1, 3, 2, 4)).width shouldBe width3 + width1 + width1 + width3 + width2 + width4
        }
        //TODO
//        "Height calculation" {
//            ProductionStepsLayout(listOf(productionStepGenerator.generate())).height shouldBe 3
//        }
        "Input calculation" {
            val ps3 = productionStepGenerator.generateProductionStep(ingredientCount = 3, ingredientPrefix = "item-1_")
            val ps2 = productionStepGenerator.generateProductionStep(ingredientCount = 2, ingredientPrefix = "item-2_")

            val inputs = ProductionStepsLayout(listOf(ps3, ps2)).inputs
            inputs.size shouldBe 5
            inputs[0] shouldBe PositionalBeltIo(
                    position = Pair(0, 0),
                    beltSide = BeltSide.LEFT,
                    type = BeltIoType.INPUT,
                    item = "item-1_1")
            inputs[1] shouldBe PositionalBeltIo(
                    position = Pair(0, 0),
                    beltSide = BeltSide.RIGHT,
                    type = BeltIoType.INPUT,
                    item = "item-1_2")
            inputs[2] shouldBe PositionalBeltIo(
                    position = Pair(1, 0),
                    beltSide = BeltSide.BOTH,
                    type = BeltIoType.INPUT,
                    item = "item-1_3")
            inputs[3] shouldBe PositionalBeltIo(
                    position = Pair(8, 0),
                    beltSide = BeltSide.LEFT,
                    type = BeltIoType.INPUT,
                    item = "item-2_1")
            inputs[4] shouldBe PositionalBeltIo(
                    position = Pair(8, 0),
                    beltSide = BeltSide.RIGHT,
                    type = BeltIoType.INPUT,
                    item = "item-2_2")
        }
    }

    private fun aProductionStepList(vararg ingredientCounts: Int): List<ProductionStep> {
        return ingredientCounts.map { productionStepGenerator.generateProductionStep(ingredientCount = it) }
    }

}
