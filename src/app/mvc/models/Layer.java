package app.mvc.models;

import javafx.scene.image.Image;

public class Layer {
  private Image baseImg;
  private Image filteredImg;
  private Image currentResultImg;
  private boolean negative;
  private boolean flippedX;
  private boolean flippedY;
  private int pixelShiftAmount;
  private double pixelScaleAmount;
  private double posOffsetX;
  private double posOffsetY;

  public Layer(Image baseImage) {
    baseImg = baseImage;
    filteredImg = baseImage;
    negative = false;
    pixelShiftAmount = 0;
    pixelScaleAmount = 1.0;
    flippedX = false;
    flippedY = false;
    // posOffsetX = 0.0;
    // posOffsetY = 0.0;
  }

  // -- GETTER/SETTERS --
  public Image getFilteredImg(){
    return filteredImg;
  }
  public Image setFilteredImg(Image img){
    filteredImg = img;
    return filteredImg;
  }
  public Image getBaseImg(){
    return baseImg;
  }
  public Image getCurrentResultImg(){
    return currentResultImg;
  }
  public Image setCurrentResultImg(Image img){
    currentResultImg = img;
    return currentResultImg;
  }

  // todo
  public Image add(Image toAdd){
    // perform arithmetic addition here
    // setCurrentresultImg to result of operation
    Image result = toAdd;
    return result;
  }
}
