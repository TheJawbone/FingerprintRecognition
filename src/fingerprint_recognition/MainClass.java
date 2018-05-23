package fingerprint_recognition;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
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
            trainingSets.add(new TrainingSet(graphicsProcessor.getMinutiaeList(), new int[] {0}));
            System.out.println("Image processed!\n");

            // Write graphics to file
            try {
                ImageIO.write(graphicsProcessor.getImage(), "png",
                        new File(System.getProperty("user.dir") + "\\images\\processed\\" + i + ".png"));
            } catch (IOException e) {
                System.out.println(e.getStackTrace());
            }
        }

        trainingSets.get(0).setTargets(new int[] {1});

        // --------------------
        // Neural network stuff
        // --------------------

        System.out.println("\n-----Starting neural network's tasks...-----\n");

        int layerSize = trainingSets.get(0).getData().size() + 1;
        System.out.println("Creating the perceptron...");
        Perceptron perceptron = new Perceptron(layerSize, layerSize,1);
        System.out.println("Perceptron created!\nPrint initial network state[Y/N]? ");

        // Ask to print initial network state
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            String input = br.readLine();
            if(input.equals("y") || input.equals("Y")) {
                System.out.println(perceptron.toString());
            }
        } catch (IOException e) {}

        System.out.println("Beginning network training...\n");

        //int epochCount = 1000000;
        int k = 0;
        double maxError = 1;
        double error;
        double errorThreshold = 0.001;
        while(maxError > errorThreshold) {
            maxError = 0;
            k++;
            System.out.println("Epoch " + k);
            //System.out.println("\n");
            for(int l = 0; l < trainingSets.size() * 2; l++) {
                if(l % 2 == 0) {
                    error = processTrainingSet(trainingSets.get(l / 2), perceptron, l / 2, k);
                } else {
                    error = processTrainingSet(trainingSets.get(0), perceptron, 0, k);
                }
                if(error > maxError) {
                    maxError = error;
                }
            }
            /*for (int l = 0; l < 2; l++) {
                error = processTrainingSet(trainingSets.get(l), perceptron, l, k);
                if(error > maxError) {
                    maxError = error;
                }
            }*/
        }
        // Ask to print final network state
        System.out.println("\nNetwork trained successfully! Print final network state[Y/N]? ");
        br = new BufferedReader(new InputStreamReader(System.in));
        try {
            String input = br.readLine();
            if(input.equals("y") || input.equals("Y")) {
                System.out.println(perceptron.toString());
            }
        } catch (IOException e) {}
    }

    private static double processTrainingSet(TrainingSet trainingSet, Perceptron perceptron, int i, int k) {

        // Copy data between training set and input layer
        //System.out.println("\t-----Training set " + i + "-----\n\n\tFeeding data into input layer...");
        perceptron.getInputLayer().inputTrainingSet(trainingSet);

        // Feed the data from the input layer forward through the hidden and output layer
        //System.out.println("\tData successfully fed to the input layer!\n\tCalculating results...\n");
        for(Node node : perceptron.getHiddenLayer().getNodeList()) {
            node.calculateValues(perceptron.getInputLayer());
        }
        for(Node node : perceptron.getOutputLayer().getNodeList()) {
            node.calculateValues(perceptron.getHiddenLayer());
        }

        // Calculate outputs and errors
        int outputLayerSize = perceptron.getOutputLayer().getNodeList().size();
        double[] outputs = new double[outputLayerSize];
        double[] targetOutputs = new double[outputLayerSize];
        double[] errors = new double[outputLayerSize];
        double error = 0;
        for(int j = 0; j < outputLayerSize; j++) {
            outputs[j] = perceptron.getOutputLayer().getNodeList().get(j).getOutputValue();
            targetOutputs[j] = trainingSet.getTargets()[j];
            errors[j] = 0.5 * Math.pow(targetOutputs[j] - outputs[j], 2);
            error += errors[j];
            //System.out.println("\tOutput " + j + " :\t\t\t" + outputs[j] + "\n\tTarget output " + j + " :\t" + targetOutputs[j]
            //        + "\n\tError " + j + " :\t\t\t" + errors[j] + "\n");
        }
        //System.out.println("\tTotal error:\t\t" + error + "\n");

        // Backpropagate the error through output layer
        //System.out.println("\tBackpropagating the error...");
        for(int l = 0; l < perceptron.getOutputLayer().getNodeList().size(); l++) {
            perceptron.getOutputLayer().getNodeList().get(l).backpropagate(l, targetOutputs, perceptron);
        }

        // Backpropagate the error through hidden layer
        for(int l = 0; l < perceptron.getHiddenLayer().getNodeList().size(); l++) {
            perceptron.getHiddenLayer().getNodeList().get(l).backpropagate(0, new double[] {0}, perceptron);
        }

        // Update values
        for(int l = 0; l < perceptron.getOutputLayer().getNodeList().size(); l++) {
            perceptron.getOutputLayer().getNodeList().get(l).updateWeights();
        }
        for(int l = 0; l < perceptron.getHiddenLayer().getNodeList().size(); l++) {
            perceptron.getHiddenLayer().getNodeList().get(l).updateWeights();
        }
        //System.out.println("\tBackpropagation complete!\n");
        return error;
    }
}
