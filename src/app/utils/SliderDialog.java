package app.utils;

import java.util.function.UnaryOperator;

import app.App;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Slider;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;

public class SliderDialog extends Dialog<Double> {

  public SliderDialog(Stage primaryStage, String title, double value, double min, double max, int decimals) {
    super();
    this.setTitle(title);
    initOwner(primaryStage);
    initStyle(StageStyle.UNDECORATED);
    BorderPane root = new BorderPane();

    StringConverter<Double> doubleStrConverter = new StringConverter<Double>() {
      @Override
      public String toString(Double d) {
        return String.format("%.2f", d);
      }

      @Override
      public Double fromString(String string) {
        return Double.valueOf(string);
      }
    };

    Text currentValue = new Text();

    Slider slider = new Slider();
    slider.setMin(min);
    slider.setMax(max);
    slider.setValue(value);
    slider.setShowTickLabels(true);
    slider.setShowTickMarks(true);
    slider.setMinorTickCount(10);
    slider.setSnapToTicks(true);
    slider.valueProperty().addListener((observable, oldValue, newValue)->
    currentValue.setText(doubleStrConverter.toString(newValue.doubleValue()))
    );
    slider.setMajorTickUnit(10*Math.pow(0.1, decimals));
    slider.setBlockIncrement(1.0);

    VBox vbox = new VBox(slider);
    vbox.setAlignment(Pos.CENTER);
    root.setTop(currentValue);
    root.setCenter(vbox);
    getDialogPane().setContent(root);

    ButtonType setBtnType = new ButtonType("Set", ButtonBar.ButtonData.APPLY);
    ButtonType closeBtnType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
    getDialogPane().getButtonTypes().addAll(closeBtnType, setBtnType);

    this.setResultConverter(button -> {
      if (button == setBtnType) {
        return doubleStrConverter.fromString(currentValue.getText());
      }
      return null;
    });
  }

  /**
   * Text formatter for string to double value
   * 
   * @param defaultValue
   * @return
   */
  private TextFormatter<Double> getTextFormatter(double defaultValue) {
    // validate text input as numeric value
    // check against all input: getControlNewText()
    UnaryOperator<TextFormatter.Change> numValidationFilter = change -> {
      App.LOGGER.log("here: " + change.getControlNewText());
      if (change.getText().matches("\\d+\\.\\d+")) {
        return change; // if change is a number (double)
      } else {
        change.setText(""); // else make no change
        change.setRange( // don't remove any selected text either.
            change.getRangeStart(), change.getRangeStart());
        return change;
      }
    };

    // stringConverter
    StringConverter<Double> doubleStrConverter = new StringConverter<Double>() {
      @Override
      public String toString(Double d) {
        return String.format("%02f", d);
      }

      @Override
      public Double fromString(String string) {
        return Double.valueOf(string);
      }
    };

    // formatter
    return new TextFormatter<>(doubleStrConverter, defaultValue, numValidationFilter);
  }
}
