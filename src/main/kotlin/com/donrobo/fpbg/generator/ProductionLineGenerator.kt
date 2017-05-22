package com.donrobo.fpbg.generator

import com.donrobo.fpbg.blueprint.Blueprint
import com.donrobo.fpbg.planner.ProductionLine

fun generateBlueprint(productionLine: ProductionLine): Blueprint = ProductionStepsLayout(productionLine.productionSteps).generateBlueprint()
