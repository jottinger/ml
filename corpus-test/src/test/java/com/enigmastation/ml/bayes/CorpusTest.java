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

package com.enigmastation.ml.bayes;

import com.enigmastation.ml.bayes.impl.FisherClassifierImpl;
import com.ice.tar.TarArchive;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.testng.annotations.Test;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CorpusTest {
    int hits;
    int misses;
    int tests;

    @Test(groups = {"fulltest"})
    public void testCorpus() throws URISyntaxException, IOException, InterruptedException {
        final Classifier classifier = new FisherClassifierImpl();
        ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        // first we expand the test dataset
        URL resource = this.getClass().getResource("/src/test/resources/publiccorpus");
        File resourceFile = new File(resource.toURI());
        String[] dataFileNames = resourceFile.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".bz2");
            }
        });

        List<String> directories = new ArrayList<String>();
        final List<File> trainingFiles = new ArrayList<>();

        for (String fileName : dataFileNames) {
            directories.add(expandFile(fileName));
        }
        // collect every name, plus mark to delete on exit
        for (String inputDirectory : directories) {
            URL url = this.getClass().getResource(inputDirectory);
            File[] dataFiles = new File(url.toURI()).listFiles();
            for (File f : dataFiles) {
                handleFiles(f, trainingFiles);
            }
        }
        long startTime = System.currentTimeMillis();
        final int[] counter = {0};
        final int[] marker = {0};
        // now let's walk through a training cycle
        for (final File file : trainingFiles) {
            service.submit(new Runnable() {
                @Override
                public void run() {
                    if ((++marker[0]) % 100 == 0) {
                        System.out.println("Progress training: " + marker[0] + " of " + trainingFiles.size());
                    }
                    if (counter[0] > 2) {
                        try {
                            trainWith(classifier, file);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    counter[0] = (counter[0] + 1) % 10;
                }
            });
        }
        service.shutdown();
        service.awaitTermination(2, TimeUnit.HOURS);
        service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        long endTime = System.currentTimeMillis();
        System.out.printf("Training took %d ms%n", (endTime - startTime));
        startTime = System.currentTimeMillis();
        marker[0] = 0;
        // now test against the training data
        for (final File file : trainingFiles) {
            service.submit(new Runnable() {
                public void run() {
                    if ((++marker[0]) % 100 == 0) {
                        System.out.println("Progress evaluating: " + marker[0] + " of " + trainingFiles.size());
                    }
                    if (counter[0] < 3) {
                        try {
                            classify(classifier, file);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    counter[0] = (counter[0] + 1) % 10;
                }
            });
        }
        service.shutdown();
        service.awaitTermination(2, TimeUnit.HOURS);
        endTime = System.currentTimeMillis();
        System.out.printf("Training accuracy: %d tests, %f%% accuracy%n", tests,
                (hits * 100.0) / tests);
        System.out.printf("Training took %d ms%n", (endTime - startTime));
    }

    private void handleFiles(File f, List<File> trainingFiles) {
        if (f.isDirectory()) {
            File[] directory = f.listFiles();
            for (File d : directory) {
                handleFiles(d, trainingFiles);
            }
        } else {
            trainingFiles.add(f);
        }
        f.deleteOnExit();
    }

    private void classify(Classifier classifier, File file) throws IOException {
        String data = readFile(file);
        if (classifier.classify(data).equals(file.getName().contains("ham") ? "ham" : "spam")) {
            hits++;
        } else {
            misses++;
        }
        tests++;
    }

    private void trainWith(Classifier classifier, File file) throws IOException {
        String data = readFile(file);
        classifier.train(data, file.getName().contains("ham") ? "ham" : "spam");

    }

    private String readFile(File file) throws IOException {
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr, 16384);
        StringBuilder sb = new StringBuilder();
        String s;
        while ((s = br.readLine()) != null) {
            sb.append(s);
        }
        br.close();
        fr.close();
        return sb.toString();
    }

    private String expandFile(String fileName)
            throws URISyntaxException, IOException {
        URL dataResource = this.getClass().getResource("/src/test/resources/publiccorpus/" + fileName);
        File inputFile = new File(dataResource.toURI());
        File outputDirectory = new File(inputFile + ".data");
        outputDirectory.mkdir();

        FileInputStream fis = new FileInputStream(inputFile);
        BufferedInputStream bis = new BufferedInputStream(fis);

        BZip2CompressorInputStream bzip2 = new BZip2CompressorInputStream(bis);
        TarArchive archive = new TarArchive(bzip2);
        archive.extractContents(outputDirectory);
        System.out.printf("%s finished expanding into \"%s\".%n",
                inputFile.getName(), outputDirectory.toString());
        bzip2.close();

        return "/src/test/resources/publiccorpus/" + inputFile.getName() + ".data";
    }
}
