package com.donrobo.fpbg.blueprint;

import com.donrobo.fpbg.planner.ProductionLine;
import com.donrobo.fpbg.planner.ProductionStep;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BlueprintGenerator {

    public static Blueprint generateBlueprint(ProductionLine pl) {
        Blueprint blueprint = new Blueprint();

        BlueprintSubsection assemblyMachines = new BlueprintSubsection();
        int xOffset = 0;
        for (ProductionStep productionStep : pl.getProductionSteps()) {
            BlueprintSubsection bs = generateSubsectionFor(productionStep);
            bs.addToBlueprintSubsection(xOffset - bs.getMinimumX(), bs.getMaximumY(), assemblyMachines);
            xOffset = bs.getMaximumX() + xOffset - bs.getMinimumX() + 1;
        }

        assemblyMachines.addToBlueprint(0, 0, blueprint);

        List<String> duplicateInputs = new ArrayList<>();
        pl.getProductionSteps().forEach(ps -> ps.getIngredientsPerSecond().forEach(i -> duplicateInputs.add(i.getItem().getName())));

        List<String> inputs = duplicateInputs.stream().distinct().collect(Collectors.toList());

        BlueprintSubsection belts = new BlueprintSubsection();
        int inputIndex = 0;
        for (String ignored : inputs) {
            for (int x = assemblyMachines.getMinimumX() - 1; x <= assemblyMachines.getMaximumX(); x++) {
                belts.addBuilding(new YellowBelt(x, inputIndex, Direction.RIGHT));
            }
            inputIndex += 1;
        }
        int beltsYOffset = inputs.size();
        belts.addToBlueprint(0, beltsYOffset, blueprint);

        for (ProductionStep productionStep : pl.getProductionSteps()) {

        }

        return blueprint;
    }

    private static BlueprintSubsection generateSubsectionFor(ProductionStep productionStep) {
        BlueprintSubsection subsection = new BlueprintSubsection();

        int assemblingMachinesRequired = (int) Math.ceil(productionStep.getCraftingSpeed() / AssemblingMachine2.getCraftingSpeed());
        int inputBelts = (int) Math.ceil(productionStep.getIngredientsPerSecond().size() / 2.0);
        if (inputBelts > 2) {
            throw new RuntimeException("Not yet implemented!");
        }
        for (int i = 0; i < assemblingMachinesRequired; i++) {
            for (int y = -2; y <= 0; y++) {
                for (int beltX = -inputBelts + 1; beltX <= 0; beltX++) {
                    subsection.addBuilding(new YellowBelt(beltX, y - i * 3, Direction.UP));
                }
                subsection.addBuilding(new YellowBelt(6, y - i * 3, Direction.DOWN));
            }
            subsection.addBuilding(new AssemblingMachine2(productionStep.getRecipe().getName(), 2, -2 - i * 3));
            subsection.addBuilding(new FastInserter(1, -i * 3, Direction.RIGHT));
            if (inputBelts == 2) {
                subsection.addBuilding(new LongInserter(1, -i * 3 - 1, Direction.RIGHT));
            }
            subsection.addBuilding(new FastInserter(5, -i * 3, Direction.RIGHT));
        }

        return subsection;
    }

}
