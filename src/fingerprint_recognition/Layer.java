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
}
