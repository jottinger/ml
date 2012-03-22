package com.enigmastation.ml.perceptron;

public class PerceptronResult implements Comparable<PerceptronResult> {
    Object target;
    double strength;

    public PerceptronResult(Object target, Double strength) {
        this.target = target;
        this.strength = strength;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("PerceptronResult");
        sb.append("{target=").append(target);
        sb.append(", strength=").append(strength);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PerceptronResult that = (PerceptronResult) o;

        if (Double.compare(that.strength, strength) != 0) return false;
        if (target != null ? !target.equals(that.target) : that.target != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = target != null ? target.hashCode() : 0;
        temp = strength != +0.0d ? Double.doubleToLongBits(strength) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    /**
     * returns a result that yields a high value prioritized over a low value
     *
     * @param o a compared perceptron result
     * @return a comparable value (-1,0,1)
     */
    @Override
    public int compareTo(PerceptronResult o) {
        return -Double.compare(strength, o.strength);
    }
}
