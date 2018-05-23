package fingerprint_recognition;

public class Perceptron {

    private Layer inputLayer;
    private Layer hiddenLayer;
    private Layer outputLayer;
    private static final double learningRate = 0.01;

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
