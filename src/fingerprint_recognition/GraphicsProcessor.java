package fingerprint_recognition;

import java.awt.image.BufferedImage;

public class GraphicsProcessor {

    private int[][] imageArray;
    private int[][] ridgeEndingArray;
    private int[][] ridgeBifurcationArray;
    private BufferedImage image;
    private BufferedImage processedImage;
    private BufferedImage blurredImage;
    private BufferedImage binarizedImage;
    private BufferedImage thinnedImage;
    private BufferedImage minutiaeImage;

    public GraphicsProcessor(BufferedImage image) {

        this.image = image;
        imageArray = new int[image.getWidth()][image.getHeight()];
        ridgeEndingArray = new int[image.getWidth()][image.getHeight()];
        ridgeBifurcationArray = new int[image.getWidth()][image.getHeight()];
        //gaussBlur(5);
        binarize();
        thin();
        thin();
        thin();
        findMinutiae();
    }

    private void binarize() {

        // Transform the graphic to gray scale and store it in the array
        // while calculating the sum of all the transformed pixels for later threshholding
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
        binarizedImage = image;
        int avgPixelValue = grayScaleSum / (image.getHeight() * image.getWidth());
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (imageArray[x][y] > avgPixelValue) {
                    imageArray[x][y] = -1;
                } else {
                    imageArray[x][y] = 0;
                }
                binarizedImage.setRGB(x, y, imageArray[x][y]);
            }
        }
    }

    private void thin() {

        // Clear the border pixels, instantiate thinned image
        for(int x = 0; x < image.getWidth(); x++) {
            imageArray[x][0] = -1;
            imageArray[x][image.getHeight() - 1] = -1;
        }
        for(int y = 0; y < image.getHeight(); y++) {
            imageArray[0][y] = -1;
            imageArray[image.getWidth() - 1][y] = -1;
        }
        thinnedImage = image;

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
                    thinnedImage.setRGB(x, y, imageArray[x][y]);
                }
            }
        }
    }

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

    private void gaussBlur (float radius) {

        // Instantiate blurred image
        blurredImage = image;

        // Store original array
        int [][] sourceArray = new int[image.getWidth()][];
        for(int i = 0; i < imageArray.length; i++)
            sourceArray[i] = imageArray[i].clone();

        // Gaussian blur
        int significantRadius = (int)Math.ceil(radius * 2.57);     // significant radius
        for(int i = 1; i < image.getHeight() - 2; i++) {
            for (int j = 1; j < image.getWidth() - 2; j++) {
                int val = 0, wsum = 0;
                for (int iy = i - significantRadius; iy < i + significantRadius + 1; iy++)
                    for (int ix = j - significantRadius; ix < j + significantRadius + 1; ix++) {
                        int x = Math.min(image.getWidth() - 1, Math.max(0, ix));
                        int y = Math.min(image.getHeight() - 1, Math.max(0, iy));
                        int dsq = (ix - j) * (ix - j) + (iy - i) * (iy - i);
                        double wght = Math.exp(-dsq / (2 * radius * radius)) / (Math.PI * 2 * radius * radius);
                        val += sourceArray[y][x] * wght;
                        wsum += wght;
                    }
                imageArray[j][i] = Math.round(val / wsum);
                blurredImage.setRGB(j, i, imageArray[j][i]);
            }
        }
    }

    private int comparePixels(int p1, int p2) {
        if(p1 == 0 && p2 == 1) {
            return 1;
        } else {
            return 0;
        }
    }

    public BufferedImage getBlurredImage() {
        return blurredImage;
    }

    public BufferedImage getBinarizedImage() {
        return binarizedImage;
    }

    public BufferedImage getThinnedImage() {
        return thinnedImage;
    }

    public BufferedImage getMinutiaeImage() {
        return minutiaeImage;
    }
}
