package com.donrobo.fpbg.data;

import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Data
public class Recipe {

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
        this.energyRequired = energyRequired;
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
}
