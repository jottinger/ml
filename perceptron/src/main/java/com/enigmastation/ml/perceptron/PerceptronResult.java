/*
 Copyright 2012 Joseph B. Ottinger

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.enigmastation.ml.perceptron;

public final class PerceptronResult implements Comparable<PerceptronResult> {
    Object target;
    double strength;

    public PerceptronResult(Object target, Double strength) {
        this.target = target;
        this.strength = strength;
    }

    public Object getTarget() {
        return target;
    }

    public double getStrength() {
        return strength;
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
