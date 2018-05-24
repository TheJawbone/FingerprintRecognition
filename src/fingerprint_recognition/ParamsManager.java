package fingerprint_recognition;

import java.text.NumberFormat;

public class ParamsManager {


    private String trainingSourceImagesPath;
    private String trainingProcessedImagesPath;
    private String testingSourceImagesPath;
    private String testingProcessedImagesPath;
    private double errorThreshold;
    private int firstTrainingImageIndex;
    private int firstTestingImageIndex;
    private int trainingImageCount;
    private int testingImageCount;
    private int windowWidth;
    private int windowHeight;

    public boolean parseArguments(String[] argv) {

        if(argv.length != 11) {
            System.out.println("No valid command line parameters detected!");
            return false;
        } else {
            try {
                trainingSourceImagesPath = argv[0];
                trainingProcessedImagesPath = argv[1];
                testingSourceImagesPath = argv[2];
                testingProcessedImagesPath = argv[3];
                errorThreshold = Double.parseDouble(argv[4]);
                firstTrainingImageIndex = Integer.parseInt(argv[5]);
                firstTestingImageIndex = Integer.parseInt(argv[6]);
                trainingImageCount = Integer.parseInt(argv[7]);
                testingImageCount = Integer.parseInt(argv[8]);
                windowWidth = Integer.parseInt(argv[9]);
                windowHeight = Integer.parseInt(argv[10]);
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
                return false;
            }
        }
        return true;
    }

    public String getTrainingSourceImagesPath() {
        return trainingSourceImagesPath;
    }

    public String getTrainingProcessedImagesPath() {
        return trainingProcessedImagesPath;
    }

    public String getTestingSourceImagesPath() {
        return testingSourceImagesPath;
    }

    public String getTestingProcessedImagesPath() {
        return testingProcessedImagesPath;
    }

    public double getErrorThreshold() {
        return errorThreshold;
    }

    public int getFirstTrainingImageIndex() {
        return firstTrainingImageIndex;
    }

    public int getFirstTestingImageIndex() {
        return firstTestingImageIndex;
    }

    public int getTrainingImageCount() {
        return trainingImageCount;
    }

    public int getTestingImageCount() {
        return testingImageCount;
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public int getWindowHeight() {
        return windowHeight;
    }
}
