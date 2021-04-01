package app.mvc.views;

import app.App;
import app.mvc.models.*;
import app.services.actions.*;
import app.services.events.*;
import app.utils.*;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
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
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
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
    private StackPane zoomableCanvas;
    private StackPane infoStack;
    // private ScrollPane viewPort;
    private SplitPane viewPort;
    private ImageView originalImageView;
    private ImageView resultImageView;
    private VBox sidePane;
    private ListView<Layer> layersList;

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
        // viewPort = makeCanvas(IdSelectors.CANVAS);
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
        setSelectedLayer(0);

        return true;
    }

    // allowing controller to set active list item to sync up with activeIndex
    public void setSelectedLayer(int i) {
        layersList.getSelectionModel().select(i);
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

    // ZOOM for canvas - does not scale the image files
    public void zoomInCanvas() {
        // zoomableCanvas.getTransforms().add(new Scale(1.2, 1.2, 0, 0));
    }

    public void zoomOutCanvas() {
        // zoomableCanvas.getTransforms().add(new Scale(0.8, 0.8, 0, 0));
    }

    public void resetZoomCanvas() {
        // zoomableCanvas.getTransforms().add(new Scale(1, 1, 0, 0));
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
        BorderPane controls = makeAppComponent(IdSelectors.CONTROLS_PANE, "Controls", infoStack);
        BorderPane layersPane = makeLayersPane(IdSelectors.LAYERS_PANE);
        BorderPane layers = makeAppComponent("layers", "Layers", layersPane);
        VBox.setVgrow(layers, Priority.ALWAYS);

        pane.getChildren().addAll(controls, layers);
        return pane;
    }

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
        gridPane.setId("info_" + layer.getId());
        gridPane.getStyleClass().add("controls");
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        // set values from layer
        gridPane.add(new Text("Layer id: "), 0, 0);
        gridPane.add(new Text(layer.getId()), 1, 0);

        gridPane.add(new Text("Layer name: "), 0, 1);
        String name = (layer.getName() != null) ? layer.getName() : "not set";
        gridPane.add(new Text(name), 1, 1);

        gridPane.add(new Text("File extension: "), 0, 2);
        String extension = (layer.getFileExtension() != null) ? layer.getFileExtension() : "not set";
        gridPane.add(new Text(extension), 1, 2);

        CheckBox negativeCheckBox = new CheckBox("Negative");
        negativeCheckBox.setId("control--negative");
        gridPane.add(negativeCheckBox, 0, 3);

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
     * Assemble a layers list item object
     * 
     * @param id   String that is used as the id of the layer component
     * @param name String that is used as the name of the layer
     * @return the layer as a HBox object
     */
    private HBox makeLayersListItem(String id, String name) {
        HBox listItem = new HBox();
        listItem.setId(id);
        listItem.setMinHeight(50);
        listItem.setMaxHeight(50);
        listItem.setPrefWidth(300);
        listItem.setBorder(new Border(new BorderStroke(Color.web(ColorPalette.DARK_GREY), BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY, new BorderWidths(0.25))));

        // internal structure of a layer
        Text text = new Text(name);
        Button btn = makeLayerButton(IdSelectors.LAYER_BTN_ACTIVATE, "btn", AppEvent.SET_ACTIVE_LAYER);
        listItem.getChildren().addAll(text, btn);

        return listItem;
    }

    /**
     * Assemble the layers panel footer
     * 
     * @param id String that is used as the id of the layer component
     * @return the footer as a VBox object
     */
    private VBox makeLayersPaneFooter(String id) {
        VBox footer = new VBox();
        footer.setId(id);
        footer.setMinHeight(30);
        return footer;
    }

    /**
     * Essemble a button for the layer component
     * 
     * @param id        String that is used as the id of the layer component
     * @param text      String that is used as the display label of the button
     * @param eventType the eventType object the button click calls
     * @return a layer button as a Button object
     */
    private Button makeLayerButton(String id, String text, EventType<AppEvent> eventType) {
        Button btn = new Button(text);
        btn.setId(id);
        btn.setPrefSize(60, 20);
        btn.setOnAction(event -> eventBus.fireEvent(new AppEvent(eventType, id), "coming from: " + id));

        return btn;
    }

    /**
     * Assembles a Controls Pane object, to be used as a set of action fields and
     * buttons
     * 
     * @param id String that is used as the id of the layer component
     * @return the control pane as an HBox object
     */
    private HBox makeControls(String id) {
        HBox pane = new HBox();
        pane.setId(id);
        pane.setMinHeight(200);
        pane.setBackground(new Background(new BackgroundFill(Color.web(ColorPalette.DARK_GREY), null, null)));
        pane.setBorder(new Border(new BorderStroke(Color.web(ColorPalette.DARK_GREY), BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY, new BorderWidths(0.25))));

        // the internal structure of a control pane -- read it from model?
        // generate dynamically
        List<Button> controlPaneBtns = new ArrayList<>();
        controlPaneBtns.add(makeControlsPaneButton(IdSelectors.CONTROLS_BUTTON_ZOOM_IN, "Zoom in", AppEvent.ZOOM_IN));
        controlPaneBtns
                .add(makeControlsPaneButton(IdSelectors.CONTROLS_BUTTON_ZOOM_OUT, "Zoom out", AppEvent.ZOOM_OUT));
        controlPaneBtns.add(makeControlsPaneButton("test-button-id-666", "Test", AppEvent.NEGATIVE_BTN));

        pane.getChildren().addAll(controlPaneBtns);

        return pane;
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
        body.setMinHeight(200);
        if (bodyContent != null)
            body.setCenter(bodyContent);

        container.setTop(head);
        container.setCenter(body);
        return container;
    }

    /**
     * Assemble a button for the control pane
     * 
     * @param id        String that is used as the id of the button component
     * @param text      String that is used as the label of the button component
     * @param eventType the event type the button will call
     * @return a button as a Button object
     */
    private Button makeControlsPaneButton(String id, String text, EventType<AppEvent> eventType) {
        Button btn = new Button(text);
        btn.setId(id);
        btn.setPrefSize(120, 20);
        btn.setOnAction(event -> eventBus.fireEvent(new AppEvent(eventType)));

        return btn;
    }

    public SplitPane makeSplitPane(String id) {
        SplitPane sp = new SplitPane();
        sp.setId(id);

        ScrollPane display1 = makeCanvasHalf(originalImageView, Color.RED);
        Pane leftHalf = makeAppComponent("left-half", "Original image", display1);

        ScrollPane display2 = makeCanvasHalf(resultImageView, Color.GREEN);
        Pane rightHalf = makeAppComponent("right-half", "Result image", display2);
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
