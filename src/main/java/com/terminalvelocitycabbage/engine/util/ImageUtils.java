package com.terminalvelocitycabbage.engine.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ImageUtils {

    /**
     * Converts an RGBA ByteBuffer into a BufferedImage and writes it to a PNG file.
     *
     * @param buffer    The ByteBuffer containing RGBA pixel data (row-major).
     * @param width     Image width in pixels.
     * @param height    Image height in pixels.
     * @param output    File to write the PNG to.
     * @throws IOException If writing fails.
     */
    public static void saveRGBAtoPNG(ByteBuffer buffer, int width, int height, File output) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int i = (y * width + x) * 4;
                int r = buffer.get(i)   & 0xFF;
                int g = buffer.get(i+1) & 0xFF;
                int b = buffer.get(i+2) & 0xFF;
                int a = buffer.get(i+3) & 0xFF;

                int argb = (a << 24) | (r << 16) | (g << 8) | b;
                image.setRGB(x, y, argb);
            }
        }

        try {
            ImageIO.write(image, "PNG", output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
