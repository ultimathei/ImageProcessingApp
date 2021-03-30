package app.mvc.models;

import java.util.ArrayList;

import javafx.scene.image.Image;

public class LayerStack {
  private static LayerStack instance;
  private ArrayList<Layer> stack;

  private LayerStack() {
    stack = new ArrayList<>();
  }

  public static LayerStack getInstance() {
    if (instance == null)
      instance = new LayerStack();
    return instance;
  }

  public int size() {
    return stack.size();
  }

  public Layer get(int i) {
    return stack.get(i);
  }

  public boolean add(Layer layer) {
    // also calculate the currentresult?
    return stack.add(layer);
  }

  public Layer set(int index, Layer layer) {
    return stack.set(index, layer);
  }

  // recursively upwards
  public Image updateImage(int index, Image image) {
    if(index == stack.size()) return stack.get(index).add(image);
    Layer nextLayer = stack.get(index+1);
    return nextLayer.add(updateImage(index+1, nextLayer.getCurrentResultImg()));
  }
}
