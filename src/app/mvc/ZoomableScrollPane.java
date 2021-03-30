package app.mvc;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;

public class ZoomableScrollPane extends ScrollPane {
  Pane zoomGroup;
  Scale scaleTransform;
  Node content;

  public ZoomableScrollPane(Node content) {
    this.content = content;
    Pane contentGroup = new Pane();
    zoomGroup = new Pane();
    contentGroup.getChildren().add(zoomGroup);
    zoomGroup.getChildren().add(content);
    setContent(contentGroup);
    scaleTransform = new Scale(1, 1, 0, 0);
    zoomGroup.getTransforms().add(scaleTransform);
  }

  public void scale(double x, double y) {
    scaleTransform = new Scale(x, y, 0, 0);
    zoomGroup.getTransforms().add(scaleTransform);
  }

  public void scale(double x) {
    scaleTransform = new Scale(x, x, 0, 0);
    zoomGroup.getTransforms().add(scaleTransform);
  }
}
