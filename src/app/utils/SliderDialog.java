package app.utils;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
  private ButtonType setBtnType;
  private ButtonType closeBtnType;
  private SimpleDoubleProperty sliderValue;
  private Slider slider;
  private TextField numberField;

  public SliderDialog(Stage primaryStage, String title, double value, double min, double max, int decimals) {
    super();
    initOwner(primaryStage);
    initStyle(StageStyle.UNDECORATED);
    sliderValue = new SimpleDoubleProperty(value);

    root = new BorderPane();
    root.setTop(new Label(title));

    slider = new Slider();
    slider.setMin(min);
    slider.setMax(max);
    slider.setShowTickLabels(true);
    slider.setShowTickMarks(true);
    slider.setMinorTickCount(0);
    slider.setSnapToTicks(true);
    slider.valueProperty().bindBidirectional(sliderValue);
    slider.setValue(Util.round(value, decimals));
    slider.setMajorTickUnit(Math.pow(0.1, decimals));
    // slider.setBlockIncrement(1);

    numberField = new TextField();
    numberField.setText(Util.round(slider.getValue(), decimals) + "");
    numberField.textProperty().bind(sliderValue.asString());

    // numberField.textProperty()
    //     .addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
    //       if (!newValue.matches("\\d{0,3}([\\.]\\d{0,2})?")) {
    //         numberField.setText(oldValue);
    //       }
    //     });

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
        return Util.round((sliderValue.getValue()).doubleValue(), decimals);
      }
      return null;
    });
  }
}
