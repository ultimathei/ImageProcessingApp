package app.utils;

import javafx.beans.property.SimpleObjectProperty;
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

public class SliderDialog<T extends Number> extends Dialog<T> {
  private BorderPane root;
  private ButtonType setBtnType;
  private ButtonType closeBtnType;
  private SimpleObjectProperty<T> sliderValue;
  private Slider slider;
  private TextField numberField;

  public SliderDialog(Stage primaryStage, String title, T value) {
    super();
    initOwner(primaryStage);
    initStyle(StageStyle.UNDECORATED);
    sliderValue = new SimpleObjectProperty<>(value);

    root = new BorderPane();
    root.setTop(new Label(title));

    slider = new Slider();
    slider.setMin(0.1);
    slider.setMax(2.0);
    slider.setShowTickLabels(true);
    slider.setShowTickMarks(true);
    slider.setMinorTickCount(1);
    slider.setBlockIncrement(0.1);
    slider.setSnapToTicks(true);
    slider.valueProperty().bindBidirectional((SimpleObjectProperty<Number>) sliderValue);
    makeSpecificities(value);

    numberField = new TextField();
    numberField.setText(slider.getValue() + "");
    numberField.textProperty().bind(sliderValue.asString("%.2f"));

    VBox vbox = new VBox(slider);
    vbox.setAlignment(Pos.CENTER);

    root.setTop(numberField);
    root.setCenter(vbox);
    getDialogPane().setContent(root);

    setBtnType = new ButtonType("Set", ButtonBar.ButtonData.APPLY);
    closeBtnType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
    getDialogPane().getButtonTypes().addAll(closeBtnType, setBtnType);
    
    this.setResultConverter(button -> {
      if (button == setBtnType) {
        return (T) (Double) Util.round((Double) sliderValue.getValue(), 2);
      }
      return null;
    });
  }

  private void makeSpecificities(T value) throws ClassCastException {
    try{
      if (value instanceof Double) {
        slider.setValue((Double) value);
        slider.setMajorTickUnit(0.1);
      } else if (value instanceof Integer) {
        slider.setValue((int) value);
        slider.setMajorTickUnit(1);
        // numberField.textProperty().bind(sliderValue.asString("%d"));
      }
    }catch (Exception e) {
      //
    }
  }
}
