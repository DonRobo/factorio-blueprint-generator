package com.donrobo.fpbg.planner;

import com.donrobo.fpbg.data.Item;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class ProductionLine {

    private final List<String> inputMaterials = new ArrayList<>();
    private final List<ProductionStep> productionSteps = new ArrayList<>();

    public void addProductionStep(ProductionStep productionStep) {
        if (isUsingOnlyInputMaterials(productionStep)) {
            productionSteps.add(0, productionStep);
        } else {
            FractionalItemStack resultsPerSecond = productionStep.getResultPerSecond();
            String result = resultsPerSecond.getItem().getName();
            int index = 0;
            while (index < productionSteps.size() && !isRequiredBy(result, productionSteps.get(index))) {
                index++;
            }
            productionSteps.add(index, productionStep);
        }
    }

    private boolean isRequiredBy(String item, ProductionStep productionStep) {
        return productionStep.getIngredientsPerSecond().stream().map(i -> i.getItem().getName()).anyMatch(ingredient -> ingredient.equals(item));
    }

    private boolean isUsingOnlyInputMaterials(ProductionStep productionStep) {
        return productionStep.getIngredientsPerSecond().stream().map(i -> i.getItem().getName()).allMatch(inputMaterials::contains);
    }


    public void addInputMaterials(List<Item> inputMaterials) {
        inputMaterials.stream().map(Item::getName).forEach(this.inputMaterials::add);
    }

    public void clearUnusedInputMaterials() {
        List<String> usedInputMaterials = inputMaterials.stream().filter(mat ->
                productionSteps.stream().anyMatch(ps -> isRequiredBy(mat, ps))
        ).collect(Collectors.toList());

        inputMaterials.clear();
        inputMaterials.addAll(usedInputMaterials);
    }

    public List<String> getAllIngredients() {
        List<String> allIngredients = new ArrayList<>();

        for (ProductionStep ps : productionSteps) {
            ps.getIngredientsPerSecond().stream().map(i -> i.getItem().getName()).forEach(allIngredients::add);
        }

        return allIngredients.stream().distinct().collect(Collectors.toList());
    }

    public List<ProductionStep> getProductionStepsThatRequire(String input) {
        return productionSteps.stream().filter(ps -> ps.requires(input)).collect(Collectors.toList());
    }
}
