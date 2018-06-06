package fingerprint_recognition;

/**
 * ParamsManager class is responsible for parsing and rendering command line arguments accessible.
 */
public class ParamsManager {

    /**
     * Path to the directory containing training images.
     */
    private String trainingSourceImagesPath;

    /**
     * Path to the directory in which processed training images are to be stored.
     */
    private String trainingProcessedImagesPath;

    /**
     * Path to the directory containing testing images.
     */
    private String testingSourceImagesPath;

    /**
     * PAth to the directory in which processed testing images are to be stored.
     */
    private String testingProcessedImagesPath;

    /**
     * Value of the error threshold used in network's training.
     */
    private double errorThreshold;

    /**
     * Index/name of the first image in the training set.
     */
    private int firstTrainingImageIndex;

    /**
     * Index/name of the first image in the testing set.
     */
    private int firstTestingImageIndex;

    /**
     * Number of training images.
     */
    private int trainingImageCount;

    /**
     * Number of testing images.
     */
    private int testingImageCount;

    /**
     * Width of the window used in minutiae array processing.
     */
    private int windowWidth;

    /**
     * Height of the window used in minutiae array processing.
     */
    private int windowHeight;

    /**
     * Overlap factor for the window used in minutiae array processing.
     */
    private int overlapFactor;

    /**
     * Parses command line arguments and stores them in the class' fields.
     * @param argv Command line arguments.
     * @return Boolean informing whether parsing was successful (true) or not (false).
     */
    public boolean parseArguments(String[] argv) {

        if(argv.length != 12) {
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
                overlapFactor = Integer.parseInt(argv[11]);
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

    public int getOverlapFactor() {
        return overlapFactor;
    }
}
