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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
  private LayerStack layerstack;

  // -- SINGLETON CONSTRUCTOR --
  private Controller(boolean loadDefault) {
    registerServices();
    history = new LinkedList<>();
    historyIndex = -1;
    layerstack = LayerStack.getInstance();
    originator = new Originator();
    model = Model.INSTANCE;
    view = View.getInstance();
    view.getStylesheets().add("app/stylesheet.css");
    view.initLayerStackData(layerstack.getStack(), makeLayerPanelChangeListener());
    if (loadDefault)
      newLayerFromFile(model.getDefaultImagePath());
  }

  // singleton instance getter
  public static Controller getInstance() {
    if (instance == null)
      instance = new Controller(true);
    return instance;
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
  public boolean newLayerFromFile(String filePath) {
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

      String[] nameArray = selectedFile.getName().split("\\.", -1);
      String fileExtension = nameArray[nameArray.length - 1];
      String imageName = nameArray[nameArray.length - 2];

      // test if the javafx image was constructed correctly
      if (image.isError()) {
        // some file types are not supported by javafx's Image class
        image = ConvertImage.toJavaFxImage(selectedFile);
      }
      Layer addedLayer = new Layer(image, imageName, fileExtension);
      addedLayer = layerstack.addLayer(addedLayer, true);
      view.setSelectedLayer(addedLayer);
      return addNewLayer(addedLayer);
    } catch (Exception e) {
      App.LOGGER.log("Image not found or corrupt file");
      return false;
    }
  }

  /**
   * Set up the change event listener for the layer panel list changes.
   * 
   * @return teh listener - pass in initLayerStackdata() method
   */
  public ChangeListener<Layer> makeLayerPanelChangeListener() {
    return (ObservableValue<? extends Layer> ov, Layer oldVal, Layer newVal) -> {
      // switching activeitem
      if (newVal != oldVal) {
        if (newVal == null) {
          // no selected element -> the list is empty
          clearCanvas();
        } else {
          layerstack.updateActiveLayerIndexById(newVal.getId());
          selectNewLayer(newVal);
        }
      }
    };
  }

  /**
   * Updating the details in the info panel located at the top of the side panel
   * 
   * @param layer
   * @return
   */
  public boolean updateInfoPanel(Layer layer) {
    // update info panel here
    try {
      return view.updateInfoStack(layer);
    } catch (Exception e) {
      App.LOGGER.log("exception while updating info stack..");
      return false;
    }
  }

  /**
   * Setting the original image viewer's image. This always displays the current
   * active layer's image
   * 
   * @return back the newly set image
   */
  public boolean addNewLayer(Layer layer) {
    Image inModel = (layer == null) ? model.setImageOriginal(null) : model.setImageOriginal(layer.getBaseImg());
    return (view.setNewOriginalimage(inModel) && updateResultImage(layerstack.size()) && updateInfoPanel(layer));
  }

  public boolean selectNewLayer(Layer layer) {
    Image img = (layer.getBaseImg() != null) ? layer.getBaseImg() : null;
    Image inModel = model.setImageOriginal(img);
    return (view.setNewOriginalimage(inModel) && updateInfoPanel(layer));
  }

  /**
   * Deleting the active (selected) layer. When we delete an image, we also want
   * to update the localrenders of layers above the deleted layer
   * 
   * @return
   */
  public boolean deleteSelectedLayer() {
    Layer removedLayer = layerstack.removeLayer();
    return removedLayer != null && updateResultImage(layerstack.size());
  }

  public void clearCanvas() {
    layerstack.setActiveLayerIndex(-1);
    model.setImageOriginal(null);
    model.setImageResult(null);
    view.setNewOriginalimage(null);
    view.updateResultImage(null);
    view.updateInfoStack(null);
  }

  /**
   * Setting the filtered image viewer's image This always displays the current
   * active layer's image including the filters applied on it
   * 
   * @return back the newly set image
   */
  public boolean updateResultImage(int forceUpdateFrom) {
    // update effected local renders, so everythig from pos to 0
    // get the top layers render that is the final result
    Image result = layerstack.getStackRenderAt(0, forceUpdateFrom);
    Image inModel = model.setImageResult(result);
    return view.updateResultImage(inModel);
  }

  public boolean setTransparency(double amount) {
    return layerstack.getActiveLayer().setTransparency(amount) && updateResultImage(layerstack.size());
  }

  public boolean applyFilterShiftScale(int shift, double scale) {
    return layerstack.getActiveLayer().shiftScale1(shift, scale) && updateResultImage(layerstack.size());
  }

  public boolean applyFilterShiftScale2(int shift, double scale) {
    return layerstack.getActiveLayer().shiftScale2(shift, scale) && updateResultImage(layerstack.size());
  }

  public boolean lutLog() {
    return layerstack.getActiveLayer().lutLog() && updateResultImage(layerstack.size());
  }

  public boolean lutPow(int p) {
    return layerstack.getActiveLayer().lutPow(p) && updateResultImage(layerstack.size());
  }

  public void bitAnd() {
    layerstack.getActiveLayer().bitAnd(layerstack.getLayer(layerstack.getActiveLayerIndex()-1));
    updateResultImage(layerstack.size());
  }

  public void bitOr() {
    layerstack.getActiveLayer().bitOr(layerstack.getLayer(layerstack.getActiveLayerIndex()-1));
    updateResultImage(layerstack.size());
  }

  public void bitNot() {
    layerstack.getActiveLayer().bitNot();
    updateResultImage(layerstack.size());
  }

  public void bitXor() {
    layerstack.getActiveLayer().bitXor(layerstack.getLayer(layerstack.getActiveLayerIndex()-1));
    updateResultImage(layerstack.size());
  }

  /**
   * Update the active layer to be the one with given id if no layer is with id,
   * return false
   * 
   * @param id
   * @return
   */
  public boolean updateActiveLayerIndexById(String id) {
    return layerstack.updateActiveLayerIndexById(id);
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
    Image image = model.getImageResult();
    if (image == null)
      return false;
    Image newImg = ConvertImage.flip(image, false);
    return view.updateResultImage(model.setImageResult(newImg));
  }

  /**
   * 
   * @return
   */
  public boolean transformFlipVetical() {
    Image image = model.getImageResult();
    if (image == null)
      return false;
    Image newImg = ConvertImage.flip(image, true);
    return view.updateResultImage(model.setImageResult(newImg));
  }

  /**
   * 
   * @param scale
   * @return
   */
  public boolean transformResize(double scale) {
    double newScale = Util.clamp(scale, 0.1, 2.0);
    Image image = model.getImageResult();
    double currentScale = model.getCurrentScale();

    if (currentScale != newScale) {
      App.LOGGER.log("old height: " + image.getHeight());
      Image newImg = ConvertImage.resize(image, newScale / currentScale);
      App.LOGGER.log("new height: " + newImg.getHeight());
      model.setCurrentScale(newScale);
      return view.updateResultImage(model.setImageResult(newImg));
    }
    return false;
  }

  /**
   * 
   * @return
   */
  public boolean displayPixelShiftDialog() {
    if (model.getImageResult() == null)
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
    if (model.getImageResult() == null)
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
    if (model.getImageResult() == null)
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

  /**
   * 
   * @param scale
   * @return
   */
  public boolean pixelShift(double amount) {
    Image image = model.getImageResult();
    if (image == null)
      return false;
    int intAmount = ((Double) amount).intValue();
    Image newImg = ConvertImage.pixelShift(image, intAmount);
    return view.updateResultImage(model.setImageResult(newImg));
  }

  /**
   * 
   * @param scale
   * @return
   */
  public boolean pixelScale(double amount) {
    Image image = model.getImageResult();
    if (image == null)
      return false;
    Image newImg = ConvertImage.pixelScale(image, amount);
    return view.updateResultImage(model.setImageResult(newImg));
  }

  /**
   * 
   */
  public boolean filterNegative() {
    Layer activeLayer = layerstack.getActiveLayer();
    boolean isNegative = activeLayer.isNegative();
    Image image = activeLayer.getBaseImg();

    if (image == null) {
      image = activeLayer.getBaseImg();
      if (image == null)
        return false;
    }
    Image newImg = ConvertImage.negative(image);
    activeLayer.flipNegative();
    activeLayer.updateFilteredImage();
    boolean done = view.updateResultImage(model.setImageResult(newImg));
    if (done) {
      pushToHistory("Negative filter layer");
    }
    return done;
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

  // HISTORY
  public void pushToHistory(String actionName) {
    App.LOGGER2.log("pushing to history");
    originator.set(new Pair<>(actionName, new Pair<>(model, layerstack)));
    historyIndex++;
    history.add(originator.saveToMemento());
  }

  public boolean undo() {
    // set model to the one from history
    App.LOGGER2.log("history-index: " + historyIndex);
    if (historyIndex < 0)
      return false;
    Pair<Model, LayerStack> restoredState = originator.restoreFromMemento(history.get(historyIndex));
    // update model and layerstack and view
    model.updateModel(restoredState.getKey().getImageOriginal(), restoredState.getKey().getImageResult());
    layerstack = restoredState.getValue();
    selectNewLayer(layerstack.getActiveLayer());
    historyIndex--;
    return true;
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
