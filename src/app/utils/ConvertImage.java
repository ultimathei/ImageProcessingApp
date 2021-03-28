package app.utils;

import java.awt.Graphics;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javax.imageio.ImageIO;

import app.App;

/**
 * A utility class for image conversions
 */
public class ConvertImage {
  // private constructor to hide implicit public one
  private ConvertImage() {
    //
  }

  /**
   * Read in File as bufferedImage, then convert to type_int_rgb finally convert
   * to javafx image
   * 
   * @param selectedFile a File object to be converted to Image object
   * @return the file converted to javafx Image or null if there was an error
   *         during load or conversion
   */
  public static Image toJavafx(File selectedFile) throws IOException {
    BufferedImage bi = ImageIO.read(selectedFile);
    int w = bi.getWidth();
    int h = bi.getHeight();

    BufferedImage biRGB = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    Graphics big = biRGB.getGraphics();
    big.drawImage(bi, 0, 0, null);

    return SwingFXUtils.toFXImage(biRGB, null);
  }

  public static Image scale(Image image, double scale) {
    int width = (int) image.getWidth();
    int height = (int) image.getHeight();
    int[][][] imageArray = toArray(image);

    int newWidth = (int)(width * scale);
    int newHeight = (int)(height * scale);

    // App.LOGGER.log("new height: "+newHeight);
    // App.LOGGER.log("new width: "+newWidth);

    int[][][] newImgArray = new int[newWidth][newHeight][4];

    // nearest neighbour
    for (int y = 0; y < newHeight; y++) {
      for (int x = 0; x < newWidth; x++) {
          int normalisedX;
          int normalisedY;
          if(scale < 1.0) {
            normalisedX = (int) (x/scale);
            normalisedY = (int) (y/scale);
          } else {
            normalisedX = (int) (x/scale);
            normalisedY = (int) (y/scale);
          }
          
          newImgArray[x][y] = imageArray[normalisedX][normalisedY];
      }
    }

    return fromArray(newImgArray);
  }

  /**
   * Transform image to be flipped if vertically is true, the flip happens along
   * the vertical axis else along the horizontal axis
   * 
   * @param image
   * @param vertically
   * @return
   */
  public static Image flip(Image image, boolean vertically) {
    int width = (int) image.getWidth();
    int height = (int) image.getHeight();

    // Convert the image to array
    int[][][] imageArray = toArray(image);
    int[][][] flippedImgArray = new int[width][height][3];

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        if (vertically)
          flippedImgArray[x][y] = imageArray[x][height - y - 1];
        else
          flippedImgArray[x][y] = imageArray[width - x - 1][y];
      }
    }

    // Convert the array to Image and return
    return fromArray(flippedImgArray);
  }

  /**
   * Image Negative filter First converts the input Image object to a 3D integer
   * array then performs the negative operation on the array finally converts the
   * modified array back to an Image object
   * 
   * @param img the original image to be filtered
   * @return the result of the filter operation
   */
  public static Image negative(Image image) {
    int width = (int) image.getWidth();
    int height = (int) image.getHeight();

    // Convert the image to array
    int[][][] imageArray = toArray(image);

    // Image Negative Operation:
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        imageArray[x][y][1] = 255 - imageArray[x][y][1]; // r
        imageArray[x][y][2] = 255 - imageArray[x][y][2]; // g
        imageArray[x][y][3] = 255 - imageArray[x][y][3]; // b
      }
    }

    // Convert the array to Image and return
    return fromArray(imageArray);
  }

  /**
   * Convert the Image object to a 3D integer array using a PixelReader to get
   * each pixel in [a,r,g,b] format
   * 
   * @param image the image to be converted
   * @return the image converted to a 3D int array
   */
  private static int[][][] toArray(Image image) {
    if (image == null) {
      throw new IllegalArgumentException("image is null");
    }
    final PixelReader reader = image.getPixelReader();
    if (reader == null) {
      throw new IllegalStateException("Pixel reader not available..");
    }

    int width = (int) image.getWidth();
    int height = (int) image.getHeight();

    int[][][] result = new int[width][height][4];

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int p = reader.getArgb(x, y);
        int a = (p >> 24) & 0xff;
        int r = (p >> 16) & 0xff;
        int g = (p >> 8) & 0xff;
        int b = p & 0xff;

        result[x][y][0] = a;
        result[x][y][1] = r;
        result[x][y][2] = g;
        result[x][y][3] = b;
      }
    }
    return result;
  }

  /**
   * Convert the 3d integer array to an Image object using a PixelWriter to create
   * a new WritableImage object and set each pixel (p) corresponding to the array
   * item.
   * 
   * @param imgArray 3D integer array to be converted
   * @return the resulting image object of the conversion
   */
  private static Image fromArray(int[][][] imgIntArray) {
    int width = imgIntArray.length;
    int height = imgIntArray[0].length;

    WritableImage newImg = new WritableImage(width, height);
    PixelWriter writer = newImg.getPixelWriter();

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        int a = imgIntArray[x][y][0];
        int r = imgIntArray[x][y][1];
        int g = imgIntArray[x][y][2];
        int b = imgIntArray[x][y][3];

        // set RGB value
        int p = (a << 24) | (r << 16) | (g << 8) | b;
        writer.setArgb(x, y, p);
      }
    }
    return newImg;
  }
}
