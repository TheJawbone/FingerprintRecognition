package fingerprint_recognition;

import java.util.ArrayList;
import java.util.List;

/**
 * Driver class is responsible for running the application. It implements the main method, which calls all the
 * necessary operations.
 */
public class Driver {

    /**
     * Main method of the application.
     * @param argv Command line arguments.
     */
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

        // Check initial time (used for time measuring later on
        long stopwatch = System.nanoTime();

        // Process training set's graphics
        System.out.println("---Processing training set...---\n");
        GraphicsProcessor graphicsProcessor = new GraphicsProcessor();
        List<Data> dataSets = graphicsProcessor.processBatch(
                manager.getTrainingSourceImagesPath(), manager.getTrainingProcessedImagesPath(),
                manager.getFirstTrainingImageIndex(), manager.getTrainingImageCount(),
                manager.getWindowWidth(), manager.getWindowHeight(), manager.getOverlapFactor());
        List<TrainingData> trainingSets = new ArrayList<>();
        for(Data dataSet : dataSets) {
            TrainingData trainingSet = new TrainingData(dataSet);
            trainingSets.add(trainingSet);
        }

        // Set target output values for the training set
        for(int i = 0; i < manager.getTrainingImageCount(); i += 2) {
            trainingSets.get(i).setTargets(new int[]{1});
        }

        // Process testing set's graphics
        System.out.println("---Processing testing set...---\n");
        List<Data> testingSets = graphicsProcessor.processBatch
                (manager.getTestingSourceImagesPath(), manager.getTestingProcessedImagesPath(),
                        manager.getFirstTestingImageIndex(), manager.getTestingImageCount(),
                        manager.getWindowWidth(), manager.getWindowHeight(), manager.getOverlapFactor());

        // Set graphics processed checkpoint
        long graphicsProcessedCheckpoint = System.nanoTime();

        // --------------------
        // Neural network stuff
        // --------------------
        System.out.println("-----Starting neural network's tasks...-----\n");

        // Create the perceptron
        int layerSize = trainingSets.get(0).getData().size() + 1;
        System.out.println("Creating the perceptron...");
        Perceptron perceptron = new Perceptron(layerSize, layerSize,1);
        System.out.println("Perceptron created!");

        // Set perceptron created checkpoint
        long perceptronCreatedCheckpoint = System.nanoTime();

        // Train the network
        System.out.println("Beginning network training...");
        int epochCounter = perceptron.train(trainingSets, manager.getErrorThreshold());
        System.out.println("Network successfully trained after " + epochCounter
                + " epochs at error threshold of " + manager.getErrorThreshold() + "!");

        // Set network trained checkpoint
        long networkTrainedCheckpoint = System.nanoTime();

        // Test the network
        System.out.println("Testing the network using a testing set...");
        int searchedPatternIndex = perceptron.test(testingSets, new double[] {1});
        System.out.println("Best match found at index " + searchedPatternIndex + "!");

        // Set network tested checkpoint
        long networkTestedCheckpoint = System.nanoTime();

        // Calculate and display times
        long graphicsProcessingTime = Math.round((graphicsProcessedCheckpoint - stopwatch) / 1000000);
        long perceptronCreationTime = Math.round((perceptronCreatedCheckpoint - graphicsProcessedCheckpoint) / 1000000);
        long networkTrainingTime = Math.round((networkTrainedCheckpoint - perceptronCreatedCheckpoint) / 1000000);
        long networkTestingTime = Math.round((networkTestedCheckpoint - networkTrainedCheckpoint) / 1000000);
        System.out.println("Graphics processed in:\t" + graphicsProcessingTime + "ms");
        System.out.println("Perceptron created in:\t" + perceptronCreationTime + "ms");
        System.out.println("Network trained in:\t\t" + networkTrainingTime + "ms");
        System.out.println("Network tested in:\t\t" + networkTestingTime + "ms");
    }
}