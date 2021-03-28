package app.utils;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SliderDialog extends Dialog<Double> {
  private BorderPane root;
  private DoubleProperty sliderValue;

  public SliderDialog(Stage primaryStage, String title, double value) {
    super();
    initOwner(primaryStage);
    initStyle(StageStyle.UNDECORATED);
    sliderValue = new SimpleDoubleProperty(value);
    

    root = new BorderPane();
    root.setTop(new Label(title));

    Slider slider = new Slider();
    slider.setMin(0.1);
    slider.setMax(2.0);
    slider.setValue(value);
    slider.setShowTickLabels(true);
    slider.setShowTickMarks(true);
    slider.setMajorTickUnit(0.1);
    slider.setMinorTickCount(1);
    slider.setBlockIncrement(0.1);
    slider.setSnapToTicks(true);
    slider.valueProperty().bindBidirectional(sliderValue);

    TextField numberField = new TextField();
    numberField.setText(slider.getValue() + "");
    numberField.textProperty().bind(sliderValue.asString("%.2f"));
    
    VBox vbox = new VBox(slider);
    vbox.setAlignment(Pos.CENTER);

    root.setTop(numberField);
    root.setCenter(vbox);
    getDialogPane().setContent(root);

    ButtonType setBtnType = new ButtonType("Set", ButtonBar.ButtonData.APPLY);
    ButtonType closeBtnType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
    getDialogPane().getButtonTypes().addAll(closeBtnType, setBtnType);

    this.setResultConverter(button -> {
      if(button == setBtnType)
        return Util.round(sliderValue.doubleValue(), 2);
      return null;
    });
  }
}
