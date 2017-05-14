package com.donrobo.fpbg.blueprint;

import com.donrobo.fpbg.planner.ProductionLine;
import com.donrobo.fpbg.planner.ProductionStep;

public class BlueprintGenerator {

    public static Blueprint generateBlueprint(ProductionLine pl) {
        Blueprint blueprint = new Blueprint();

        int x = 0;

        for (ProductionStep productionStep : pl.getProductionSteps()) {
            BlueprintSubsection bs = generateSubsectionFor(productionStep);
            bs.addToBlueprint(x - bs.getMaximumLeft(), bs.getMaximumDown(), blueprint);
            x = bs.getMaximumRight() + x - bs.getMaximumLeft();
        }
        return blueprint;
    }

    private static BlueprintSubsection generateSubsectionFor(ProductionStep productionStep) {
        BlueprintSubsection subsection = new BlueprintSubsection();

        int assemblingMachinesRequired = (int) Math.ceil(productionStep.getCraftingSpeed() / AssemblingMachine2.getCraftingSpeed());

        for (int i = 0; i < assemblingMachinesRequired; i++) {
            for (int y = -2; y <= 0; y++) {
                subsection.addBuilding(new YellowBelt(0, y - i * 3, Direction.UP));
                subsection.addBuilding(new YellowBelt(6, y - i * 3, Direction.DOWN));
            }
            subsection.addBuilding(new AssemblingMachine2(productionStep.getRecipe().getName(), 2, -2 - i * 3));
            subsection.addBuilding(new FastInserter(1, -i * 3, Direction.RIGHT));
            subsection.addBuilding(new FastInserter(5, -i * 3, Direction.RIGHT));
        }

        return subsection;
    }

}
