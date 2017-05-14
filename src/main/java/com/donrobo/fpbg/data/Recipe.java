package com.donrobo.fpbg.data;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.List;

@Data
@EqualsAndHashCode
public class Recipe {

    private static final double DEFAULT_RECIPE_TIME = 0.5;

    private final String name;
    private final Boolean enabled;
    private final List<ItemStack> ingredients;
    private final List<ItemStack> result;
    private final Double energyRequired;
    private final HashMap<String, Object> extra;

    public Recipe(String name, Boolean enabled, List<ItemStack> ingredients, List<ItemStack> result, Double energyRequired, HashMap<String, Object> extra) {
        this.name = name;
        this.enabled = enabled;
        this.ingredients = ingredients;
        this.result = result;
        this.energyRequired = (energyRequired == null || Math.abs(energyRequired) < 0.001) ? DEFAULT_RECIPE_TIME : energyRequired;
        this.extra = extra;

        this.extra.remove("name");
        this.extra.remove("enabled");
        this.extra.remove("ingredients");
        this.extra.remove("result");
        this.extra.remove("results");
        this.extra.remove("result_count");
        this.extra.remove("energy_required");
        this.extra.remove("type");
        this.extra.remove("normal");
        this.extra.remove("expensive");
    }

    public boolean isProducingItem(Item item) {
        return result.stream().anyMatch(i -> i.getItem().equals(item));
    }

    public boolean isProducingItem(String item) {
        return result.stream().anyMatch(i -> i.getItem().getName().equals(item));
    }
}
