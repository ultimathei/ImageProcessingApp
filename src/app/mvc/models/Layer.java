package app.mvc.models;

import javafx.scene.image.Image;

/**
 * The layer object used in the layerstack, storing all the information about
 * the layer
 */
public class Layer {
  private static int idCount = 0;
  // a unique id for the layer - ever increasing
  private String id;
  // optional custom name for the layer
  private String name;
  // we keep the original image
  private Image baseImg;
  // and make a copy for the modificaitons (after 1+ alterations)
  private Image filteredImg;
  // the current result of the layer stack up to this layer
  private Image currentResultImg;
  
  // possibe other fields -  to store the modification amounts/flags
  // these would be needed so we can recalculate the image at any time
  private boolean negative;
  private boolean flippedX;
  private boolean flippedY;
  private int pixelShiftAmount;
  private double pixelScaleAmount;
  private double posOffsetX;
  private double posOffsetY;

  // -- CONSTRUCTORs --
  public Layer(Image baseImage) {
    init(baseImage);
  }
  public Layer(Image baseImage, String name) {
    init(baseImage);
    this.name = name;
  }

  // initialiser
  private void init(Image baseImage) {
    id = Layer.generateNextId();
    baseImg = baseImage;
    filteredImg = baseImage;
    negative = false;
    pixelShiftAmount = 0;
    pixelScaleAmount = 1.0;
    flippedX = false;
    flippedY = false;
    posOffsetX = 0.0;
    posOffsetY = 0.0;
  }

  // -- GETTER/SETTERS --
  public Image getBaseImg(){
    return baseImg;
  }
  public Image getFilteredImg(){
    return filteredImg;
  }
  public Image setFilteredImg(Image img){
    filteredImg = img;
    return filteredImg;
  }
  public Image getCurrentResultImg(){
    return currentResultImg;
  }
  public Image setCurrentResultImg(Image img){
    currentResultImg = img;
    return currentResultImg;
  }
  public String getId() {
    return id;
  }
  public String setId(String id){
    this.id = id;
    return this.id;
  }
  public String getName(){
    return name;
  }
  public boolean setName(String s) {
    name = s;
    return true;
  }


  // ARITHMETIC OPERATIONS ? here or in controller!
  // 
  // public Image add(Image toAdd){
  //   // perform arithmetic addition here
  //   // setCurrentresultImg to result of operation
  //   Image result = toAdd;
  //   return result;
  // }

  // -- STATIC METHODS --

  // generate a unique (per file) id for the layer
  public static String generateNextId() {
    return "layer_"+(idCount++);
  }
}
