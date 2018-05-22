package fingerprint_recognition;

import java.util.List;

public class TrainingSet {

    private List<Integer> minutiaeList;
    private int target;

    public TrainingSet(List<Integer> minutiaeList, int target) {
        this.minutiaeList = minutiaeList;
        this.target = target;
    }

    public List<Integer> getMinutiaeList() {
        return minutiaeList;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }
}
