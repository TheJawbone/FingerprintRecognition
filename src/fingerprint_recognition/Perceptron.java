package fingerprint_recognition;

import java.util.List;

/**
 * Perceptron class represents the structure of the neural network. It consists of three layers and is able to learn
 * and then detect patterns in the data.
 */
public class Perceptron {

    /**
     * Input layer of the network.
     */
    private Layer inputLayer;

    /**
     * Hidden layer of the network.
     */
    private Layer hiddenLayer;

    /**
     * Output layer of the network.
     */
    private Layer outputLayer;

    /**
     * Learning rate specifies the rate at which weights in the network are adjusted during backpropagation.
     */
    private static final double learningRate = 0.01;

    /**
     * Instantiates layers and creates bias nodes for input and hidden layers.
     * @param inputLayerSize Size of the input layer.
     * @param hiddenLayerSize Size of the hidden layer.
     * @param outputLayerSize Size of the output layer.
     */
    public Perceptron(int inputLayerSize, int hiddenLayerSize, int outputLayerSize) {

        // Initialize layers, set first elements' value of input and hidden layer to one (bias)
        inputLayer = new Layer(inputLayerSize, 0, SharedTypes.LayerType.INPUT);
        hiddenLayer = new Layer(hiddenLayerSize, inputLayerSize, SharedTypes.LayerType.HIDDEN);
        outputLayer = new Layer(outputLayerSize, hiddenLayerSize, SharedTypes.LayerType.OUTPUT);
        inputLayer.getNodeList().get(0).setOutputValue(1);
        hiddenLayer.getNodeList().get(0).setOutputValue(1);
    }

    /**
     * Trains the network using given set of training data and specified error threshold.
     * @param trainingSets Set of training data.
     * @param errorThreshold Value specifying maximum value of error that can occur during training to deem the network
     *                       functional.
     * @return Number of epochs it took to train the network
     */
    public int train(List<TrainingData> trainingSets, double errorThreshold) {
        double maxError = 1;
        double error;
        int epochCounter = 1;
        while(maxError > errorThreshold) {
            maxError = 0;
            for (TrainingData trainingSet : trainingSets) {
                propagateForwards(trainingSet);
                error = propagateBackwards(trainingSet);
                if(error > maxError) {
                    maxError = error;
                }
            }
            epochCounter++;
        }
        return epochCounter;
    }

    /**
     * Find the fingerprint from among the data pieces in the testing set that matches the pattern.
     * @param testingSets List of testing sets containing processed fingerprint data.
     * @return Index of the fingerprint that matches the patter the most.
     */
    public int test(List<Data> testingSets, double[] searchedValues) {

        double smallestError = Double.MAX_VALUE;
        double error;
        int searchedPatternIndex = -1;
        for(int i = 0; i < testingSets.size(); i++) {
            propagateForwards(testingSets.get(i));
            error = 0;
            for (Node node : outputLayer.getNodeList()) {
                error += Math.abs(searchedValues[node.getIndex()] - node.getOutputValue());
                if(error < smallestError) {
                    smallestError = error;
                    searchedPatternIndex = i;
                }
            }
        }
        return searchedPatternIndex;
    }

    /**
     * Transfers data from the data set onto the input layer and propagate it through hidden and output layers.
     * @param dataSet
     */
    private void propagateForwards(Data dataSet) {

        inputLayer.inputDataSet(dataSet);
        for (Node node : hiddenLayer.getNodeList()) {
            node.calculateValues(inputLayer);
        }
        for (Node node : outputLayer.getNodeList()) {
            node.calculateValues(hiddenLayer);
        }
    }

    /**
     * Propagates the output error backwards through output and hidden layers.
     * @param trainingSet Data used to train the network.
     * @return Combined value of error from all outputs.
     */
    private double propagateBackwards(TrainingData trainingSet) {

        // Calculate outputs and errors
        int outputLayerSize = outputLayer.getNodeList().size();
        double[] outputs = new double[outputLayerSize];
        double[] targetOutputs = new double[outputLayerSize];
        double[] errors = new double[outputLayerSize];
        double error = 0;
        for(int j = 0; j < outputLayerSize; j++) {
            outputs[j] = outputLayer.getNodeList().get(j).getOutputValue();
            targetOutputs[j] = trainingSet.getTargets()[j];
            errors[j] = 0.5 * Math.pow(targetOutputs[j] - outputs[j], 2);
            error += errors[j];
        }

        // Backpropagate the error through output layer
        for(int l = 0; l < outputLayer.getNodeList().size(); l++) {
            outputLayer.getNodeList().get(l).backpropagate(l, targetOutputs, this);
        }

        // Backpropagate the error through hidden layer
        for(int l = 0; l < hiddenLayer.getNodeList().size(); l++) {
            hiddenLayer.getNodeList().get(l).backpropagate(0, new double[] {0}, this);
        }

        // Update values
        for(int l = 0; l < outputLayer.getNodeList().size(); l++) {
            outputLayer.getNodeList().get(l).updateWeights();
        }
        for(int l = 0; l < hiddenLayer.getNodeList().size(); l++) {
            hiddenLayer.getNodeList().get(l).updateWeights();
        }
        return error;
    }

    public Layer getInputLayer() {
        return inputLayer;
    }

    public Layer getHiddenLayer() {
        return hiddenLayer;
    }

    public Layer getOutputLayer() {
        return outputLayer;
    }

    public double getLearningRate() {
        return learningRate;
    }

    @Override
    public String toString() {
        String retVal = "";
        retVal += "Input layer:";
        for(Node node : inputLayer.getNodeList()) {
            retVal += ("\n\tValue " + node.getIndex() + " : " + node.getOutputValue());
        }
        retVal += "\nHidden layer:";
        for(Node node : hiddenLayer.getNodeList()) {
            retVal += ("\n\tValue " + node.getIndex() + " : " + node.getOutputValue());
            retVal += "\n\tWeights:";
            for(double weight : node.getInputWeights()) {
                retVal += ("\n\t\t" + weight);
            }
        }
        retVal += "\nOutput layer:";
        for(Node node : outputLayer.getNodeList()) {
            retVal += ("\n\tValue " + node.getIndex() + " : " + node.getOutputValue());
            retVal += "\n\tWeights:";
            for(double weight : node.getInputWeights()) {
                retVal += ("\n\t\t" + weight);
            }
        }
        return retVal;
    }
}