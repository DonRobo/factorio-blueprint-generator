package com.donrobo.fpbg.planner;

import com.donrobo.fpbg.data.Recipe;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class ProductionStep {

    private final Recipe recipe;
    private final AssemblyMachine assemblyMachine;
    private final int count;

    private double getCraftingSpeed() {
        return assemblyMachine.getCraftingSpeed() * count;
    }

    public List<FractionalItemStack> getResultPerSecond() {
        return recipe.getResult().stream().map(is -> new FractionalItemStack((is.getCount() * getCraftingSpeed()) / recipe.getEnergyRequired(), is.getItem())).collect(Collectors.toList());
    }

    public List<FractionalItemStack> getIngredientsPerSecond() {
        return recipe.getIngredients().stream().map(is -> new FractionalItemStack((is.getCount() * getCraftingSpeed()) / recipe.getEnergyRequired(), is.getItem())).collect(Collectors.toList());
    }
}
