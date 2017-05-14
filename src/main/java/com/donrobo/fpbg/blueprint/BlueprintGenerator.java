package com.donrobo.fpbg.blueprint;

import com.donrobo.fpbg.planner.ProductionLine;
import com.donrobo.fpbg.planner.ProductionStep;

import java.util.*;
import java.util.stream.Collectors;

import static com.donrobo.fpbg.blueprint.Direction.*;

public class BlueprintGenerator {

    public static Blueprint generateBlueprint(ProductionLine pl) {
        Blueprint blueprint = new Blueprint();

        Map<String, Integer> outputs = new HashMap<>();
        Map<String, Integer> productionStepIndexes = new HashMap<>();
        BlueprintSubsection assemblyMachines = new BlueprintSubsection();
        int xOffset = 0;
        for (ProductionStep productionStep : pl.getProductionSteps()) {
            BlueprintSubsection bs = generateSubsectionFor(productionStep);
            bs.addToBlueprintSubsection(xOffset - bs.getMinimumX(), -bs.getMaximumY(), assemblyMachines);
            productionStepIndexes.put(productionStep.getRecipe().getName(), xOffset);
            xOffset = bs.getMaximumX() + xOffset - bs.getMinimumX() + 2;
            if (productionStep.getResultPerSecond().size() != 1) {
                throw new RuntimeException("Multiple outputs not supported!");
            }
            outputs.put(productionStep.getResultPerSecond().get(0).getItem().getName(), xOffset - 2);
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
                    Integer productionIndex = productionStepIndexes.get(productionStep.getRecipe().getName());
                    int outputX = outputs.get(input);

                    boolean comingFromRight = i % 2 == 0;
                    boolean goingRight = outputX < productionIndex;

                    BlueprintSubsection bs = new BlueprintSubsection();
                    if (goingRight) {
                        bs.addBuilding(new Splitter(outputX, 0, DOWN));
                        bs.addBuilding(new YellowBelt(outputX, 1, DOWN));
                        if (!comingFromRight) {
                            for (int x = outputX + 1; x < productionIndex; x++) {
                                bs.addBuilding(new YellowBelt(x, 1, RIGHT));
                            }
                            bs.addBuilding(new YellowBelt(productionIndex, 0, UP));
                            bs.addBuilding(new YellowBelt(productionIndex, 1, UP));
                            bs.addBuilding(new YellowBelt(productionIndex, 2, UP));
                        } else {
                            for (int x = outputX + 1; x < productionIndex - 1; x++) {
                                bs.addBuilding(new YellowBelt(x, 1, RIGHT));
                            }
//                            bs.addBuilding(new UndergroundBelt(productionIndex-1, 1, RIGHT, true));
//                            bs.addBuilding(new UndergroundBelt(productionIndex+1, 1, RIGHT, true));
                            bs.addBuilding(new YellowBelt(productionIndex + 2, 1, UP));
                            bs.addBuilding(new YellowBelt(productionIndex + 2, 0, LEFT));
                            bs.addBuilding(new YellowBelt(productionIndex + 1, 0, LEFT));

                            bs.addBuilding(new YellowBelt(productionIndex, 0, UP));
                            bs.addBuilding(new YellowBelt(productionIndex, 1, UP));
                        }
                    }

                    int yOffset = findYPlaceForSection(blueprint, bs);
                    bs.addToBlueprint(0, yOffset, blueprint);
                    for (int beltY = 0; )
//                    if (goingRight) {
//                        int x = outputX + 1;
//                        int y = 0;
//                        int beltLength = inputX - x;
//                        while (blueprint.isOccupied(outputX, y, 2, 2) || blueprint.isOccupied(x, y, beltLength, 2)) {
//                            y++;
//                        }
//                        blueprint.addBuilding(new Splitter(outputX, y, DOWN));
//                        blueprint.addBuilding(new YellowBelt(outputX, y + 1, DOWN));
//                        for (int beltX = x; beltX <= inputX; beltX++) {
//                            blueprint.addBuilding(new YellowBelt(beltX, y + 1, RIGHT));
//                        }
//                    } else {
//                        int x = outputX - 1;
//                        int y = 0;
//                        int beltLength = x - inputX;
//                        while (blueprint.isOccupied(x, y, 2, 2) || blueprint.isOccupied(inputX, y, beltLength, 2)) {
//                            y++;
//                        }
//                        blueprint.addBuilding(new Splitter(x, y, DOWN));
//                        blueprint.addBuilding(new YellowBelt(outputX, y + 1, DOWN));
//                        for (int beltX = x; beltX >= inputX; beltX--) {
//                            blueprint.addBuilding(new YellowBelt(beltX, y + 1, LEFT));
//                        }
//                    }
                }
            }
        }

        return blueprint;
    }

    private static int findYPlaceForSection(Blueprint blueprint, BlueprintSubsection bs) {
        int y = 0;
        while (!bs.fits(blueprint, 0, y)) {
            y++;
        }
        return y;
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
                    subsection.addBuilding(new YellowBelt(beltX, y - i * 3, UP));
                }
                subsection.addBuilding(new YellowBelt(6, y - i * 3, DOWN));
            }
            subsection.addBuilding(new AssemblingMachine2(productionStep.getRecipe().getName(), 2, -2 - i * 3));
            subsection.addBuilding(new FastInserter(1, -i * 3, RIGHT));
            if (inputBelts == 2) {
                subsection.addBuilding(new LongInserter(1, -i * 3 - 1, RIGHT));
            }
            subsection.addBuilding(new FastInserter(5, -i * 3, RIGHT));
        }

        return subsection;
    }

}
