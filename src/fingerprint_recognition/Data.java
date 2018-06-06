package fingerprint_recognition;

import java.util.List;

/**
 * Data class represent input data for the network.
 */
public class Data {

    /**
     * List of integers representing the data.
     */
    private List<Integer> data;

    /**
     * Main constructor.
     * @param data List of integers representing the data.
     */
    public Data(List<Integer> data) {
        this.data = data;
    }

    public List<Integer> getData() {
        return data;
    }
}
