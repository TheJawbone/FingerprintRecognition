package fingerprint_recognition;

import java.util.ArrayList;
import java.util.List;

/**
 * Layer class represents a layer in a neural network.
 */
public class Layer {

    /**
     * List of nodes that the layer consists of.
     */
    private List<Node> nodeList;

    /**
     * Type of the layer.
     */
    private SharedTypes.LayerType layerType;

    /**
     * Constructor that initializes all the necessary fields.
     * @param nodeCount Number of nodes in the layer.
     * @param nodeInputCount Number of inputs for each node in the layer.
     * @param layerType Type of layer.
     */
    public Layer(int nodeCount, int nodeInputCount, SharedTypes.LayerType layerType) {
        this.layerType = layerType;
        nodeList = new ArrayList<>();
        int i = 0;
        Node node;

        //Create bias node for each layer except output layer
        if(!layerType.equals(SharedTypes.LayerType.OUTPUT)) {
            node = new Node(0, 0, SharedTypes.LayerType.BIAS);
            node.setOutputValue(1);
            nodeList.add(node);
            i++;
        }
        for (; i < nodeCount; i++) {
            node = new Node(i, nodeInputCount, layerType);
            nodeList.add(node);
        }
    }

    public List<Node> getNodeList() {
        return nodeList;
    }

    /**
     * Assigns input data to the layer if it's type is input.
     * @param trainingSet
     */
    public void inputDataSet(Data trainingSet) {
        if(layerType == SharedTypes.LayerType.INPUT) {
            int dataSum = 0;
            for(int value : trainingSet.getData()) {
                dataSum += value;
            }
            int dataAvg = dataSum / trainingSet.getData().size();
            for (int j = 1; j < nodeList.size(); j++) {
                nodeList.get(j).setOutputValue(trainingSet.getData().get(j - 1) - dataAvg);
            }
        }
    }
}