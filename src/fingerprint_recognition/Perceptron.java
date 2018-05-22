package fingerprint_recognition;

import java.util.ArrayList;
import java.util.List;

public class Perceptron {

    private Layer inputLayer;
    private Layer hiddenLayer;
    private Layer outputLayer;
    private static final double learningRate = 0.005;

    public Perceptron(int inputLayerSize, int hiddenLayerSize, int outputLayerSize) {

        // Initialize layers, set first elements' value of input and hidden layer to one (bias)
        inputLayer = new Layer(inputLayerSize, 0, SharedTypes.LayerType.INPUT);
        hiddenLayer = new Layer(hiddenLayerSize, inputLayerSize, SharedTypes.LayerType.HIDDEN);
        outputLayer = new Layer(outputLayerSize, hiddenLayerSize, SharedTypes.LayerType.OUTPUT);
        inputLayer.getNodeList().get(0).setOutputValue(1);
        hiddenLayer.getNodeList().get(0).setOutputValue(1);
    }

    public Layer getInputLayer() {
        return inputLayer;
    }

    public void setInputLayer(Layer inputLayer) {
        this.inputLayer = inputLayer;
    }

    public Layer getHiddenLayer() {
        return hiddenLayer;
    }

    public void setHiddenLayer(Layer hiddenLayer) {
        this.hiddenLayer = hiddenLayer;
    }

    public Layer getOutputLayer() {
        return outputLayer;
    }

    public void setOutputLayer(Layer outputLayer) {
        this.outputLayer = outputLayer;
    }

    public double getLearningRate() {
        return learningRate;
    }
}
