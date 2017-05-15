package com.donrobo.fpbg.blueprint;

import com.donrobo.fpbg.blueprint.building.*;
import com.donrobo.fpbg.planner.ProductionLine;
import com.donrobo.fpbg.planner.ProductionStep;
import com.donrobo.fpbg.util.MultipleEntryMap;

import java.util.*;
import java.util.stream.Collectors;

import static com.donrobo.fpbg.blueprint.Direction.*;

public class BlueprintGenerator {

    @SuppressWarnings("Duplicates")
    public static Blueprint generateBlueprint(ProductionLine pl) {
        Blueprint blueprint = new Blueprint();

        Map<String, Integer> outputs = new HashMap<>();
        Map<String, Integer> productionStepIndexes = new HashMap<>();
        MultipleEntryMap<String, Integer> inputIndexes = new MultipleEntryMap<>();
        Blueprint assemblyMachines = new Blueprint();
        int xOffset = 0;
        for (ProductionStep productionStep : pl.getProductionSteps()) {
            Blueprint bs = generateSubsectionFor(productionStep);
            assemblyMachines.addBlueprint(bs, xOffset - bs.getMinimumX(), -bs.getMaximumY());
            productionStepIndexes.put(productionStep.getRecipe().getName(), xOffset);

            int count = 0;
            int finalXOffset = xOffset;
            productionStep.getIngredientsPerSecond().stream().map(i -> i.getItem().getName()).forEach(ingredient -> {
                inputIndexes.put(ingredient, finalXOffset + count / 2);
            });

            xOffset = bs.getMaximumX() + xOffset - bs.getMinimumX() + 3;
            if (productionStep.getResultPerSecond().size() != 1) {
                throw new RuntimeException("Multiple outputs not supported!");
            }
            outputs.put(productionStep.getResultPerSecond().get(0).getItem().getName(), xOffset - 3);
        }

        blueprint.addBlueprint(assemblyMachines, 0, 0);

        Set<String> rawInputs = new HashSet<>(pl.getInputMaterials());

        List<String> inputs = pl.getAllIngredients();

        for (String input : inputs) {
            Blueprint belts = new Blueprint();
            boolean isRaw = rawInputs.contains(input);

            int startX = isRaw ? -1 : outputs.get(input);
            int endX = inputIndexes.get(input).stream().mapToInt(i -> i).max().orElse(0);

            if (!isRaw) {
                belts.addBuilding(new UndergroundBelt(startX, -1, DOWN, false));
            }
            placeBeltFromTo(belts, startX, 0, endX - startX, RIGHT);

            List<ProductionStep> productionSteps = pl.getProductionStepsThatRequire(input);
            List<Integer> endings = inputIndexes.get(input);
            for (int i = 0; i < endings.size(); i++) {
                List<String> ingredients = productionSteps.get(i).getIngredientsPerSecond().stream().map(ing -> ing.getItem().getName()).collect(Collectors.toList());
                int ingIndex = ingredients.indexOf(input);

                int ending = endings.get(i);
                boolean last = i == (endings.size() - 1);

                if (ingIndex % 2 == 0 && ingIndex == (ingredients.size() - 1)) {
                    ending += ingIndex / 2;
                    if (!last) {
                        belts.remove(ending - 1, 0);
                        belts.addBuilding(new Splitter(ending - 1, -1, RIGHT));
                        belts.addBuilding(new YellowBelt(ending, -1, UP));
                        belts.addBuilding(new UndergroundBelt(ending, -2, UP, true));
                    } else {
                        belts.addBuilding(new YellowBelt(ending, 0, UP));
                        belts.addBuilding(new UndergroundBelt(ending, -1, UP, true));
                    }
                } else if (ingIndex % 2 == 0) {
                    int offset;
                    if (!last) {
                        belts.remove(ending - 2, 0);
                        belts.addBuilding(new Splitter(ending - 2, -1, RIGHT));
                        offset = 1;
                    } else {
                        belts.remove(ending - 1, 0);
                        offset = 0;
                    }
                    belts.addBuilding(new YellowBelt(ending - 1, 0 - offset, UP));
                    belts.addBuilding(new YellowBelt(ending - 1, -1 - offset, RIGHT));
                    belts.addBuilding(new UndergroundBelt(ending, 0 - offset, UP, false));
                    belts.addBuilding(new YellowBelt(ending, -1 - offset, UP));
                    belts.addBuilding(new UndergroundBelt(ending, -2 - offset, UP, true));

                } else {
                    int offset;
                    if (!last) {
                        belts.remove(ending - 2, 0);
                        belts.addBuilding(new Splitter(ending - 2, -1, RIGHT));
                        offset = 1;
                    } else {
                        belts.remove(ending - 1, 0);
                        offset = 0;
                    }

                    belts.addBuilding(new UndergroundBelt(ending - 1, 0 - offset, RIGHT, true));
                    belts.addBuilding(new UndergroundBelt(ending + 1, 0 - offset, RIGHT, false));
                    belts.addBuilding(new YellowBelt(ending + 2, 0 - offset, UP));
                    belts.addBuilding(new YellowBelt(ending + 2, -1 - offset, LEFT));
                    belts.addBuilding(new YellowBelt(ending + 1, -1 - offset, LEFT));
                    belts.addBuilding(new YellowBelt(ending, -1 - offset, UP));
                    belts.addBuilding(new UndergroundBelt(ending, -2 - offset, UP, true));
                    belts.addBuilding(new UndergroundBelt(ending, 0 - offset, UP, false));
                }
            }

            int yPlacement = findYPlaceForSection(blueprint, belts, 2 - belts.getMinimumY());
            blueprint.addBlueprint(belts, 0, yPlacement);
        }

        fixUndergroundBelts(blueprint);

        return blueprint;
    }

    private static void fixUndergroundBelts(Blueprint blueprint) {
        //TODO replace orphans with belts
        //TODO replace by belts where possible
        //TODO fix too long underground belts
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

    private static int findYPlaceForSection(Blueprint base, Blueprint sub, int startY) {
        int y = startY;
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
            for (int y = -2; y <= 0; y++) { //TODO was y <= 0
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

        for (int beltX = -inputBelts + 1; beltX <= 0; beltX++) {
            subsection.addBuilding(new UndergroundBelt(beltX, 1, UP, false));
        }
        subsection.addBuilding(new UndergroundBelt(6, 1, DOWN, true));

        return subsection;
    }

}
