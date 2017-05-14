package com.donrobo.fpbg.blueprint;

import com.donrobo.fpbg.blueprint.building.*;
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
        Blueprint assemblyMachines = new Blueprint();
        int xOffset = 0;
        for (ProductionStep productionStep : pl.getProductionSteps()) {
            Blueprint bs = generateSubsectionFor(productionStep);
            assemblyMachines.addBlueprint(bs, xOffset - bs.getMinimumX(), -bs.getMaximumY());
            productionStepIndexes.put(productionStep.getRecipe().getName(), xOffset);
            xOffset = bs.getMaximumX() + xOffset - bs.getMinimumX() + 2;
            if (productionStep.getResultPerSecond().size() != 1) {
                throw new RuntimeException("Multiple outputs not supported!");
            }
            outputs.put(productionStep.getResultPerSecond().get(0).getItem().getName(), xOffset - 2);
        }

        blueprint.addBlueprint(assemblyMachines, 0, 0);

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

                    Blueprint bs = new Blueprint();
                    if (goingRight) {
                        bs.addBuilding(new Splitter(outputX, 0, DOWN));
                        bs.addBuilding(new YellowBelt(outputX, 1, DOWN));
                        if (!comingFromRight) {
                            placeBeltFromTo(bs, outputX + 1, 1, productionIndex - outputX - 1, RIGHT);
                            bs.addBuilding(new YellowBelt(productionIndex, 0, UP));
                            bs.addBuilding(new YellowBelt(productionIndex, 1, UP));
                            bs.addBuilding(new YellowBelt(productionIndex, 2, UP));
                        } else {
                            placeBeltFromTo(bs, outputX + 1, 1, productionIndex - outputX - 2, RIGHT);
                            bs.addBuilding(new UndergroundBelt(productionIndex - 1, 1, RIGHT, true));
                            bs.addBuilding(new UndergroundBelt(productionIndex + 1, 1, RIGHT, false));
                            bs.addBuilding(new YellowBelt(productionIndex + 2, 1, UP));
                            bs.addBuilding(new YellowBelt(productionIndex + 2, 0, LEFT));
                            bs.addBuilding(new YellowBelt(productionIndex + 1, 0, LEFT));

                            bs.addBuilding(new YellowBelt(productionIndex, 0, UP));
                            bs.addBuilding(new YellowBelt(productionIndex, 1, UP));
                        }
                    } else {
                        bs.addBuilding(new Splitter(outputX - 1, 0, DOWN));
                        bs.addBuilding(new YellowBelt(outputX, 1, DOWN));
                        if (comingFromRight) {
                            placeBeltFromTo(bs, outputX - 1, 1, outputX - productionIndex - 1, LEFT);
                            bs.addBuilding(new YellowBelt(productionIndex, 0, UP));
                            bs.addBuilding(new YellowBelt(productionIndex, 1, UP));
                            bs.addBuilding(new YellowBelt(productionIndex, 2, UP));
                        } else {
                            placeBeltFromTo(bs, outputX - 1, 1, outputX - productionIndex - 2, LEFT);
                            bs.addBuilding(new UndergroundBelt(productionIndex + 1, 1, LEFT, true));
                            bs.addBuilding(new UndergroundBelt(productionIndex - 1, 1, LEFT, false));
                            bs.addBuilding(new YellowBelt(productionIndex - 2, 1, UP));
                            bs.addBuilding(new YellowBelt(productionIndex - 2, 0, RIGHT));
                            bs.addBuilding(new YellowBelt(productionIndex - 1, 0, RIGHT));

                            bs.addBuilding(new YellowBelt(productionIndex, 0, UP));
                            bs.addBuilding(new YellowBelt(productionIndex, 1, UP));
                        }
                    }

                    int yOffset = findYPlaceForSection(blueprint, bs);
                    blueprint.addBlueprint(bs, 0, yOffset);
                    placeBeltFromTo(blueprint, productionIndex, yOffset, yOffset, UP);
                    placeBeltFromTo(blueprint, outputX, 0, yOffset, DOWN);
                }
            }
        }

        return blueprint;
    }

    private static void placeBeltFromTo(Blueprint bp, int startX, int startY, int length, int direction) {
        Integer previousX = null;
        Integer previousY = null;
        boolean isUnderground = false;
        for (int offset = 0; offset < length; offset++) {
            int actualX = startX;
            int actualY = startY;

            switch (direction) {
                case UP:
                    actualY -= offset;
                    break;
                case DOWN:
                    actualY += offset;
                    break;
                case LEFT:
                    actualX -= offset;
                    break;
                case RIGHT:
                    actualX += offset;
                    break;
            }

            Building belt = isUnderground
                    ? new UndergroundBelt(actualX, actualY, direction, false)
                    : new YellowBelt(actualX, actualY, direction);
            Building occupation = bp.get(belt.getX(), belt.getY());
            if (occupation != null) {
                if (!isUnderground && goingSameDirection(belt, occupation)) {
                    if (previousX != null) { //TODO
                        bp.remove(previousX, previousY);
                        bp.addBuilding(new UndergroundBelt(previousX, previousY, direction, true));
                    } else {
                        System.err.println("Can't start placing belt");
                    }
                    isUnderground = true;
                }
            } else {
                bp.addBuilding(belt);
                isUnderground = false;
            }
            previousX = actualX;
            previousY = actualY;
        }

        if (isUnderground) {
//            throw new RuntimeException("Couldn't fit belt");
            System.err.println("Couldn't fit belt");
        }
    }

    private static boolean goingSameDirection(Building beltToPlace, Building beltOccup) {
        int occupDirection;

        if (beltOccup instanceof YellowBelt) {
            occupDirection = ((YellowBelt) beltOccup).getDirection();
        } else if (beltOccup instanceof UndergroundBelt) {
            if (((UndergroundBelt) beltOccup).isOutput()) {
                return false;
            }
            occupDirection = ((UndergroundBelt) beltOccup).getDirection();
        } else {
            return false;
        }

        int placeDirection;
        if (beltToPlace instanceof YellowBelt) {
            placeDirection = ((YellowBelt) beltToPlace).getDirection();
        } else if (beltToPlace instanceof UndergroundBelt) {
            placeDirection = ((UndergroundBelt) beltToPlace).getDirection();
        } else {
            return false;
        }

        return occupDirection == placeDirection;
    }

    private static int findYPlaceForSection(Blueprint base, Blueprint sub) {
        int y = 0;
        while (!base.canPlace(sub, 0, y)) {
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

    private static Blueprint generateSubsectionFor(ProductionStep productionStep) {
        Blueprint subsection = new Blueprint();

        int assemblingMachinesRequired = (int) Math.ceil(productionStep.getCraftingSpeed() / AssemblingMachine2.getCraftingSpeed());
        int inputBelts = (int) Math.ceil(productionStep.getIngredientsPerSecond().size() / 2.0);
        if (inputBelts > 2) {
            throw new RuntimeException("Not yet implemented!");
        }
        for (int i = 0; i < assemblingMachinesRequired; i++) {
            for (int y = -2; y <= -2; y++) { //TODO was y <= 0
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
