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

import com.enigmastation.ml.perceptron.impl.HSQLDBPerceptronRepository;
import com.enigmastation.ml.perceptron.impl.PerceptronImpl;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class PerceptronTest {
    HSQLDBPerceptronRepository repo = new HSQLDBPerceptronRepository();

    @BeforeTest
    public void clear() {
        System.out.println("Clearing");
        repo.clear();
    }

    @Test
    public void testFeedForward() {
        PerceptronImpl perceptron = new PerceptronImpl(repo);
        List<String> corpus = new ArrayList<>();
        corpus.add("world");
        corpus.add("bank");
        List<Object> targets = new ArrayList<>();
        targets.add("worldbank");
        targets.add("river");
        targets.add("earth");
        PerceptronState state = perceptron.buildPerceptron(corpus, targets);
        System.out.println(perceptron.feedForward(state));
    }


    @DataProvider
    Object[][] getTokenData() {
        return new Object[][]{
                new Object[]{"foo", PerceptronRepository.Layer.HIDDEN, 0, "creation of foo"},
                new Object[]{"foo", PerceptronRepository.Layer.HIDDEN, 0, "verification of first read of foo"},
                new Object[]{"bar", PerceptronRepository.Layer.HIDDEN, 1, "creation of bar"},
                new Object[]{"foo", PerceptronRepository.Layer.HIDDEN, 0, "read of foo after creation of bar"},
                new Object[]{"bar", PerceptronRepository.Layer.HIDDEN, 1, "read of bar"},
                new Object[]{"foo", PerceptronRepository.Layer.TO, 2, "creation of foo in different layer"},
                new Object[]{"foo", PerceptronRepository.Layer.HIDDEN, 0, "read of foo after creation of foo in different layer"},
                new Object[]{"foo", PerceptronRepository.Layer.TO, 2, "read of foo after creation of foo in different layer"},
        };
    }

    @Test(dataProvider = "getTokenData")
    public void createRepository(String token, PerceptronRepository.Layer layer, int expectedResult, String message) {
        assertEquals(repo.getNodeId(token, layer, PerceptronRepository.NodeCreation.CREATE),
                expectedResult, message);
    }

    @Test(dependsOnMethods = "createRepository")
    public void createHiddenNodes() {
        List<Object> words = new ArrayList<>();
        words.add("world");
        words.add("bank");
        List<Object> targets = new ArrayList<>();
        targets.add("worldbank");
        targets.add("river");
        targets.add("earth");
        repo.generateHiddenNodes(words, targets);
    }

    @Test
    public void testTrain() {
        Perceptron perceptron = new PerceptronImpl(repo);
        System.out.println(perceptron.getResults(Arrays.asList(new Object[]{"world", "bank"}),
                Arrays.asList(new Object[]{"worldbank", "river", "earth"}))
        );
        perceptron.train(Arrays.asList(new Object[]{"world", "bank"}),
                Arrays.asList(new Object[]{"worldbank", "river", "earth"}),
                "worldbank");
        System.out.println(perceptron.getResults(Arrays.asList(new Object[]{"world", "bank"}),
                Arrays.asList(new Object[]{"worldbank", "river", "earth"}))
        );
    }

    @Test
    public void testUnseenResult() {
        Object[] allTargets = new Object[]{"worldbank", "river", "earth"};
        Perceptron perceptron = new PerceptronImpl(repo);
        //noinspection UnusedDeclaration
        for (int i : range(0, 30)) {
            perceptron.train(Arrays.asList(new Object[]{"world", "bank"}),
                    Arrays.asList(allTargets), "worldbank");
            perceptron.train(Arrays.asList(new Object[]{"river", "bank"}),
                    Arrays.asList(allTargets), "river");
            perceptron.train(Arrays.asList(new Object[]{"world"}),
                    Arrays.asList(allTargets), "earth");
        }
        System.out.println(perceptron.getResults(
                Arrays.asList(new Object[]{"world", "bank"}),
                Arrays.asList(allTargets)));
        System.out.println(perceptron.getResults(
                Arrays.asList(new Object[]{"river", "bank"}),
                Arrays.asList(allTargets)));
        System.out.println(perceptron.getResults(
                Arrays.asList(new Object[]{"bank"}),
                Arrays.asList(allTargets)));

    }

    @Test
    public void testNAND() {
        List<Object> targets = Arrays.asList(new Object[]{"true", "false"});
        String[][] trainingSet = new String[][]{
                new String[]{"1true nand 2true", "false"},
                new String[]{"1true nand 2false", "true"},
                new String[]{"1false nand 2true", "true"},
        };
        Perceptron perceptron = new PerceptronImpl(repo);
        //noinspection UnusedDeclaration
        for (int i : range(0, 30)) {
            for (String[] data : trainingSet) {
                Object[] inputs = data[0].split(" ");
                perceptron.train(Arrays.asList(inputs), targets, data[1]);
            }
        }
        System.out.println(perceptron.getResults(
                Arrays.asList(new Object[]{"1false", "nand", "2false"}), targets));
    }


    @Test
    public void testCleanerAPI() {
        String[][] trainingSet = new String[][]{
                new String[]{"1true nand 2true", "false"},
                new String[]{"1true nand 2false", "true"},
                new String[]{"1false nand 2true", "true"},
        };
        Perceptron perceptron = new PerceptronImpl(repo);
        perceptron.createTarget("true");
        perceptron.createTarget("false");

        //noinspection UnusedDeclaration
        for (int i : range(0, 30)) {
            for (String[] data : trainingSet) {
                perceptron.train(data[0], data[1]);
            }
        }
        assertEquals(perceptron.getFirstResult("1false nand 2false"), "true");

    }

    private List<Integer> range(Integer start, Integer end) {
        List<Integer> list = new ArrayList<>();
        int value = start;
        int delta = (start > end) ? -1 : 1;
        while (value != (end + delta)) {
            list.add(value);
            value += delta;
        }
        return list;
    }
}
