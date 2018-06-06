package fingerprint_recognition;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Graphics processor responsible for processing input images to a form which can be processed by the neural network.
 */
public class GraphicsProcessor {

    /**
     * Array containing pixel values of an image.
     */
    private int[][] imageArray;

    /**
     * Array used to store information regarding ridge endings' occurrences.
     */
    private int[][] ridgeEndingArray;

    /**
     * Array used to store information regarding ridge bifurcations' occurrences.
     */
    private int[][] ridgeBifurcationArray;

    /**
     * List containing processed minutiae data from the input image.
     */
    private List<Integer> minutiaeList;

    /**
     * Stores input image.
     */
    private BufferedImage image;

    /**
     * Width of the input image.
     */
    private int imageWidth;

    /**
     * Height of the input image.
     */
    private int imageHeight;

    /**
     * Width of the processing window.
     */
    private int windowWidth;

    /**
     * Height of the processing window.
     */
    private int windowHeight;

    /**
     * Value that determines by how much the processing window will be moved in each iteration.
     */
    private int overlapFactor;

    /**
     * Processes images in a specified folder - extract minutiae, use them to generate data set for tne neural network
     * and generate processed images with marked minutiae.
     * @param sourcePath Path to folder containing source images.
     * @param destinationPath Path to folder into which processed images will be written.
     * @param startingIndex Index (name) of the first image.
     * @param imageCount Number of images in the folder.
     * @param windowWidth Width of the processing window.
     * @param windowHeight Height of the processing window.
     * @return Processed minutiae vector to be used as input to the neural network.
     */
    public List<Data> processBatch(String sourcePath, String destinationPath, int startingIndex, int imageCount, int windowWidth, int windowHeight, int overlapFactor) {

        this.image = null;
        List<Data> dataSet = new ArrayList<>();

        // Process each image...
        for(int i = startingIndex; i < imageCount + startingIndex; i++) {

            // Load the image
            try {
                System.out.println("Opening image \"" + i + ".bmp\"...");
                image = ImageIO.read(new File(sourcePath + i + ".bmp"));
                imageWidth = image.getWidth();
                imageHeight = image.getHeight();
                imageArray = new int[imageWidth][imageHeight];
                ridgeEndingArray = new int[imageWidth][imageHeight];
                ridgeBifurcationArray = new int[imageWidth][imageHeight];
                this.windowHeight = windowHeight;
                this.windowWidth = windowWidth;
                this.overlapFactor = overlapFactor;
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

            // Process the image
            System.out.println("Processing image...");
            binarize();
            for(int j = 0; j < 10; j++) {
                thin();
            }
            findMinutiae();
            generateMinutiaeArray();
            dataSet.add(new Data(minutiaeList));
            System.out.println("Image processed!\n");

            // Write processed image to file
            try {
                ImageIO.write(image, "png",
                        new File(destinationPath + i + ".png"));
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        return dataSet;
    }

    /**
     * Binarizes the image, using an average value of all the pixels.
     */
    private void binarize() {

        // Calculate an average value of all the pixels
        int grayScaleSum = 0;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y);
                int red = (rgb >> 16) & 0x000000FF;
                int green = (rgb >> 8) & 0x000000FF;
                int blue = (rgb) & 0x000000FF;
                imageArray[x][y] = (red + green + blue) / 3;
                grayScaleSum += imageArray[x][y];
            }
        }

        // Threshold (binarize) the array, create binarized image
        int avgPixelValue = grayScaleSum / (image.getHeight() * image.getWidth());
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (imageArray[x][y] > avgPixelValue) {
                    imageArray[x][y] = -1;
                } else {
                    imageArray[x][y] = 0;
                }
                image.setRGB(x, y, imageArray[x][y]);
            }
        }
    }

    /**
     * Thins (skeletonizes) the image using the Zhang-Suen algorithm
     */
    private void thin() {

        // Clear the border pixels
        for(int x = 0; x < image.getWidth(); x++) {
            imageArray[x][0] = -1;
            imageArray[x][image.getHeight() - 1] = -1;
        }
        for(int y = 0; y < image.getHeight(); y++) {
            imageArray[0][y] = -1;
            imageArray[image.getWidth() - 1][y] = -1;
        }

        // Zhang-Suen algorithm
        int numberOfChanges = 1;
        while(numberOfChanges > 0) {
            for(int x = 1; x < image.getWidth() - 2; x++) {
                for(int y = 1; y < image.getHeight() - 2; y++) {

                    // If the pixel is black, check if it should be deleted
                    numberOfChanges = 0;
                    if(imageArray[x][y] == 0) {

                        // Declare values of the neighbours
                        int p8 = imageArray[x - 1][y] + 1, p9 = imageArray[x - 1][y - 1] + 1,
                                p2 = imageArray[x][y - 1] + 1, p3 = imageArray[x + 1][y - 1] + 1, p4 = imageArray[x + 1][y] + 1,
                                p5 = imageArray[x + 1][y + 1] + 1, p6 = imageArray[x][y + 1] + 1, p7 = imageArray[x - 1][y + 1] + 1;

                        // First condition
                        int numberOfPresentNeighbours = p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9;
                        boolean condition1 = (numberOfPresentNeighbours >= 2 && numberOfPresentNeighbours <= 6);

                        // Second condition
                        int numberOfTransitions = 0;
                        numberOfTransitions += comparePixels(p2, p3);
                        numberOfTransitions += comparePixels(p3, p4);
                        numberOfTransitions += comparePixels(p4, p5);
                        numberOfTransitions += comparePixels(p5, p6);
                        numberOfTransitions += comparePixels(p6, p7);
                        numberOfTransitions += comparePixels(p7, p8);
                        numberOfTransitions += comparePixels(p8, p9);
                        numberOfTransitions += comparePixels(p9, p2);
                        boolean condition2 = (numberOfTransitions == 1);

                        // Third and fourth condition
                        boolean condition3, condition4;
                        if ((x + y) % 2 != 0) {
                            condition3 = (p2 * p4 * p6 == 0);
                            condition4 = (p4 * p6 * p8 == 0);
                        } else {
                            condition3 = (p2 * p4 * p8 == 0);
                            condition4 = (p2 * p6 * p8 == 0);
                        }

                        // Summarize conditions, delete pixel if all of them are met
                        if(condition1 && condition2 && condition3 && condition4) {
                            imageArray[x][y] = -1;
                            numberOfChanges++;
                        }
                    }
                    image.setRGB(x, y, imageArray[x][y]);
                }
            }
        }
    }

    /**
     * Detects minutiae on the image and stores information regarding them in corresponding arrays.
     */
    private void findMinutiae() {

        for(int x = 1; x < image.getWidth() - 2; x++) {
            for(int y = 1; y < image.getHeight() - 2; y++) {
                if(imageArray[x][y] == 0) {
                    int numberOfBorderPixels = 0;
                    numberOfBorderPixels += imageArray[x][y-1] + 1;
                    numberOfBorderPixels += imageArray[x+1][y] + 1;
                    numberOfBorderPixels += imageArray[x][y+1] + 1;
                    numberOfBorderPixels += imageArray[x-1][y] + 1;

                    if(numberOfBorderPixels == 1) {
                        ridgeEndingArray[x][y] = 1;
                    } else if(numberOfBorderPixels == 3) {
                        ridgeBifurcationArray[x][y] = 1;
                    }
                }
            }
        }
        drawMinutiaeMarkers();
    }

    /**
     * Draws markers for each minutiae on a processed image.
     */
    private void drawMinutiaeMarkers() {
        for(int x = 1; x < image.getWidth() - 2; x++) {
            for (int y = 1; y < image.getHeight() - 2; y++) {
                if(ridgeEndingArray[x][y] == 1) {
                    image.setRGB(x - 1, y - 1, 0xFFFF0000);
                    image.setRGB(x, y - 1, 0xFFFF0000);
                    image.setRGB(x + 1, y - 1, 0xFFFF0000);
                    image.setRGB(x - 1, y, 0xFFFF0000);
                    image.setRGB(x, y, 0xFFFF0000);
                    image.setRGB(x + 1, y, 0xFFFF0000);
                    image.setRGB(x - 1, y + 1, 0xFFFF0000);
                    image.setRGB(x, y + 1, 0xFFFF0000);
                    image.setRGB(x + 1, y + 1, 0xFFFF0000);
                } else if(ridgeBifurcationArray[x][y] == 1) {
                    image.setRGB(x - 1, y - 1, 0xFF00FF00);
                    image.setRGB(x, y - 1, 0xFF00FF00);
                    image.setRGB(x + 1, y - 1, 0xFF00FF00);
                    image.setRGB(x - 1, y, 0xFF00FF00);
                    image.setRGB(x, y, 0xFF00FF00);
                    image.setRGB(x + 1, y, 0xFF00FF00);
                    image.setRGB(x - 1, y + 1, 0xFF00FF00);
                    image.setRGB(x, y + 1, 0xFF00FF00);
                    image.setRGB(x + 1, y + 1, 0xFF00FF00);
                }
            }
        }
    }

    /**
     * Sums values of each minutiae array using processing window and then combines the arrays into one.
     */
    private void generateMinutiaeArray() {

        minutiaeList = new ArrayList<>();
        for(int x = 0; x < imageWidth - windowWidth + 1; x += windowWidth / overlapFactor) {
            for(int y = 0; y < imageHeight - windowHeight + 1; y += windowHeight) {
                minutiaeList.add(calculateSubarraySum(x, y, ridgeEndingArray));
            }
        }
        for(int x = 0; x < imageWidth - windowWidth + 1; x += windowWidth / overlapFactor) {
            for(int y = 0; y < imageHeight - windowHeight + 1; y += windowHeight) {
                minutiaeList.add(calculateSubarraySum(x, y, ridgeBifurcationArray));
            }
        }
    }

    /**
     * Calculates a sum of a 2d subarray.
     * @param x X position of the subarray in the array (subarray origin is it's top left corner).
     * @param y Y position of the subarray in the array (subarray origin is it's top left corner).
     * @param array Array for which the subarray sum is being calculated.
     * @return Calculated sum of the subarray
     */
    private int calculateSubarraySum(int x, int y, int[][] array) {

        int sum = 0;
        for(int i = x; i < x + windowWidth; i++) {
            for(int j = y; j < y + windowHeight; j++) {
                sum += array[i][j];
            }
        }
        return sum;
    }

    /**
     * Pixel comparison used in the Zhang-Suen algorithm.
     * @param p1 Value of the first pixel.
     * @param p2 Value of the second pixel.
     * @return Result of the comparison.
     */
    private int comparePixels(int p1, int p2) {
        if(p1 == 0 && p2 == 1) {
            return 1;
        } else {
            return 0;
        }
    }
}
