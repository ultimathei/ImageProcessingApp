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
public enum ConvertImage {
  INSTANCE;

  public static final int MIN_VALUE = 0;
  public static final int MAX_VALUE = 255;

  /**
   * Read in File as bufferedImage, then convert to type_int_rgb finally convert
   * to javafx image
   * 
   * @param selectedFile a File object to be converted to Image object
   * @return the file converted to javafx Image or null if there was an error
   *         during load or conversion
   */
  public static Image toJavaFxImage(File selectedFile) throws IOException {
    BufferedImage bi = ImageIO.read(selectedFile);
    int w = bi.getWidth();
    int h = bi.getHeight();

    BufferedImage biRGB = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    Graphics big = biRGB.getGraphics();
    big.drawImage(bi, 0, 0, null);

    return SwingFXUtils.toFXImage(biRGB, null);
  }

  /**
   * Re-scale all the pixel values of an image, stored in a matrix.
   * 
   * @param image
   * @param scalar
   * @return
   */
  public static Image pixelShift(Image image, int amount) {
    int width = (int) image.getWidth();
    int height = (int) image.getHeight();
    int[][][] imageArray = toArray(image);

    // pixel scaling operation
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        for (int i = 1; i <= 3; i++) {
          int val = imageArray[x][y][i] + amount;
          imageArray[x][y][i] = Util.clamp(val, 0, 255);
        }
      }
    }

    return fromArray(imageArray);
  }

  /**
   * Re-scale all the pixel values of an image, stored in a matrix.
   * 
   * @param image
   * @param scalar
   * @return
   */
  public static Image pixelScale(Image image, double scalar) {
    int width = (int) image.getWidth();
    int height = (int) image.getHeight();
    int[][][] imageArray = toArray(image);

    // pixel scaling operation
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        for (int i = 1; i <= 3; i++) {
          imageArray[x][y][i] = (int) Util.clamp(imageArray[x][y][i] * scalar, 0, 255);
        }
      }
    }

    return fromArray(imageArray);
  }

  /**
   * Resize the given image by a scalar factor using a naive nearest neighbour
   * algorithm.
   * 
   * @param image
   * @param scale
   * @return
   */
  public static Image resize(Image image, double scale) {
    int width = (int) image.getWidth();
    int height = (int) image.getHeight();
    int[][][] imageArray = toArray(image);

    int newWidth = (int) (width * scale);
    int newHeight = (int) (height * scale);

    // App.LOGGER.log("new height: "+newHeight);
    // App.LOGGER.log("new width: "+newWidth);

    int[][][] newImgArray = new int[newWidth][newHeight][4];

    // nearest neighbour
    for (int y = 0; y < newHeight; y++) {
      for (int x = 0; x < newWidth; x++) {
        int normalisedX;
        int normalisedY;
        if (scale < 1.0) {
          normalisedX = (int) (x / scale);
          normalisedY = (int) (y / scale);
        } else {
          normalisedX = (int) (x / scale);
          normalisedY = (int) (y / scale);
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

  // ARITHMETIC ADD
  /**
   * Arithmetic Addition of two images
   * 
   * @param base
   * @param addition
   * @return
   */
  public static Image pixelAdd(Image img_a, Image img_b) {
    // convert image to array
    int[][][] aArr = toArray(img_a);
    int[][][] bArr = toArray(img_b);
    // x, y size for both arrays
    int aX = aArr[0].length;
    int aY = aArr[1].length;
    int bX = bArr[0].length;
    int bY = bArr[1].length;
    // result width and height, and the non overlapping size
    int resX = Math.max(aX, bX);
    int resY = Math.max(aY, bY);
    // declaration fills with zeros
    int[][][] result = new int[resX][resY][4];

    // repeat with four channels
    for (int j = 0; j < aY; j++) {
      for (int i = 0; i < aX; i++) {
        // alpha channels of A and B
        int aA = aArr[i][j][0];
        int aB = bArr[i][j][0];
        // if not overlapping, then take the alpha of what is covered
        // alpha contribution of A and B
        int aA_contrib = (i > aX || j > aY) ? 0 : aA / 255;
        int aB_contrib = (i > bX || j > bY) ? 0 : aB / 255;
        // result alpha is the multiplication of the two source alphas
        result[i][j][0] = aA_contrib * aB_contrib * 255;
        // rgb channels with premultiplied alphas
        for (int k = 1; k < 4; k++) {
          result[i][j][k] += (i > aX || j > aY) ? 0 : aArr[i][j][k];
          result[i][j][k] += (i > bX || j > bY) ? 0 : bArr[i][j][k];
          result[i][j][k] = Util.clamp(result[i][j][k], MIN_VALUE, MAX_VALUE);
        }
      }
    }
    // convert back to image
    return fromArray(result);
  }

  /**
   * 
   * @param img_a
   * @param img_b
   * @return
   */
  public static Image normalAdd(Image img_a, Image img_b) {
    // convert image to array
    int[][][] aArr = toArray(img_a);
    int[][][] bArr = toArray(img_b);
    // x, y size for both arrays
    int aX = aArr[0].length;
    int aY = aArr[1].length;
    int bX = bArr[0].length;
    int bY = bArr[1].length;
    // result width and height, and the non overlapping size
    int resX = Math.max(aX, bX);
    int resY = Math.max(aY, bY);
    // declaration fills with default zero values
    int[][][] result = new int[resX][resY][4];

    // repeat with four channels
    for (int j = 0; j < aY; j++) {
      for (int i = 0; i < aX; i++) {
        for (int k = 1; k < 4; k++) {
          result[i][j][k] = (i > aX || j > aY) ? 0 : aArr[i][j][k];
          result[i][j][k] = (i > bX || j > bY) ? 0 : bArr[i][j][k];
          result[i][j][k] = Util.clamp(result[i][j][k], MIN_VALUE, MAX_VALUE);
        }
        result[i][j][0] = 255;
      }
    }
    // convert back to image
    return fromArray(result);
  }

  // from lecture notes
  public static Image shiftAndScale(Image image, int t, double s) {
    // convert image to array
    int width = (int) image.getWidth();
    int height = (int) image.getHeight();
    int[][][] imageArray = toArray(image);
    int[][][] resultArray = new int[width][height][4];

    // To shift by t and rescale by s and find the min and the max
    int rmin = (int) (s * (imageArray[0][0][1] + t));
    int rmax = rmin;
    int gmin = (int) (s * (imageArray[0][0][2] + t));
    int gmax = gmin;
    int bmin = (int) (s * (imageArray[0][0][3] + t));
    int bmax = bmin;
    for (int y = 0; y < resultArray[1].length; y++) {
      for (int x = 0; x < resultArray[0].length; x++) {
        resultArray[x][y][0] = imageArray[x][y][0]; // a
        resultArray[x][y][1] = (int) (s * (imageArray[x][y][1] + t)); // r
        resultArray[x][y][2] = (int) (s * (imageArray[x][y][2] + t)); // g
        resultArray[x][y][3] = (int) (s * (imageArray[x][y][3] + t)); // b
        if (rmin > resultArray[x][y][1]) {
          rmin = resultArray[x][y][1];
        }
        if (gmin > resultArray[x][y][2]) {
          gmin = resultArray[x][y][2];
        }
        if (bmin > resultArray[x][y][3]) {
          bmin = resultArray[x][y][3];
        }
        if (rmax < resultArray[x][y][1]) {
          rmax = resultArray[x][y][1];
        }
        if (gmax < resultArray[x][y][2]) {
          gmax = resultArray[x][y][2];
        }
        if (bmax < resultArray[x][y][3]) {
          bmax = resultArray[x][y][3];
        }
      }
    }
    for (int y = 0; y < resultArray[1].length; y++) {
      for (int x = 0; x < resultArray[0].length; x++) {
        resultArray[x][y][1] = 255 * (resultArray[x][y][1] - rmin) / (rmax - rmin);
        resultArray[x][y][2] = 255 * (resultArray[x][y][2] - gmin) / (gmax - gmin);
        resultArray[x][y][3] = 255 * (resultArray[x][y][3] - bmin) / (bmax - bmin);
      }
    }

    // convert back to image
    return fromArray(resultArray);
  }

  // from lecture notes
  public static Image shiftAndScale2(Image image, int t, double s) {
    // convert image to array
    int width = (int) image.getWidth();
    int height = (int) image.getHeight();
    int[][][] imageArray = toArray(image);
    int[][][] resultArray = new int[width][height][4];

    // To shift by t and rescale by s without finding the min and the max
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        resultArray[x][y][0] = imageArray[x][y][0]; // a
        resultArray[x][y][1] = (int) (s * (imageArray[x][y][1] + t)); // r
        resultArray[x][y][2] = (int) (s * (imageArray[x][y][2] + t)); // g
        resultArray[x][y][3] = (int) (s * (imageArray[x][y][3] + t)); // b
        if (resultArray[x][y][1] < 0) {
          resultArray[x][y][1] = 0;
        }
        if (resultArray[x][y][2] < 0) {
          resultArray[x][y][2] = 0;
        }
        if (resultArray[x][y][3] < 0) {
          resultArray[x][y][3] = 0;
        }
        if (resultArray[x][y][1] > 255) {
          resultArray[x][y][1] = 255;
        }
        if (resultArray[x][y][2] > 255) {
          resultArray[x][y][2] = 255;
        }
        if (resultArray[x][y][3] > 255) {
          resultArray[x][y][3] = 255;
        }
      }
    }

    // convert back to image
    return fromArray(resultArray);
  }

  public static Image setTransparency(Image image, double amount) {
    int width = (int) image.getWidth();
    int height = (int) image.getHeight();

    // Convert the image to array
    int[][][] imageArray = toArray(image);

    // Image Negative Operation:
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        // 1.0-amount
        imageArray[x][y][0] = (int) (amount * 255); // a
      }
    }

    // Convert the array to Image and return
    return fromArray(imageArray);
  }


  // for Look-Up Table LUT of 256 levels
  public static Image filterWithLUT(Image image, int[] LUT) {
    int width = (int) image.getWidth();
    int height = (int) image.getHeight();
    // Convert the image to array
    int[][][] imageArray = toArray(image);
    int[][][] resultArray = new int[width][height][4];
    int r, g, b;
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        r = imageArray[x][y][1]; // r
        g = imageArray[x][y][2]; // g
        b = imageArray[x][y][3]; // b
        resultArray[x][y][1] = LUT[r]; // r
        resultArray[x][y][2] = LUT[g]; // g
        resultArray[x][y][3] = LUT[b]; // b
      }
    }
    return fromArray(resultArray);
  }

  // UTILITIES
  // for generating a LUT of 256 levels for logarithmic function
  public static int[] generateLogLUT() {
    int[] LUT = new int[256];
    for (int k = 0; k < 256; k++) {
      LUT[k] = (int) (Math.log(1 + k) * 255 / Math.log(256));
    }
    return LUT;
  }

  // for generating a LUT of 256 levels for power law (p)
  public static int[] generatePowLUT(int p) {
    int[] LUT = new int[256];
    for (int k = 0; k < 256; k++) {
      LUT[k] = (int) (Math.pow(255, 1 - p) * Math.pow(k, p));
    }
    return LUT;
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
