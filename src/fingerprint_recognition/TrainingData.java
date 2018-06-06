package fingerprint_recognition;

import java.util.List;

/**
 * TrainingData class represents a piece of training data.
 */
public class TrainingData extends Data {

    /**
     * Array of integers representing target values for each output of the network.
     */
    private int[] targets;

    /**
     * Main constructor.
     * @param data Data to be stored in the class's object.
     */
    public TrainingData(Data data) {
        super(data.getData());
        targets = new int[] {0};
    }

    public int[] getTargets() {
        return targets;
    }

    public void setTargets(int[] targets) {
        this.targets = targets;
    }
}