package app.mvc.views;

import app.App;
import app.mvc.models.*;
import app.services.actions.*;
import app.services.events.*;
import app.utils.*;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Singleton class to represent the view (of MVC) of the application
 */
public class View extends Scene {
    private static View instance = null;
    private EventBus eventBus;
    private static BorderPane root = new BorderPane();
    private Model model;

    private MenuBar menuBar;
    private StackPane infoStack;
    // private ScrollPane viewPort;
    private SplitPane viewPort;
    private ImageView originalImageView;
    private ImageView resultImageView;
    private VBox sidePane;
    private ListView<Layer> layersList;
    private int intPow = 0;

    // singleton -- private constructor
    private View() {
        super(root);
        eventBus = ServiceLocator.INSTANCE.getService(EventBus.class);
        ServiceLocator.INSTANCE.getService(ImageAction.class);
        ServiceLocator.INSTANCE.getService(FileAction.class);
        ServiceLocator.INSTANCE.getService(EditAction.class);

        // add menu actions...
        // get model instance
        model = Model.INSTANCE;

        originalImageView = new ImageView() {
            @Override
            public void requestFocus() {
                // disabling focus
            }
        };
        resultImageView = new ImageView() {
            @Override
            public void requestFocus() {
                // disabling focus
            }

        };

        infoStack = new StackPane();
        infoStack.setId("info-stack");

        // initialise
        menuBar = makeMenuBar(IdSelectors.MENU, model.getMenuStructure());
        viewPort = makeSplitPane(IdSelectors.CANVAS);
        sidePane = makeSidePane(IdSelectors.SIDE_PANE);

        // add to root
        root.setTop(menuBar);
        root.setCenter(viewPort);
        root.setRight(sidePane);
    }

    // singletonn instance
    public static View getInstance() {
        if (instance == null)
            instance = new View();
        return instance;
    }

    // assign data and listener to layers list
    public boolean initLayerStackData(ObservableList<Layer> stack, ChangeListener<? super Layer> listener) {
        layersList.setItems(stack);
        layersList.setCellFactory(input -> new LayersListItemBox());
        layersList.getSelectionModel().selectedItemProperty().addListener(listener);
        // setSelectedLayer(0);

        return true;
    }

    // allowing controller to set active list item to sync up with activeIndex
    public void setSelectedLayer(Layer layer) {
        layersList.getSelectionModel().select(layer);
        // layersList.getSelectionModel().select(i);
    }

    // -- PUBLIC INSTANCE METHODS --

    /**
     * Updating the image in the image view for active object (original image of the
     * active layer)
     * 
     * @return true if successfully updated, false otherwise
     */
    public boolean setNewOriginalimage(Image image) {
        App.LOGGER.log("update canvas here..");
        try {
            originalImageView.setImage(image);
            return true;
        } catch (Exception e) {
            App.LOGGER.log("Error while loading new image, error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update the result image with a new, filtered version of the image or a
     * completely new image (when new original is loaded)
     * 
     * @param image the new image
     * @return true if successfully updated, false otherwise
     */
    public boolean updateResultImage(Image image) {
        App.LOGGER.log("updating filtered image here..");
        try {
            // updtaing here..
            resultImageView.setImage(image);
            return true;
        } catch (Exception e) {
            App.LOGGER.log("Error while updating filtered image");
            return false;
        }
    }

    public boolean clearCanvas() {
        originalImageView.setImage(null);
        resultImageView.setImage(null);
        return true;
    }

    // ASSEMBLE UI ELEMENTS

    /**
     * Assemble the side panel of the app
     * 
     * @param id String that is used as the id of the component
     * @return a BorderPane object
     */
    private VBox makeSidePane(String id) {
        VBox pane = new VBox();
        pane.setId(id);
        pane.setPrefWidth(model.getSidePaneWidth());
        BorderPane info = makeAppComponent(IdSelectors.INFO_PANE, "Layer Info", infoStack);
        BorderPane controls = makeAppComponent(IdSelectors.CONTROLS_PANE, "Controls/effects",
                makeControlsPanel("controls-new"));
        BorderPane layersPane = makeLayersPane(IdSelectors.LAYERS_PANE);
        BorderPane layers = makeAppComponent("layers", "Layers", layersPane);
        VBox.setVgrow(layers, Priority.ALWAYS);

        pane.getChildren().addAll(info, controls, layers);
        return pane;
    }

    /**
     * REfreshing the info panel to always show the active (selected) layer's
     * details
     * 
     * @param layer
     * @return
     */
    public boolean updateInfoStack(Layer layer) {
        infoStack.getChildren().setAll(makeInfoPane(layer));
        return true;
    }

    /**
     * Info grid panel for the active layer information
     * 
     * @param layer
     * @return
     */
    public GridPane makeInfoPane(Layer layer) {
        GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add("info");
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        if (layer != null) {
            Slider shiftLevel = new Slider(-256, 255, 0);
            Label shiftValue = new Label(Double.toString(shiftLevel.getValue()));
            Slider scaleLevel = new Slider(0.0, 2.0, 1.0);
            Label scaleValue = new Label(Double.toString(scaleLevel.getValue()));
            Slider transparencyLevel = new Slider(0.0, 1.0, 1.0);
            Label transparencyValue = new Label(Double.toString(transparencyLevel.getValue()));
            // set values from layer
            int row = 0;
            gridPane.setId("info_" + layer.getId());
            gridPane.add(new Text("Layer id: "), 0, row);
            gridPane.add(new Text(layer.getId()), 1, row);
            row++;

            gridPane.add(new Text("Layer name: "), 0, row);
            String name = (layer.getName() != null) ? layer.getName() : "not set";
            gridPane.add(new Text(name), 1, row);
            row++;

            gridPane.add(new Text("File extension: "), 0, row);
            String extension = (layer.getFileExtension() != null) ? layer.getFileExtension() : "not set";
            gridPane.add(new Text(extension), 1, row);
            row++;

            gridPane.add(new Label("Transparency: "), 0, row);
            transparencyLevel.valueProperty().set(layer.getTransparency());
            gridPane.add(transparencyValue, 1, row);
            transparencyLevel.valueProperty().addListener((ObservableValue<? extends Number> ov, Number old_val,
                    Number new_val) -> transparencyValue.setText(String.format("%2f", new_val)));
            GridPane.setColumnSpan(transparencyLevel, 3);
            row++;
            gridPane.add(transparencyLevel, 0, row);
            row++;

            Button transBtn = new Button("Apply transparency");
            transBtn.setOnAction(event -> eventBus.fireEvent(
                    new AppEvent(AppEvent.SET_TRANSPARENCY, transparencyLevel.valueProperty().doubleValue())));
            GridPane.setColumnSpan(transBtn, 3);
            gridPane.add(transBtn, 0, row);
            row++;

            gridPane.add(new Label("Shift value: "), 0, row);
            gridPane.add(shiftValue, 1, row);
            shiftLevel.valueProperty().addListener((ObservableValue<? extends Number> ov, Number old_val,
                    Number new_val) -> shiftValue.setText(String.format("%2f", new_val)));
            GridPane.setColumnSpan(shiftLevel, 3);
            row++;
            gridPane.add(shiftLevel, 0, row);
            row++;

            gridPane.add(new Label("Scale value: "), 0, row);
            gridPane.add(scaleValue, 1, row);
            scaleLevel.valueProperty().addListener((ObservableValue<? extends Number> ov, Number old_val,
                    Number new_val) -> scaleValue.setText(String.format("%2f", new_val)));
            GridPane.setColumnSpan(scaleLevel, 3);
            row++;
            gridPane.add(scaleLevel, 0, row);
            row++;

            gridPane.add(new Label("Apply scale and shift:"), 0, row);

            Button shiftScaleBtn = new Button("v1");
            shiftScaleBtn.setMinWidth(50);
            shiftScaleBtn.setOnAction(event -> eventBus.fireEvent(new AppEvent(AppEvent.SHIFT_SCALE,
                    shiftLevel.valueProperty().intValue(), scaleLevel.valueProperty().doubleValue())));
            gridPane.add(shiftScaleBtn, 1, row);

            Button shiftScaleBtn2 = new Button("v2");
            shiftScaleBtn2.setMinWidth(50);
            shiftScaleBtn2.setOnAction(event -> eventBus.fireEvent(new AppEvent(AppEvent.SHIFT_SCALE_2,
                    shiftLevel.valueProperty().intValue(), scaleLevel.valueProperty().doubleValue())));
            gridPane.add(shiftScaleBtn2, 2, row);
            row++;
            
            Button lutLogBtn = new Button("Filter with Logaritmic LUT");
            lutLogBtn.setMinWidth(50);
            lutLogBtn.setOnAction(event -> eventBus.fireEvent(new AppEvent(AppEvent.LUT_LOG)));
            GridPane.setColumnSpan(lutLogBtn, 3);
            gridPane.add(lutLogBtn, 0, row);
            row++;

            TextField powerInput = new TextField(""+intPow);
            powerInput.setEditable(true);
            powerInput.textProperty().addListener((observable, oldValue, newValue) -> {
                try{
                    intPow = Integer.parseInt(newValue);
                }catch(Exception e){
                    // stay same
                }
            });
            gridPane.add(powerInput, 1, row);

            Button lutPowBtn = new Button("Filter with Power LUT");
            lutPowBtn.setMinWidth(50);
            lutPowBtn.setOnAction(event -> eventBus.fireEvent(new AppEvent(AppEvent.LUT_POW, intPow)));
            GridPane.setColumnSpan(lutLogBtn, 3);
            gridPane.add(lutPowBtn, 0, row);
            // row++;
        } else {
            gridPane.setId("info_empty");
            gridPane.add(new Text("No layer selected! Open an image to create a layer!"), 0, 0);
        }

        return gridPane;
    }

    /**
     * Assemble the layers panel footer
     * 
     * @param id String that is used as the id of the layer component
     * @return the footer as a VBox object
     */
    private GridPane makeControlsPanel(String id) {
        GridPane gridPane = new GridPane();
        gridPane.setId(id);
        gridPane.getStyleClass().add("gridPane");
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        CheckBox negativeCheckBox = new CheckBox("Negative");
        negativeCheckBox.setId("control--negative");
        gridPane.add(negativeCheckBox, 0, 0);

        return gridPane;
    }

    /**
     * Assemble the layers panel of the app
     * 
     * @param id String that is used as the id of the component
     * @return the layers panel as a BorderPane object
     */
    private BorderPane makeLayersPane(String id) {
        BorderPane layersPane = new BorderPane();
        layersPane.setId(id);
        layersPane.setMinHeight(200);
        layersPane.setBackground(new Background(new BackgroundFill(Color.web(ColorPalette.MID_GREY), null, null)));
        layersPane.setBorder(new Border(new BorderStroke(Color.web(ColorPalette.DARK_GREY), BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY, new BorderWidths(0.25))));

        // the layers list
        layersList = makeLayersList(IdSelectors.LAYERS_LIST);
        layersPane.setCenter(layersList);
        layersPane.setBottom(makeLayersPaneFooter(IdSelectors.LAYERS_PANE_FOOTER));
        return layersPane;
    }

    /**
     * Assemble the layers list component
     * 
     * @param id id String that is used as the id of the component
     * @return the list as a VBox object
     */
    private ListView<Layer> makeLayersList(String id) {
        ListView<Layer> list = new ListView<>();
        list.setId(id);
        list.setBackground(new Background(new BackgroundFill(Color.web(ColorPalette.LIGHT_GREY), null, null)));

        return list;
    }

    /**
     * Assemble the layers panel footer
     * 
     * @param id String that is used as the id of the layer component
     * @return the footer as a VBox object
     */
    private HBox makeLayersPaneFooter(String id) {
        HBox footer = new HBox();
        footer.setId(id);
        footer.setMinHeight(30);

        Button addBtn = new Button("+");
        addBtn.setOnAction(event -> eventBus.fireEvent(new AppEvent(AppEvent.OPEN)));

        Button removeBtn = new Button("-");
        removeBtn.setOnAction(event -> eventBus.fireEvent(new AppEvent(AppEvent.REMOVE_LAYER)));

        Region emptyRegion = new Region();
        HBox.setHgrow(emptyRegion, Priority.ALWAYS);

        footer.getChildren().addAll(emptyRegion, removeBtn, addBtn);

        return footer;
    }

    /**
     * A generic component for UI elements
     * 
     * @param id
     * @param name
     * @param bodyContent
     * @return
     */
    private BorderPane makeAppComponent(String id, String name, Node bodyContent) {
        BorderPane container = new BorderPane();
        container.setId(id);

        HBox head = new HBox();
        head.getStyleClass().add("component__head");
        head.setMaxHeight(20);
        head.setPrefHeight(20);
        Text title = new Text();
        title.getStyleClass().add("head__text");
        title.setText(name);
        head.getChildren().addAll(title);

        BorderPane body = new BorderPane();
        body.getStyleClass().add("component__body");
        body.setMinHeight(100);
        if (bodyContent != null)
            body.setCenter(bodyContent);

        container.setTop(head);
        container.setCenter(body);
        return container;
    }

    /**
     * Assemble a side by side split view for comparing two images
     * 
     * @param id
     * @return
     */
    public SplitPane makeSplitPane(String id) {
        SplitPane sp = new SplitPane();
        sp.setId(id);

        ScrollPane display1 = makeCanvasHalf(originalImageView, Color.RED);
        Pane leftHalf = makeAppComponent("left-half", "Original image of selected layer", display1);

        ScrollPane display2 = makeCanvasHalf(resultImageView, Color.GREEN);
        Pane rightHalf = makeAppComponent("right-half", "Result image of combination of layers", display2);
        sp.getItems().addAll(leftHalf, rightHalf);

        return sp;
    }

    /**
     * Displaying the image viewer in one half of the SplitPane
     * 
     * @param iv
     * @param c
     * @return
     */
    public ScrollPane makeCanvasHalf(ImageView iv, Color c) {
        StackPane centeredPane = new StackPane();
        centeredPane.setPrefSize(model.getViewerWidth(), model.getViewerHeight());
        centeredPane.getChildren().add(iv);
        iv.setPreserveRatio(true);

        GridPane outerPane = new GridPane();
        RowConstraints row = new RowConstraints();
        row.setPercentHeight(100);
        row.setFillHeight(false);
        row.setValignment(VPos.CENTER);
        outerPane.getRowConstraints().add(row);

        ColumnConstraints col = new ColumnConstraints();
        col.setPercentWidth(100);
        col.setFillWidth(false);
        col.setHalignment(HPos.CENTER);
        outerPane.getColumnConstraints().add(col);

        outerPane.add(centeredPane, 0, 0);

        ScrollPane rootPane = new ScrollPane();
        rootPane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
        rootPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        rootPane.setContent(outerPane);

        return rootPane;
    }

    /**
     * Assemble the menu bar object containing all the menus and their items
     * 
     * @param menus a list of menus (list of menu items inside)
     * @return the menubar object
     */
    private MenuBar makeMenuBar(String id, List<Pair<String, List<Pair<String, EventType<AppEvent>>>>> menus) {
        MenuBar mb = new MenuBar();
        mb.setId(id);

        for (Pair<String, List<Pair<String, EventType<AppEvent>>>> entry : menus) {
            Menu menu = new Menu(entry.getKey());

            for (Pair<String, EventType<AppEvent>> menuItemEntry : entry.getValue()) {

                MenuItem menuItem = new MenuItem(menuItemEntry.getKey());
                menuItem.setOnAction(event -> eventBus.fireEvent(new AppEvent(menuItemEntry.getValue())));
                menu.getItems().add(menuItem);
            }
            mb.getMenus().add(menu);
        }

        return mb;
    }

}
