package app.mvc.views;

import app.App;
import app.mvc.models.ColorPalette;
import app.mvc.models.Layer;
import javafx.geometry.Insets;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

class LayersListItemBox extends ListCell<Layer> {
  @Override
  public void updateItem(Layer item, boolean empty) {
    super.updateItem(item, empty);

    Text layerNameText = new Text();
    HBox box = new HBox();
    HBox.setMargin(layerNameText, new Insets(8, 10, 8, 10));
    box.setBorder(new Border(new BorderStroke(Color.web(ColorPalette.DARK_GREY), BorderStrokeStyle.SOLID,
        CornerRadii.EMPTY, new BorderWidths(0.25))));
    box.getChildren().addAll(layerNameText);

    // item exists
    if (!empty) {
      // populate the cell with graphic and/or text
      layerNameText.setText(item.getId());
      setGraphic(box);
    } else {
      setText(null);
      setGraphic(null);
    }
  }
}