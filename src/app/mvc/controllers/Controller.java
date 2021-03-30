package app.mvc.controllers;

import app.App;
import app.utils.ConvertImage;
import app.utils.SliderDialog;
import app.utils.Util;
import app.services.memento.Originator;
import app.mvc.models.*;
import app.mvc.models.Layer;
import app.mvc.models.LayerStack;
import app.mvc.views.View;
import app.services.actions.*;
import app.services.events.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Pair;

/**
 * Singleton class to represent the controller (of MVC) of the Application. Also
 * being used as the Caretaker for the memento pattern.
 */
public class Controller implements ImageManipulationController {
  private static Controller instance = null;
  private Stage mainStage;
  private Model model;
  private View view;
  private List<Originator.Memento> history;
  private int historyIndex;
  private Originator originator;
  // the currently selected layer on the layer stack panel
  private int activeLayerIndex; // update this with the layer component?
  private LayerStack layerstack;

  // -- SINGLETON CONSTRUCTOR --
  private Controller() {
    registerServices();
    history = new LinkedList<>();
    historyIndex = 0;
    activeLayerIndex = 0;
    layerstack = LayerStack.getInstance();
    originator = new Originator();
    model = Model.INSTANCE;
    view = View.getInstance();
  }

  // singleton instance getter
  public static Controller getInstance() {
    if (instance == null)
      instance = new Controller();
    return instance;
  }

  /**
   * FILTER method
   * 
   * @return
   */
  public boolean filter() {
    if (layerstack.size() < 1)
      return false;
    Layer layer = layerstack.get(activeLayerIndex);
    if (layer == null)
      return false;
    Image img = layer.getFilteredImg();
    if (img == null)
      return false;

    // testing only with negative filter now
    Image newImg = ConvertImage.negative(img);

    // should save previous filtered state to history?
    Image result = layerstack.updateImage(activeLayerIndex, newImg);
    model.setResultImage(result);

    return true;
  }

  /**
   * LOAD FILE from path
   * 
   * @return
   */
  public boolean loadDefaultImage() {
    return openImage("/Users/ultimathei/macDocs/code_local/ImageProcessingApp/src/images/Barbara.bmp");
  }

  /**
   * Open image file using a file chooser component. If the file was openned
   * successfully, an Image object is created with it, then the original file and
   * filtered file is updated in the model, then the new image is passed onto the
   * view to update the imageviews.
   * 
   * @param filePath an optional argument for the relative path to the file
   * @return true if the open and the view update was successful false otherwise
   */
  public boolean openImage(String filePath) {
    File selectedFile;
    Image image;

    try {
      if (filePath != null) {
        selectedFile = new File(filePath);
      } else {
        // get the extensions from model, append *. to them and make a list
        ArrayList<String> extensions = new ArrayList<>();
        for (String ext : model.getFormats()) {
          extensions.add("*." + ext);
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open an image file");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Image Files", extensions));
        selectedFile = fileChooser.showOpenDialog(view.getWindow());
      }

      image = new Image(new FileInputStream(selectedFile));

      // test if the javafx image was not constructed correctly
      if (image.isError()) {
        // some file types are not supported by javafx's Image class
        image = ConvertImage.toJavafx(selectedFile);
      }

      view.setNewOriginalimage(model.setImageFiltered(model.setImageOriginal(image)));
      String fileName = selectedFile.getName();
      String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
      model.setOriginalImgType(fileExtension);

      // layerstack try
      // layerstack.add(new Layer(image));

      return true;
    } catch (Exception e) {
      App.LOGGER.log("Image not found or corrupt file");
      return false;
    }
  }

  /**
   * Check if the application is safe to quit (from menu quit)
   * 
   * @return
   */
  public boolean tryQuit() {
    // later decide if you want to display a warning instead (unsaved file for ex.)
    mainStage.fireEvent(new WindowEvent(mainStage, WindowEvent.WINDOW_CLOSE_REQUEST));
    return true;
  }

  /**
   * Close application event handler
   * 
   * @param event
   * @return
   */
  public boolean closeWindowEventHandler(WindowEvent event) {
    App.LOGGER.log("Wants to close the window..");

    if (model.getHasChanged()) {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.getButtonTypes().remove(ButtonType.OK);
      alert.getButtonTypes().add(ButtonType.CANCEL);
      alert.getButtonTypes().add(ButtonType.YES);
      // add save button here..
      // ..
      alert.setTitle("Closing with unsaved changes");
      alert.setContentText("Close without saving?");
      alert.initOwner(view.getWindow());

      Optional<ButtonType> res = alert.showAndWait();

      if (res.isPresent()) {
        if (res.get().equals(ButtonType.CANCEL)) {
          App.LOGGER.log("Returning to the app");
          event.consume();
        } else {
          App.LOGGER.log("Closing app without saving. Good bye!");
          return false;
        }

        // add save option here
        // ..
      }
    } else {
      App.LOGGER.log("Closing app. Good bye!");
      return true;
    }
    return false;
  }

  // -- IMAGE MANIPULATION --

  /**
   * 
   * @return
   */
  public boolean transformFlipHorizontal() {
    Image image = model.getImageFiltered();
    if (image == null)
      return false;
    Image newImg = ConvertImage.flip(image, false);
    return view.updateFilteredImage(model.setImageFiltered(newImg));
  }

  /**
   * 
   * @return
   */
  public boolean transformFlipVetical() {
    Image image = model.getImageFiltered();
    if (image == null)
      return false;
    Image newImg = ConvertImage.flip(image, true);
    return view.updateFilteredImage(model.setImageFiltered(newImg));
  }

  /**
   * 
   * @param scale
   * @return
   */
  public boolean transformResize(double scale) {
    double newScale = Util.clamp(scale, 0.1, 2.0);
    Image image = model.getImageFiltered();
    double currentScale = model.getCurrentScale();

    if (currentScale != newScale) {
      App.LOGGER.log("old height: " + image.getHeight());
      Image newImg = ConvertImage.resize(image, newScale / currentScale);
      App.LOGGER.log("new height: " + newImg.getHeight());
      model.setCurrentScale(newScale);
      return view.updateFilteredImage(model.setImageFiltered(newImg));
    }
    return false;
  }

  /**
   * 
   * @return
   */
  public boolean displayPixelShiftDialog() {
    if (model.getImageFiltered() == null)
      return false;
    SliderDialog dialog = new SliderDialog(mainStage, "Pixel shift amount", 1, -255.0, 255.0, 0);
    Optional<Double> result = dialog.showAndWait();
    try {
      result.ifPresent(this::pixelShift);
      return dialog.getResult() != null;
    } catch (Exception e) {
      App.LOGGER.log("error in shifter !!!" + e.getMessage());
    }
    return false;
  }

  /**
   * 
   * @return
   */
  public boolean displayPixelScaleDialog() {
    if (model.getImageFiltered() == null)
      return false;
    SliderDialog dialog = new SliderDialog(mainStage, "Pixel scale amount", 1.0, 0.1, 2.0, 2);
    Optional<Double> result = dialog.showAndWait();
    try {
      result.ifPresent(this::pixelScale);
      return dialog.getResult() != null;
    } catch (Exception e) {
      App.LOGGER.log(e.getMessage());
    }
    return false;
  }

  /**
   * 
   * @return
   */
  public boolean displayResizeDialog() {
    if (model.getImageFiltered() == null)
      return false;
    SliderDialog dialog = new SliderDialog(mainStage, "Scale amount", model.getCurrentScale(), 0.1, 2.0, 2);
    Optional<Double> result = dialog.showAndWait();
    try {
      result.ifPresent(this::transformResize);
      return dialog.getResult() != null;
    } catch (Exception e) {
      App.LOGGER.log(e.getMessage());
    }
    return false;
  }

  // /**
  //  * NOT USED CURRENTLY
  //  * @param consumer
  //  * @return
  //  */
  // public boolean displayDoubleDialog(Consumer<? super Double> consumer) {
  //   if (model.getImageFiltered() == null)
  //     return false;

  //   SliderDialog dialog = new SliderDialog(mainStage, "Scale amount", model.getCurrentScale(), 0.1, 2.0, 2);
  //   Optional<Double> result = dialog.showAndWait();
  //   try {
  //     result.ifPresent(consumer);
  //     return dialog.getResult() != null;
  //   } catch (Exception e) {
  //     App.LOGGER.log(e.getMessage());
  //   }
  //   return false;
  // }

  /**
   * 
   * @param scale
   * @return
   */
  public boolean pixelShift(double amount) {
    Image image = model.getImageFiltered();
    if (image == null)
      return false;
    int intAmount = ((Double) amount).intValue();
    Image newImg = ConvertImage.pixelShift(image, intAmount);
    return view.updateFilteredImage(model.setImageFiltered(newImg));
  }

  /**
   * 
   * @param scale
   * @return
   */
  public boolean pixelScale(double amount) {
    Image image = model.getImageFiltered();
    if (image == null)
      return false;
    Image newImg = ConvertImage.pixelScale(image, amount);
    return view.updateFilteredImage(model.setImageFiltered(newImg));
  }

  /**
   * 
   */
  public boolean filterNegative() {
    Image image = model.getImageFiltered();
    if (image == null)
      return false;
    Image newImg = ConvertImage.negative(image);
    return view.updateFilteredImage(model.setImageFiltered(newImg));
  }

  // ZOOM

  public boolean zoomIn() {
    view.zoomInCanvas();
    return true;
  }

  public boolean zoomOut() {
    view.zoomOutCanvas();
    return true;
  }

  public boolean zoomReset() {
    view.resetZoomCanvas();
    return true;
  }

  /**
   * Preparing the main stage of the app
   * 
   * @param primaryStage
   */
  public void setMainStage(Stage primaryStage) {
    mainStage = primaryStage;
    mainStage.setTitle(App.APP_NAME);
    mainStage.setScene(view);
    mainStage.setMinHeight(model.getViewerHeight());
    mainStage.setMinWidth(model.getViewerWidth());
    mainStage.setResizable(true);
    mainStage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, this::closeWindowEventHandler);
  }

  // HISTORY -- NOT SURE YET
  public void pushToHistory(String actionName) {
    originator.set(new Pair<>(actionName, model));
    history.add(originator.saveToMemento());
    historyIndex++;
  }

  public void undo() {
    // set model to the one from history
    originator.restoreFromMemento(history.get(historyIndex));
  }

  // END of HISTORY :)

  // -- PRIVATE METHODS --

  /**
   * Registers the services/events This is needed so all (and only the registered)
   * events in the app will be recognized.
   */
  private void registerServices() {
    ServiceLocator.INSTANCE.registerService(EventBus.class, EventBusProvider.class);
    ServiceLocator.INSTANCE.registerService(ImageAction.class, ImageActionProvider.class);
    ServiceLocator.INSTANCE.registerService(FileAction.class, FileActionProvider.class);
    ServiceLocator.INSTANCE.registerService(EditAction.class, EditActionProvider.class);
  }

  // -- GETTERS/SETTERS --

  /**
   * The only accesspoint to view is through the controller.
   * 
   * @return the singleton view object
   */
  public View getView() {
    return view;
  }

  /**
   * Change the currently active layer index
   * 
   * @param index
   */
  public void setActiveIndex(int index) {
    activeLayerIndex = index;
  }

  /**
   * 
   * @param state
   */
  public void setHasChanged(boolean state) {
    model.setHasChanged(state);
  }

  /**
   * 
   * @param scale
   */
  public void setCurrentScale(double scale) {
    model.setCurrentScale(scale);
  }
}
