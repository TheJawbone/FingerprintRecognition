package fingerprint_recognition;

import javafx.scene.chart.PieChart;

import java.util.List;

public class TrainingSet extends DataSet {

    private int[] targets;

    public TrainingSet(List<Integer> data, int[] targets) {
        super(data);
        this.targets = targets;
    }

    public TrainingSet(DataSet dataSet) {
        super(dataSet.getData());
        targets = new int[] {0};
    }

    public int[] getTargets() {
        return targets;
    }

    public void setTargets(int[] targets) {
        this.targets = targets;
    }
}