package fingerprint_recognition;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Driver {

    public static void main(String[] argv) {

        // Read command line arguments
        ParamsManager manager = new ParamsManager();
        if(!manager.parseArguments(argv)) {
            return;
        }

        // -------------------
        // Graphics processing
        // -------------------
        System.out.println("-----Starting graphics processing...-----\n");

        // Process training set's graphics
        System.out.println("---Processing training set...---\n");
        GraphicsProcessor graphicsProcessor = new GraphicsProcessor();
        List<DataSet> dataSets = graphicsProcessor.processBatch(
                manager.getTrainingSourceImagesPath(), manager.getTrainingProcessedImagesPath(),
                manager.getFirstTrainingImageIndex(), manager.getTrainingImageCount(),
                manager.getWindowWidth(), manager.getWindowHeight());
        List<TrainingSet> trainingSets = new ArrayList<>();
        for(DataSet dataSet : dataSets) {
            TrainingSet trainingSet = new TrainingSet(dataSet);
            trainingSets.add(trainingSet);
        }

        // Set target output values for the training set
        for(int i = 0; i < manager.getTrainingImageCount(); i += 2) {
            trainingSets.get(i).setTargets(new int[]{1});
        }

        // Process testing set's graphics
        System.out.println("---Processing testing set...---\n");
        List<DataSet> testingSets = graphicsProcessor.processBatch
                (manager.getTestingSourceImagesPath(), manager.getTestingProcessedImagesPath(),
                        manager.getFirstTestingImageIndex(), manager.getTestingImageCount(),
                        manager.getWindowWidth(), manager.getWindowHeight());


        // --------------------
        // Neural network stuff
        // --------------------
        System.out.println("-----Starting neural network's tasks...-----\n");

        // Create the perceptron
        int layerSize = trainingSets.get(0).getData().size() + 1;
        System.out.println("Creating the perceptron...");
        Perceptron perceptron = new Perceptron(layerSize, layerSize,1);
        System.out.println("Perceptron created!");

        // Train the network
        System.out.println("Beginning network training...");
        perceptron.train(trainingSets, manager.getErrorThreshold());

        // Test the network
        System.out.println("Testing the network using a testing set...");
        int searchedPatternIndex = perceptron.test(testingSets, new double[] {1});
        System.out.println("Best match found at index " + searchedPatternIndex + "!");
    }
}