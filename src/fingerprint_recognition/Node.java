package fingerprint_recognition;

import java.util.Random;

/**
 * Mode class represents a single neuron in a neural network.
 */
public class Node {

    /**
     * Index representing position of the node in the layer.
     */
    private int index;

    /**
     * Combined value from all of the node's inputs.
     */
    private double netInputValue;

    /**
     * Output value of the node
     */
    private double outputValue;

    /**
     * Derivative of output value over net input value.
     */
    private double outputNetDerivative;

    /**
     * Derivative of total error over node's output value.
     */
    private double errorOutputDerivative;

    /**
     * Array of node's input weights.
     */
    private double[] inputWeights;

    /**
     * Array of node's updated weights (separate from input weights for the sake of backpropagation algorithm).
     */
    private double[] updatedWeights;

    /**
     * Type of the layer that the node belongs to.
     */
    private SharedTypes.LayerType layerType;

    /**
     * Constructor that initializes all the necessary fields.
     * @param index Index of the node in it's layer.
     * @param inputCount Number of node's inputs.
     * @param layerType Type of layer that the node belongs to.
     */
    public Node(int index, int inputCount, SharedTypes.LayerType layerType) {
        this.index = index;
        this.layerType = layerType;
        updatedWeights = new double[inputCount];
        inputWeights = new double[inputCount];
        Random generator = new Random();
        for(int i = 0; i < inputCount; i++) {
            inputWeights[i] = generator.nextDouble() * (1.0 / inputCount / 2);
        }
    }

    /**
     * Calculates net input value and output value.
     * @param layer Layer to the left of the node's layer.
     */
    public void calculateValues(Layer layer) {
        if(layerType != SharedTypes.LayerType.BIAS) {
            netInputValue = 0;
            for (int i = 0; i < inputWeights.length; i++) {
                netInputValue += layer.getNodeList().get(i).getOutputValue() * inputWeights[i];
            }
            outputValue = Math.pow(Math.E, netInputValue) / (Math.pow(Math.E, netInputValue) + 1);
            outputNetDerivative = outputValue * (1 - outputValue);
        }
    }

    /**
     * Implements the backpropagation algorithm.
     * @param outputIndex Index of the output from which the backpropagation starts.
     * @param targetOutputs Array of target values for each output.
     * @param perceptron Perceptron that the node belongs to.
     */
    public void backpropagate(int outputIndex, double[] targetOutputs, Perceptron perceptron) {
        switch(layerType) {
            case INPUT:
                break;
            case HIDDEN:

                // For each weight in the node...
                for (int j = 0; j < inputWeights.length; j++) {

                    // Calculate partial derivatives dEok / douthi and their sum
                    double totalErrorHiddenOutputDerivative = 0;
                    for(int k = 0; k < perceptron.getOutputLayer().getNodeList().size(); k++) {
                        double errorHiddenOutputDerivative = perceptron.getOutputLayer().getNodeList().get(k).getErrorOutputDerivative()
                                * perceptron.getOutputLayer().getNodeList().get(k).getOutputNetDerivative()
                                * perceptron.getOutputLayer().getNodeList().get(k).getInputWeights()[index];
                        totalErrorHiddenOutputDerivative += errorHiddenOutputDerivative;
                    }

                    // Calculate partial derivative douthi / dnethi
                    outputNetDerivative = outputValue * (1 - outputValue);

                    // Calculate partial derivative dneth1 / dwj
                    double netWeightDerivative = perceptron.getInputLayer().getNodeList().get(j).getOutputValue();

                    // Calculate derivative dEtotal / dwj
                    double totalErrorWeightDerivative = totalErrorHiddenOutputDerivative * outputNetDerivative * netWeightDerivative;

                    // Calculate and store updated value
                    updatedWeights[j] = inputWeights[j] - perceptron.getLearningRate() * totalErrorWeightDerivative;
                }
                break;
            case OUTPUT:
                for(int i = 0; i < inputWeights.length; i++) {
                    double netWeightDerivative = perceptron.getHiddenLayer().getNodeList().get(i).getOutputValue();
                    errorOutputDerivative = 0;
                    errorOutputDerivative = outputValue - targetOutputs[outputIndex];
                    updatedWeights[i] = inputWeights[i] - perceptron.getLearningRate() *
                            errorOutputDerivative * outputNetDerivative * netWeightDerivative;
                }
                break;
            default:
                break;
        }
    }

    /**
     * Updates input weights by assigning to them the value of updated weights.
     */
    public void updateWeights() {
        inputWeights = updatedWeights;
    }

    public double getOutputValue() {
        return outputValue;
    }

    public void setOutputValue(double value) {
        this.outputValue = value;
    }

    public double getErrorOutputDerivative() {
        return errorOutputDerivative;
    }

    public double getOutputNetDerivative() {
        return outputNetDerivative;
    }

    public double[] getInputWeights() {
        return inputWeights;
    }

    public int getIndex() {
        return index;
    }
}