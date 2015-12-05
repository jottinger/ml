package com.enigmastation.ml.model;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "neurons")
@Cacheable
@NamedQueries(
        {
                @NamedQuery(name = "neuron.getNeuronId",
                        query = "select n.id from Neuron n where n.key=:key",
                        lockMode = LockModeType.READ)
        }
)
public class Neuron implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @NaturalId
    @Column(nullable = false, unique = true, length = 4096)
    String key;

    public Neuron(String key) {
        this.key = key;
    }

    public Neuron() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Neuron)) return false;
        Neuron neuron = (Neuron) o;
        return Objects.equal(getId(), neuron.getId()) &&
                Objects.equal(getKey(), neuron.getKey());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId(), getKey());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("key", key)
                .toString();
    }
}
