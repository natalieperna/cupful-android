package com.natalieperna.cupful.models;

import org.jscience.physics.amount.Amount;

import javax.measure.quantity.VolumetricDensity;

public class Ingredient {
    private final String name;
    private final Amount<VolumetricDensity> density;

    public Ingredient(String name, double baseDensity) {
        this.name = name;
        this.density = Amount.valueOf(baseDensity, Kitchen.G_PER_CUP);
    }

    public Amount<VolumetricDensity> getDensity() {
        return density;
    }

    @Override
    public String toString() {
        return name;
    }
}
