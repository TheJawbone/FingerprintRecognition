package fingerprint_recognition;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainClass {

    public static void main(String[] argv) {

        // -------------------
        // Graphics processing
        // -------------------

        System.out.println("-----Starting graphics processing...-----\n");
        BufferedImage image = null;
        GraphicsProcessor graphicsProcessor;
        List<TrainingSet> trainingSets = new ArrayList<>();
        for(int i = 1; i <= 10; i++) {
            try {
                System.out.println("Opening image \"" + i + ".bmp\"...");
                image = ImageIO.read(new File("images\\source\\" + i + ".bmp"));
            } catch (IOException e) {
                System.out.println(e.getStackTrace());
            }
            System.out.println("Processing image...");
            graphicsProcessor = new GraphicsProcessor(image);
            trainingSets.add(new TrainingSet(graphicsProcessor.getMinutiaeList(), 0));
            System.out.println("Image processed!\n");

            // Write graphics to file
            try {
                ImageIO.write(graphicsProcessor.getImage(), "png",
                        new File(System.getProperty("user.dir") + "\\images\\processed\\" + i + ".png"));
            } catch (IOException e) {
                System.out.println(e.getStackTrace());
            }
        }

        trainingSets.get(0).setTarget(1);

        // --------------------
        // Neural network stuff
        // --------------------

        System.out.println("\n-----Starting neural network's tasks...-----\n");

        int layerSize = trainingSets.get(0).getMinutiaeList().size() + 1;
        System.out.println("Creating the perceptron...");
        Perceptron perceptron = new Perceptron(layerSize, layerSize,2);
        System.out.println("Perceptron created!\n");

        System.out.println("Beginning network training...\n");

        int epochCount = 500;
        for(int k = 0; k < epochCount; k++) {
            System.out.println("Epoch " + k + "\n");
            for(int l = 0; l < 10; l++)
            processTrainingSet(trainingSets.get(l), perceptron, l);
        }
    }

    private static void processTrainingSet(TrainingSet trainingSet, Perceptron perceptron, int i) {
        System.out.println("\tTraining set " + i + "\n\n\tFeeding data into input layer...");
        for (int j = 1; j < perceptron.getInputLayer().getNodeList().size(); j++) {
            double value = trainingSet.getMinutiaeList().get(j - 1);
            value = Math.pow(Math.E, value) / (Math.pow(Math.E, value) + 1);
            perceptron.getInputLayer().getNodeList().get(j).setOutputValue(value * 1000);
        }
        System.out.println("\tData successfully fed to the input layer!\n\tCalculating results...");
        for(Node node : perceptron.getHiddenLayer().getNodeList()) {
            node.calculateValues(perceptron.getInputLayer());
        }
        for(Node node : perceptron.getOutputLayer().getNodeList()) {
            node.calculateValues(perceptron.getHiddenLayer());
        }
        double outputOne = perceptron.getOutputLayer().getNodeList().get(0).getOutputValue();
        double outputTwo = perceptron.getOutputLayer().getNodeList().get(1).getOutputValue();
        double outputOneTarget = trainingSet.getTarget();
        //double outputTwoTarget = Math.abs(trainingSet.getTarget() - 1);
        double outputTwoTarget = trainingSet.getTarget();
        double errorOne = 0.5 * Math.pow(outputOneTarget - outputOne, 2);
        double errorTwo = 0.5 * Math.pow(outputTwoTarget - outputTwo, 2);
        double error = errorOne + errorTwo;
        System.out.println("\tOutput one: " + outputOne + "\n\tOutput two: " + outputTwo
                + "\n\tOutput one target: " + outputOneTarget + "\n\tOutput two target: " + outputTwoTarget
                + "\n\tError one: " + errorOne + "\n\tError two: " + errorTwo + "\n\tError: " + error + "\n");

        System.out.println("Backpropagating the error...");

        // Backpropagate through output layer
        for(int l = 0; l < perceptron.getOutputLayer().getNodeList().size(); l++) {
            perceptron.getOutputLayer().getNodeList().get(l).backpropagate(l + 1, outputOneTarget,
                    outputTwoTarget, perceptron);
        }

        // Backpropagate through hidden layer
        for(int l = 0; l < perceptron.getHiddenLayer().getNodeList().size(); l++) {
            perceptron.getHiddenLayer().getNodeList().get(l).backpropagate(0, 0, 0, perceptron);
        }

        // Update values
        for(int l = 0; l < perceptron.getOutputLayer().getNodeList().size(); l++) {
            perceptron.getOutputLayer().getNodeList().get(l).updateWeights();
        }

        for(int l = 0; l < perceptron.getHiddenLayer().getNodeList().size(); l++) {
            perceptron.getHiddenLayer().getNodeList().get(l).updateWeights();
        }
    }
}
