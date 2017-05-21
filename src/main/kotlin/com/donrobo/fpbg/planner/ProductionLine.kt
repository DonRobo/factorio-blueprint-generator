package com.donrobo.fpbg.planner

import com.donrobo.fpbg.data.Item
import java.util.*

class ProductionLine {

    private val mutableInputMaterials = ArrayList<String>()
    private val mutableProductionSteps = ArrayList<ProductionStep>()

    fun addProductionStep(productionStep: ProductionStep) {
        if (isUsingOnlyInputMaterials(productionStep)) {
            mutableProductionSteps.add(0, productionStep)
        } else {
            val (_, item) = productionStep.resultPerSecond
            val result = item.name
            var index = 0
            while (index < mutableProductionSteps.size && !isRequiredBy(result, mutableProductionSteps[index])) {
                index++
            }
            mutableProductionSteps.add(index, productionStep)
        }
    }

    fun isRequiredBy(item: String, productionStep: ProductionStep) = productionStep.ingredientsPerSecond.map { it.item.name }.contains(item)

    fun isUsingOnlyInputMaterials(productionStep: ProductionStep) = productionStep.ingredientsPerSecond.map { it.item.name }.all { inputMaterials.contains(it) }

    fun addInputMaterials(inputMaterials: List<Item>) = this.mutableInputMaterials.addAll(inputMaterials.map { it.name })

    fun clearUnusedInputMaterials() {
        val usedInputMaterials = inputMaterials.filter { mat -> productionSteps.any { ps -> isRequiredBy(mat, ps) } }

        mutableInputMaterials.clear()
        mutableInputMaterials.addAll(usedInputMaterials)
    }

    val inputMaterials: List<String> get() = mutableInputMaterials

    val productionSteps: List<ProductionStep> get() = mutableProductionSteps

    val allIngredients: List<String> get() = productionSteps.flatMap { it.ingredientsPerSecond.map { it.item.name } }.distinct()

    fun getProductionStepsThatRequire(input: String): List<ProductionStep> = productionSteps.filter { it.requires(input) }

}
