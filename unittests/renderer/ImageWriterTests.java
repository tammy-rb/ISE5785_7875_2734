package renderer;

import org.junit.jupiter.api.Test;
import primitives.Color;

import static java.awt.Color.*;
import static org.junit.jupiter.api.Assertions.*;

class ImageWriterTests {

    @Test
    void testWriteToImage() {
        ImageWriter imageWriter = new ImageWriter(800, 500);
        int numOfRows = 10;
        int numOfCols = 16;
        int spaceX = 800/numOfCols;
        int spaceY = 500/numOfRows;
        Color yellow = new Color(YELLOW);
        Color red = new Color(RED);
        for (int i = 0; i < imageWriter.nX(); i++) {
            for (int j = 0; j < imageWriter.nY(); j++) {
                if (i % spaceX == 0 || j % spaceY == 0) {
                    imageWriter.writePixel(i, j, red);
                } else {
                    imageWriter.writePixel(i, j, yellow);
                }
            }
        }
        imageWriter.writeToImage("yellow");
    }
}