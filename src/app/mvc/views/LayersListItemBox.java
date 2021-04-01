package app.mvc.views;

import app.mvc.models.Layer;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

class LayersListItemBox extends ListCell<Layer> {
  @Override
  public void updateItem(Layer item, boolean empty) {
    super.updateItem(item, empty);

    Text layerNameText = new Text();
    layerNameText.getStyleClass().addAll("layer__name");
    HBox box = new HBox();
    box.getStyleClass().addAll("layer");
    box.getChildren().addAll(layerNameText);

    // item exists
    if (!empty) {
      // populate the cell with graphic and/or text

      if(item.getName()!=null) 
        layerNameText.setText(item.getName());
      else 
        layerNameText.setText(item.getId());
      setGraphic(box);
    } else {
      setText(null);
      setGraphic(null);
    }
  }
}