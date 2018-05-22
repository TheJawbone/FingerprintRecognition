package fingerprint_recognition;

import java.util.Random;

public class Node {

    private int index;
    private double netInputValue;
    private double outputValue;
    private double outputNetDerivative;
    double errorOutputDerivative;
    private double[] netToWeightDerivatives;
    private double[] inputWeights;
    private double[] updatedWeights;
    private SharedTypes.LayerType layerType;

    public Node(int index, int inputCount, SharedTypes.LayerType layerType) {
        this.index = index;
        this.layerType = layerType;
        netToWeightDerivatives = new double[inputCount];
        updatedWeights = new double[inputCount];
        inputWeights = new double[inputCount];
        Random generator = new Random();
        for(int i = 0; i < inputCount; i++) {
            inputWeights[i] = generator.nextDouble() * (1.0 / inputCount / 2);
        }
    }

    public void calculateValues(Layer layer) {
        netInputValue = 0;
        for(int i = 0; i < inputWeights.length; i++) {
            netInputValue += layer.getNodeList().get(i).getOutputValue() * inputWeights[i];
        }
        outputValue = Math.pow(Math.E, netInputValue) / (Math.pow(Math.E, netInputValue) + 1);
        outputNetDerivative = outputValue * (1 - outputValue);
    }

    public void backpropagate(int outputIndex, double targetOne, double targetTwo, Perceptron perceptron) {
        switch(layerType) {
            case INPUT:
                break;
            case HIDDEN:
                // For each weight in the node...
                for (int j = 0; j < inputWeights.length; j++) {
                    Node outputNodeOne = perceptron.getOutputLayer().getNodeList().get(0);
                    Node outputNodeTwo = perceptron.getOutputLayer().getNodeList().get(1);

                    // Calculate partial derivatives dEo1 / douthi and dE02 / douthi and their sum
                    double errorOneHiddenOutputDerivative = outputNodeOne.getErrorOutputDerivative()
                            * outputNodeOne.getOutputNetDerivative()
                            * perceptron.getOutputLayer().getNodeList().get(0).getInputWeights()[index];
                    double errorTwoHiddenOutputDerivative = outputNodeTwo.getErrorOutputDerivative()
                            * outputNodeOne.getOutputNetDerivative()
                            * perceptron.getOutputLayer().getNodeList().get(1).getInputWeights()[index];
                    double totalErrorHiddenOutputDerivative = errorOneHiddenOutputDerivative + errorTwoHiddenOutputDerivative;

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
                    switch (outputIndex) {
                        case 1:
                            errorOutputDerivative = outputValue - targetOne;
                            break;
                        case 2:
                            errorOutputDerivative = outputValue - targetTwo;
                            break;
                    }
                    updatedWeights[i] = inputWeights[i] - perceptron.getLearningRate() *
                            errorOutputDerivative * outputNetDerivative * netWeightDerivative;
                }
                break;
            default:
                break;
        }
    }

    public void updateWeights() {
        inputWeights = updatedWeights;
    }

    public double getOutputValue() {
        return outputValue;
    }

    public void setOutputValue(double value) {
        this.outputValue = outputValue;
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
}
