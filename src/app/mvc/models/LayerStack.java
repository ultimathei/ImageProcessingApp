package app.mvc.models;

import app.App;
import app.utils.ConvertImage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

/**
 * An ordered list like structure to store a list of Layer objects To be used by
 * layersPane to create a stack of images then calculate the resulting images
 */
public class LayerStack {
  // the singleton instance
  private static LayerStack instance;
  // an ordered list of Layers (the position of the Layer is the z-index)
  private ObservableList<Layer> stack;
  // active index is the marker of what layer is currently selected as th active.
  // the active layer's original image is shown, and all the imeg processing
  // modifications
  // are relevant to that layer's images
  private int activeLayerIndex = -1;

  // -- PRIVATE CONSTRUCTOR --
  private LayerStack() {
    stack = FXCollections.observableArrayList();
  }

  // instance getter
  public static LayerStack getInstance() {
    if (instance == null)
      instance = new LayerStack();
    return instance;
  }

  public ObservableList<Layer> getStack() {
    return stack;
  }

  // getter for size of the stack
  public int size() {
    return stack.size();
  }

  // getter for layer at position i
  public Layer getLayer(int i) {
    if (i < 0 || i >= stack.size())
      return null;
    return stack.get(i);
  }

  // setting a layer object at i position - will be used for reordering?
  public Layer setLayer(int index, Layer layer) {
    return stack.set(index, layer);
  }

  // make active should be true for now, so the new layer is added to the top
  public Layer addLayer(Layer layer, boolean makeActive) {
    // add new layer to start of the list
    stack.add(0, layer);
    if (makeActive)
      activeLayerIndex = 0;
    // should also calculate the currentresult?
    App.LOGGER.log("Layer added, stack size now: " + stack.size());
    return layer;
  }

  // remove given layer from the stack
  public boolean removeLayer(Layer layer) {
    if (!stack.remove(layer))
      return false;
    // set top layer as active
    activeLayerIndex = 0;
    // should also update the currentresult for effected layers?
    return true;
  }

  // remove active (selected) layer from the stack
  public Layer removeLayer() {
    if (activeLayerIndex < 0)
      return null;
    return stack.remove(activeLayerIndex);
  }

  // getter for the currently active layer's index
  public int getActiveLayerIndex() {
    return activeLayerIndex;
  }

  public boolean setActiveLayerIndex(int i) {
    activeLayerIndex = i;
    return true;
  }

  // getter for the Layer at active position
  public Layer getActiveLayer() {
    if (activeLayerIndex < 0)
      return null;
    return stack.get(activeLayerIndex);
  }

  // what it says on the tin
  private int findLayerIndexFor(String id) {
    for (int i = 0; i < stack.size(); i++) {
      if (stack.get(i).getId().equals(id)) {
        return i;
      }
    }
    // no layer found with that id
    return -1;
  }

  // update index or return -1
  public boolean updateActiveLayerIndexById(String id) {
    int index = findLayerIndexFor(id);
    if (index > -1) {
      App.LOGGER.log("updating active index to: " + index);
      activeLayerIndex = index;
      return true;
    } else {
      return false;
    }
  }

  // get a given layer's current render, top: stack.size()-1
  public Image getStackRenderAt(int index, int forceUpdateFrom) {
    if (index < 0)
      return null;

    Layer layer = getLayer(index);
    if(layer==null){
      return null;
    }
    
    Image local = layer.getLocalRender();

    // already have a local render, return that one
    if (local != null && index >= forceUpdateFrom) {
      App.LOGGER2.log("1");
      return local;
    }

    // no local render yet, but layer with index exists
    if (index < stack.size() - 1) {
      App.LOGGER2.log("2: " + index);
      Image localRenderBelow = getStackRenderAt(index + 1, forceUpdateFrom); // recursive!
      if (localRenderBelow != null)
        return layer.updateLocalRender(localRenderBelow);
    }

    App.LOGGER2.log("3: " + index);
    return layer.setLocalRender(layer.getFilteredImg());
  }

  // MOVING LAYERS!
  //
  // // recursively upwards
  // public Image updateImage(int index, Image image) {
  // if(index == stack.size()) return stack.get(index).add(image);
  // Layer nextLayer = stack.get(index+1);
  // return nextLayer.add(updateImage(index+1, nextLayer.getLocalRender()));
  // }

  // public Layer updateActiveLayer(String id){
  // return setActiveLayer(id);
  // }
  // private Layer setActiveLayer(String id){
  // Layer activeLayer = findActiveLayer(id);
  // if(activeLayer==null) return null;
  // // do the swap here
  // int newIndex = getActiveLayerIndex();
  // int oldIndex = activeLayerIndex;
  // // only swap if oldindex exist (not a sole layer on the stack)
  // if(oldIndex > -1) Collections.swap(stack, newIndex, oldIndex);
  // activeLayerIndex = newIndex;
  // // update the layers in stack
  // // layers below the lower index are unchanged
  // // so start from lower index
  // int lowerIndex = activeLayerIndex < oldIndex ? activeLayerIndex : oldIndex;
  // for(int i=lowerIndex; i<stack.size(); i++) {
  // // update layer here by using the previous layer's currentresult and this
  // viewlayer
  // }
  // }
}
