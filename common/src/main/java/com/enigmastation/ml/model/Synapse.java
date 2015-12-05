package com.enigmastation.ml.model;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "synapses",
        indexes = {
                @Index(name = "synapse_fk", columnList = "input,output,layer", unique = true)
        })
@NamedQueries(
        {
                @NamedQuery(name = "synapse.getStrength",
                        query = "select s.strength from Synapse s where s.input=:input and s.output=:output and s.layer=:layer",
                        lockMode = LockModeType.READ),
                @NamedQuery(name = "synapse.findSynapse",
                        query = "from Synapse s  where s.input=:input and s.output=:output and s.layer=:layer"),
        }
)
@Cacheable
public class Synapse implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @Column(nullable = false)
    long input;
    @Column(nullable = false)
    Long output;
    @Column(nullable = false, name = "layer")
    Layer layer;
    @Column(nullable = false)
    double strength;

    public Synapse() {
    }

    public Synapse(Long input, Long output, Layer layer, double strength) {
        this.input = input;
        this.output = output;
        this.layer = layer;
        this.strength = strength;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInput() {
        return input;
    }

    public void setInput(Long input) {
        this.input = input;
    }

    public Long getOutput() {
        return output;
    }

    public void setOutput(Long output) {
        this.output = output;
    }

    public Layer getLayer() {
        return layer;
    }

    public void setLayer(Layer layer) {
        this.layer = layer;
    }

    public double getStrength() {
        return strength;
    }

    public void setStrength(double strength) {
        this.strength = strength;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("input", input)
                .add("output", output)
                .add("layer", layer)
                .add("strength", strength)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Synapse)) return false;
        Synapse synapse = (Synapse) o;
        return Double.compare(synapse.getStrength(), getStrength()) == 0 &&
                Objects.equal(getId(), synapse.getId()) &&
                Objects.equal(getInput(), synapse.getInput()) &&
                Objects.equal(getOutput(), synapse.getOutput()) &&
                getLayer() == synapse.getLayer();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId(), getInput(), getOutput(), getLayer(), getStrength());
    }
}
