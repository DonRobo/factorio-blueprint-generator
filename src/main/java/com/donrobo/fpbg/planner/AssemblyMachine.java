package com.donrobo.fpbg.planner;

public enum AssemblyMachine {
    ASSEMBLY_MACHINE_1(0.5),
    ASSEMBLY_MACHINE_2(0.75),
    ASSEMBLY_MACHINE_3(1.25);

    private final double craftingSpeed;

    AssemblyMachine(double craftingSpeed) {
        this.craftingSpeed = craftingSpeed;
    }

    public double getCraftingSpeed() {
        return craftingSpeed;
    }
}
