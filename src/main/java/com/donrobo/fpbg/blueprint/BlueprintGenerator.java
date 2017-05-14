package com.donrobo.fpbg.blueprint;

import com.donrobo.fpbg.planner.ProductionLine;
import com.donrobo.fpbg.planner.ProductionStep;

import java.util.*;
import java.util.stream.Collectors;

public class BlueprintGenerator {

    public static Blueprint generateBlueprint(ProductionLine pl) {
        Blueprint blueprint = new Blueprint();

        Map<String, Integer> outputs = new HashMap<>();
        BlueprintSubsection assemblyMachines = new BlueprintSubsection();
        int xOffset = 0;
        for (ProductionStep productionStep : pl.getProductionSteps()) {
            BlueprintSubsection bs = generateSubsectionFor(productionStep);
            bs.addToBlueprintSubsection(xOffset - bs.getMinimumX(), bs.getMaximumY(), assemblyMachines);
            xOffset = bs.getMaximumX() + xOffset - bs.getMinimumX() + 1;
            if (productionStep.getResultPerSecond().size() != 1) {
                throw new RuntimeException("Multiple outputs not supported!");
            }
            outputs.put(productionStep.getResultPerSecond().get(0).getItem().getName(), xOffset - 1);
        }

        assemblyMachines.addToBlueprint(0, 0, blueprint);

        List<String> rawInputs = findRawInputs(pl);

        for (ProductionStep productionStep : pl.getProductionSteps()) {
            List<String> inputs = productionStep.getIngredientsPerSecond().stream().map(is -> is.getItem().getName()).collect(Collectors.toList());
            for (int i = 0; i < inputs.size(); i++) {
                String input = inputs.get(i);
                if (rawInputs.contains(input)) {
                    continue;//TODO
                } else {
                    int x = outputs.get(input);
                    int y = 0;
                    while (blueprint.isOccupied(x, y)) {
                        y++;
                    }
                }
            }
        }

        return blueprint;
    }

    private static List<String> findRawInputs(ProductionLine pl) {
        Set<String> inputs = new HashSet<>();
        pl.getProductionSteps().forEach(ps -> ps.getIngredientsPerSecond().forEach(is -> inputs.add(is.getItem().getName()))); //all inputs

        Set<String> outputs = new HashSet<>();
        pl.getProductionSteps().forEach(ps -> ps.getResultPerSecond().forEach(is -> outputs.add(is.getItem().getName()))); //all inputs

        return inputs.stream().filter(input -> !outputs.contains(input)).collect(Collectors.toList());
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
