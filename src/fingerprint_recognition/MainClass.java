package fingerprint_recognition;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MainClass {

    public static void main(String[] argv) {

        BufferedImage image = null;
        try {
            image = ImageIO.read(new File("fingerprint3.bmp"));
        } catch (IOException e) {
            System.out.println(e.getStackTrace());
        }

        GraphicsProcessor graphicsProcessor = new GraphicsProcessor(image);

        // Write graphics to file
        try {
            ImageIO.write(graphicsProcessor.getThinnedImage(), "png", new File(System.getProperty("user.dir") + "\\processed.png"));
            //ImageIO.write(graphicsProcessor.getThinnedImage(), "png", new File(System.getProperty("user.dir") + "\\thinned.png"));
        } catch (IOException e) {
            System.out.println(e.getStackTrace());
        }
    }
}
