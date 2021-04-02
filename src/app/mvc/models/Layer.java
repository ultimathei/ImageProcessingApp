package app.mvc.models;

import app.App;
import app.utils.ConvertImage;
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
  // store the original file extension in case we need it at export
  private String fileExtension;
  // we keep the original image
  private Image baseImg;
  // and make a copy for the modificaitons (after 1+ alterations)
  private Image filteredImg;
  // the current result of the layer stack up to this layer
  private Image localRender;

  // possibe other fields - to store the modification amounts/flags
  // these would be needed so we can recalculate the image at any time
  private double transparency;
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

  public Layer(Image baseImage, String name, String fileExtension) {
    init(baseImage);
    if (name != null)
      this.name = name;
    this.fileExtension = fileExtension;
  }

  // initialiser
  private void init(Image baseImage) {
    id = Layer.generateNextId();
    baseImg = baseImage;
    filteredImg = baseImage;
    negative = false;
    transparency = 1.0;
    pixelShiftAmount = 0;
    pixelScaleAmount = 1.0;
    flippedX = false;
    flippedY = false;
    posOffsetX = 0.0;
    posOffsetY = 0.0;
  }

  // INSTANCE METHODS
  public Image updateFilteredImage() {
    if (baseImg == null)
      return null;

    // negation first
    if (filteredImg == null)
      setFilteredImg(getBaseImg());
    return setFilteredImg(ConvertImage.negative(baseImg));
  }

  // -- FILTERS --

  public Image filterNegative() {
    return setFilteredImg(ConvertImage.negative(filteredImg));
  }

  public boolean setTransparency(double amount) {
    if (amount < 0.0 || amount > 1.0)
      return false;
    filteredImg = ConvertImage.setTransparency(getFilteredImg(), amount);
    transparency = amount;
    return true;
  }

  public boolean shiftScale1(int shift, double scale) {
    int validShift = (shift < -256) ? -256 : (shift > 255) ? 255 : shift;
    double validScale = (scale < 0.0) ? 0.0 : (scale > 2.0) ? 2.0 : scale;
    // filteredImg = ConvertImage.shiftAndScale2(getFilteredImg(), validShift,
    // validScale);
    filteredImg = ConvertImage.shiftAndScale(getFilteredImg(), validShift, validScale);
    return true;
  }

  public boolean shiftScale2(int shift, double scale) {
    int validShift = (shift < -256) ? -256 : (shift > 255) ? 255 : shift;
    double validScale = (scale < 0.0) ? 0.0 : (scale > 2.0) ? 2.0 : scale;
    filteredImg = ConvertImage.shiftAndScale2(getFilteredImg(), validShift, validScale);
    return true;
  }

  // -- endof FILTERS --

  // -- GETTER/SETTERS --
  public Image getBaseImg() {
    return baseImg;
  }

  public Image setBaseImage(Image img) {
    baseImg = img;
    return baseImg;
  }

  public Image getFilteredImg() {
    return filteredImg;
  }

  public Image setFilteredImg(Image img) {
    filteredImg = img;
    return filteredImg;
  }

  public Image getLocalRender() {
    return localRender;
  }

  public Image setLocalRender(Image img) {
    localRender = img;
    return localRender;
  }

  public Image updateLocalRender(Image renderOfLayerBelow) {
    App.LOGGER2.log("in update local render");
    // return ConvertImage.pixelAdd(renderOfLayerBelow, filteredImg);
    return ConvertImage.normalAdd(renderOfLayerBelow, filteredImg);
  }

  public String getId() {
    return id;
  }

  public String setId(String id) {
    this.id = id;
    return this.id;
  }

  public String getName() {
    return name;
  }

  public boolean setName(String s) {
    name = s;
    return true;
  }

  public String getFileExtension() {
    return fileExtension;
  }

  public boolean setFileExtension(String s) {
    fileExtension = s;
    return true;
  }

  public boolean isNegative() {
    return negative;
  }

  public boolean setNegative(boolean newVal) {
    negative = newVal;
    return negative;
  }

  public boolean flipNegative() {
    negative = !negative;
    return negative;
  }

  // ARITHMETIC OPERATIONS ? here or in controller!
  //
  // public Image add(Image toAdd){
  // // perform arithmetic addition here
  // // setLocalRender to result of operation
  // Image result = toAdd;
  // return result;
  // }

  // -- STATIC METHODS --

  // generate a unique (per file) id for the layer
  public static String generateNextId() {
    return "layer_" + (idCount++);
  }
}
