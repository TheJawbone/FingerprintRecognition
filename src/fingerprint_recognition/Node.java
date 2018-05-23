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
        if(layerType != SharedTypes.LayerType.BIAS) {
            netInputValue = 0;
            for (int i = 0; i < inputWeights.length; i++) {
                netInputValue += layer.getNodeList().get(i).getOutputValue() * inputWeights[i];
            }
            outputValue = Math.pow(Math.E, netInputValue) / (Math.pow(Math.E, netInputValue) + 1);
            outputNetDerivative = outputValue * (1 - outputValue);
        }
    }

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
