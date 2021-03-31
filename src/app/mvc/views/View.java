package app.mvc.views;

import app.App;
import app.mvc.models.*;
import app.services.actions.*;
import app.services.events.*;
import app.utils.*;

import java.util.ArrayList;
import java.util.List;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
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
    private ScrollPane viewPort;
    private BorderPane sidePane;

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

        // initialise
        menuBar = makeMenuBar(IdSelectors.MENU, model.getMenuStructure());
        viewPort = makeCanvas(IdSelectors.CANVAS);
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

    // -- PRIVATE INSTANCE METHODS --

    /**
     * Assemble the side panel of the app
     * @param id String that is used as the id of the component
     * @return a BorderPane object
     */
    private BorderPane makeSidePane(String id) {
        BorderPane pane = new BorderPane();
        pane.setId(id);
        pane.setPrefWidth(model.getSidePaneWidth());
        pane.setBackground(new Background(new BackgroundFill(Color.web(ColorPalette.DARK_GREY), null, null)));
        pane.setBorder(new Border(new BorderStroke(Color.web(ColorPalette.DARK_GREY), BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY, new BorderWidths(0.25))));

        pane.setTop(makeControls(IdSelectors.CONTROLS_PANE));
        pane.setCenter(makeLayersPane(IdSelectors.LAYERS_PANE));
        return pane;
    }

    /**
     * Assemble the layers panel of the app
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

        // an internal scrollable wrapper
        ScrollPane layers = new ScrollPane();
        layers.setBackground(new Background(new BackgroundFill(Color.web(ColorPalette.LIGHT_GREY), null, null)));
        layers.setFitToWidth(true);

        VBox list = makeLayersList(IdSelectors.LAYERS_LIST);
        layers.setContent(list);
        layersPane.setCenter(layers);
        layersPane.setBottom(makeLayersPaneFooter(IdSelectors.LAYERS_PANE_FOOTER));
        return layersPane;
    }

    /**
     * Assemble the layers list component
     * @param id id String that is used as the id of the component
     * @return the list as a VBox object
     */
    private VBox makeLayersList(String id) {
        VBox list = new VBox();
        list.setId(id);

        // add list items
        // layersList.getChildren().add(makeLayersListItem(IdSelectors.LAYERS_LIST_ITEM,
        // "Layer name - file name"));
        return list;
    }

    /**
     * Assemble a layers list item object
     * @param id String that is used as the id of the layer component
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
     * @param id String that is used as the id of the layer component
     * @return the footer as a VBox object
     */
    private VBox makeLayersPaneFooter(String id) {
        VBox footer = new VBox();
        footer.setId(id);
        footer.setMinHeight(30);
        footer.setBackground(new Background(new BackgroundFill(Color.web(ColorPalette.DARK_GREY), null, null)));
        return footer;
    }

    /**
     * Essemble a button for the layer component
     * @param id String that is used as the id of the layer component
     * @param text String that is used as the display label of the button
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
     * Assembles a Controls Pane object, to be used as a set of action fields and buttons
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
        controlPaneBtns.add(makeControlsPaneButton(IdSelectors.CONTROLS_BUTTON_ZOOM_OUT, "Zoom out", AppEvent.ZOOM_OUT));
        // controlPaneBtns.add(makeControlsPaneButton("Zoom reset",
        // AppEvent.ZOOM_RESET));

        pane.getChildren().addAll(controlPaneBtns);

        return pane;
    }

    /**
     * Assemble a button for the control pane
     * @param id String that is used as the id of the button component
     * @param text String that is used as the label of the button component
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

    /**
     * Assemble the canvas object (the image editor area)
     * @param id String that is used as the id of the pane component
     * @return the canvas area as a ScrollPane object
     */
    public ScrollPane makeCanvas(String id) {
        viewPort = new ScrollPane();
        viewPort.setId(id);
        viewPort.setPannable(true);

        zoomableCanvas = new StackPane();
        zoomableCanvas.setMinSize(5000, 5000);
        zoomableCanvas.setMaxSize(5000, 5000);
        zoomableCanvas.setBackground(new Background(new BackgroundFill(Color.AZURE, null, null)));
        zoomableCanvas.getChildren().add(makeSplitView(IdSelectors.CANVAS_SPLIT_BOX));

        viewPort.setContent(zoomableCanvas);

        return viewPort;
    }

    /**
     * Assembles a side-by-side split view of two image views (original and result)
     * @param id String that is used as the id of the box component
     * @return the split view box as an HBox object
     */
    private HBox makeSplitView(String id) {
        HBox box = new HBox();
        box.setId(id);
        box.getChildren().addAll(
            makeImageView(IdSelectors.CANVAS_ORIGINAL, model.getImageOriginal()),
            makeImageView(IdSelectors.CANVAS_RESULT, model.getImageFiltered())
        );
        box.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        box.setPadding(new Insets(model.getPaddingSize()));
        box.setAlignment(Pos.TOP_LEFT);
        box.setSpacing(10);
        return box;
    }

    /**
     * Assembles a single image view to be used to diplay an image
     * @param id String that is used as the id of the component
     * @param image binding the image object to the view
     * @return a scrollable image view as a ScrollPane object
     */
    private ScrollPane makeImageView(String id, Image image) {
        // display imageView and add original image inside
        ImageView iv = new ImageView() {
            @Override
            public void requestFocus() {
                // disabling focus
            }
        };
        if (image != null) {
            iv.setImage(image);
        }
        iv.setPreserveRatio(true);
        iv.setSmooth(true);
        iv.setCache(true);
        iv.setId(id);

        double w = model.getViewerWidth() + 3.0;
        double h = model.getViewerHeight() + 3.0;
        ScrollPane scrollableWrap = new ScrollPane() {
            @Override
            public void requestFocus() {
                //
            }
        };
        scrollableWrap.setContent(iv);
        scrollableWrap.setMaxSize(w, h);
        scrollableWrap.setMinSize(w, h);
        return scrollableWrap;
    }

    /**
     * Assemble the menu bar object containing all the menus and their items
     * @param menus a list of menus (list of menu items inside)
     * @return the menubar object
     */
    private MenuBar makeMenuBar(String id, List<Pair<String, List<Pair<String, EventType<AppEvent>>>>> menus) {
        MenuBar menuBar = new MenuBar();
        menuBar.setId(id);

        for (Pair<String, List<Pair<String, EventType<AppEvent>>>> entry : menus) {
            Menu menu = new Menu(entry.getKey());

            for (Pair<String, EventType<AppEvent>> menuItemEntry : entry.getValue()) {

                MenuItem menuItem = new MenuItem(menuItemEntry.getKey());
                menuItem.setOnAction(event -> eventBus.fireEvent(new AppEvent(menuItemEntry.getValue())));
                menu.getItems().add(menuItem);
            }
            menuBar.getMenus().add(menu);
        }

        return menuBar;
    }

    // -- PUBLIC INSTANCE METHODS --

    /**
     * Updating the image in the image view for active object 
     * (original image of the active layer)
     * @return true if successfully updated, false otherwise
     */
    public boolean setNewOriginalimage(Image image) {
        App.LOGGER.log("update canvas here..");
        try {
            ImageView iv1 = (ImageView) instance.lookup("#" + IdSelectors.CANVAS_ORIGINAL);
            iv1.setImage(image);
            // ImageView iv2 = (ImageView) instance.lookup("#" + IdSelectors.CANVAS_RESULT);
            // iv2.setImage(image);
            return true;
        } catch (Exception e) {
            App.LOGGER.log("Error while loading new image");
            return false;
        }
    }

    /**
     * Update the result image with a new, filtered version of the image
     * or a completely new image (when new original is loaded)
     * @param image the new image
     * @return true if successfully updated, false otherwise
     */
    public boolean updateResultImage(Image image) {
        App.LOGGER.log("updating filtered image here..");
        try {
            ImageView iv = (ImageView) instance.lookup("#" + IdSelectors.CANVAS_RESULT);
            iv.setImage(image);
            return true;
        } catch (Exception e) {
            App.LOGGER.log("Error while updating filtered image");
            return false;
        }
    }

    // ZOOM for canvas - does not scale the image files
    public void zoomInCanvas() {
        zoomableCanvas.getTransforms().add(new Scale(1.2, 1.2, 0, 0));
    }
    public void zoomOutCanvas() {
        zoomableCanvas.getTransforms().add(new Scale(0.8, 0.8, 0, 0));
    }
    public void resetZoomCanvas() {
        zoomableCanvas.getTransforms().add(new Scale(1, 1, 0, 0));
    }
}
