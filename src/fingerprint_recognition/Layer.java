package fingerprint_recognition;

import java.util.ArrayList;
import java.util.List;

public class Layer {

    private List<Node> nodeList;
    private SharedTypes.LayerType layerType;

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

    public void inputDataSet(DataSet trainingSet) {
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