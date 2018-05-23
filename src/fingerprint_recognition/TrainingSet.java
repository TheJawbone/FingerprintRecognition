package fingerprint_recognition;

import java.util.List;

public class TrainingSet {

    private List<Integer> data;
    private int[] targets;

    public TrainingSet(List<Integer> data, int[] targets) {
        this.data = data;
        this.targets = targets;
    }

    public List<Integer> getData() {
        return data;
    }

    public int[] getTargets() {
        return targets;
    }

    public void setTargets(int[] targets) {
        this.targets = targets;
    }
}
