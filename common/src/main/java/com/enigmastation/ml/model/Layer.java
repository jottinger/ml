package com.enigmastation.ml.model;

import com.google.common.base.MoreObjects;

public enum Layer {
    HIDDEN(-0.2),
    OUTPUT(0);

    double strength;

    Layer(double strength) {
        this.strength = strength;
    }

    public double getStrength() {
        return strength;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", this.name())
                .add("strength", strength)
                .toString();
    }
}
