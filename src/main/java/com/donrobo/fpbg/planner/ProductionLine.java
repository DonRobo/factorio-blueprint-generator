package com.donrobo.fpbg.planner;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ProductionLine {

    private final List<ProductionStep> productionSteps;


}
