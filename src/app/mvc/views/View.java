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
    StackPane zoomableCanvas;
    ScrollPane scroll;

    // singleton -- private constructor
    private View() {
        super(root);
        eventBus = ServiceLocator.INSTANCE.getService(EventBus.class);
        ServiceLocator.INSTANCE.getService(ImageAction.class);
        ServiceLocator.INSTANCE.getService(FileAction.class);
        ServiceLocator.INSTANCE.getService(EditAction.class);
        model = Model.INSTANCE;

        root.setTop(makeMenuBar("app__menu", model.getMenuStructure()));
        scroll = makeCanvas("app__canvas");
        root.setCenter(scroll);
        root.setRight(makeSidePane("app__sidepane"));
    }

    // GETTER singletonn instance
    public static View getInstance() {
        if (instance == null)
            instance = new View();
        return instance;
    }

    // -- PUBLIC INSTANCE METHODS --

    /**
     * Updating the images in the image view objects
     * 
     * @return true if successfully updated false otherwise
     */
    public boolean setNewOriginalimage(Image image) {
        App.LOGGER.log("update canvas here..");
        try {
            ImageView iv1 = (ImageView) instance.lookup("#image-view-original");
            iv1.setImage(image);
            ImageView iv2 = (ImageView) instance.lookup("#image-view-filtered");
            iv2.setImage(image);
            return true;
        } catch (Exception e) {
            App.LOGGER.log("Error while loading new image");
            return false;
        }
    }

    /**
     * Update the filtered image with new image
     * 
     * @param image
     * @return
     */
    public boolean updateFilteredImage(Image image) {
        App.LOGGER.log("updating filtered image here..");
        try {
            ImageView iv = (ImageView) instance.lookup("#image-view-filtered");
            iv.setImage(image);
            return true;
        } catch (Exception e) {
            App.LOGGER.log("Error while updating filtered image");
            return false;
        }
    }

    // -- PRIVATE INSTANCE METHODS --

    private BorderPane makeSidePane(String id) {
        BorderPane sidePane = new BorderPane();
        sidePane.setId(id);
        sidePane.setPrefWidth(model.getSidePaneWidth());
        sidePane.setBackground(new Background(new BackgroundFill(Color.web("555555"), null, null)));
        sidePane.setBorder(new Border(new BorderStroke(Color.web(ColorPalette.DARK_GREY), BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY, new BorderWidths(0.25))));

        sidePane.setTop(makeControls("app__controls-pane"));
        sidePane.setCenter(makeLayersPane("app__layers-pane"));
        return sidePane;
    }

    private BorderPane makeLayersPane(String id) {
        BorderPane layersPane = new BorderPane();
        layersPane.setId(id);
        layersPane.setMinHeight(200);
        layersPane.setBackground(new Background(new BackgroundFill(Color.web(ColorPalette.MID_GREY), null, null)));
        layersPane.setBorder(new Border(new BorderStroke(Color.web(ColorPalette.DARK_GREY), BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY, new BorderWidths(0.25))));

        ScrollPane layers = new ScrollPane();
        layers.setBackground(new Background(new BackgroundFill(Color.web(ColorPalette.LIGHT_GREY), null, null)));
        layers.setFitToWidth(true);
        VBox layersList = new VBox();
        layersList.getChildren().add(makeLayersListItem("layer-001"));

        layers.setContent(layersList);

        VBox footer = new VBox();
        footer.setId("layers-pane__footer");
        footer.setMinHeight(30);
        footer.setBackground(new Background(new BackgroundFill(Color.web(ColorPalette.DARK_GREY), null, null)));

        layersPane.setCenter(layers);
        layersPane.setBottom(footer);
        return layersPane;
    }

    private HBox makeLayersListItem(String id) {
        HBox layersListItem = new HBox();
        layersListItem.setId(id);
        layersListItem.setMinHeight(50);
        layersListItem.setMaxHeight(50);
        layersListItem.setPrefWidth(300);
        layersListItem.setBorder(new Border(new BorderStroke(Color.web(ColorPalette.DARK_GREY), BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY, new BorderWidths(0.25))));

        Text text = new Text("Layer name - file name");
        Button btn = makeLayerButton("id__"+"set-active-button", "btn", AppEvent.SET_ACTIVE_LAYER);
        
        layersListItem.getChildren().addAll(text, btn);
        
        return layersListItem;
    }

        /**
     * An example for how to use buttons
     * 
     * @param text
     * @param eventType
     * @return
     */
    private Button makeLayerButton(String id, String text, EventType<AppEvent> eventType) {
        Button btn = new Button(text);
        btn.setId(id);
        btn.setPrefSize(60, 20);
        btn.setOnAction(event -> eventBus.fireEvent(new AppEvent(eventType, id), "coming from: "+id));

        return btn;
    }

    // CONTROLS PANE

    /**
     * ControlsPane factory method
     * @param id
     * @return HBox
     */
    private HBox makeControls(String id) {
        HBox cntrlPane = new HBox();
        cntrlPane.setId(id);
        cntrlPane.setMinHeight(200);
        cntrlPane.setBackground(new Background(new BackgroundFill(Color.web("eeeeee"), null, null)));
        cntrlPane.setBorder(new Border(new BorderStroke(Color.web(ColorPalette.DARK_GREY), BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY, new BorderWidths(0.25))));

        List<Button> controlPaneBtns = new ArrayList<>();
        controlPaneBtns.add(makeControlsPaneButton("Zoom in", AppEvent.ZOOM_IN));
        controlPaneBtns.add(makeControlsPaneButton("Zoom out", AppEvent.ZOOM_OUT));
        // controlPaneBtns.add(makeControlsPaneButton("Zoom reset", AppEvent.ZOOM_RESET));
        cntrlPane.getChildren().addAll(controlPaneBtns);

        return cntrlPane;
    }

    /**
     * Button maker for the controls pane
     * 
     * @param text
     * @param eventType
     * @return
     */
    private Button makeControlsPaneButton(String text, EventType<AppEvent> eventType) {
        Button btn = new Button(text);
        btn.setPrefSize(120, 20);
        btn.setOnAction(event -> eventBus.fireEvent(new AppEvent(eventType)));

        return btn;
    }

    /**
     * Canvas object
     * @param id
     * @return
     */
    public ScrollPane makeCanvas(String id) {
        scroll = new ScrollPane();
        scroll.setId(id);
        scroll.setPannable(true);

        zoomableCanvas = new StackPane();
        zoomableCanvas.setMinSize(5000, 5000);
        zoomableCanvas.setMaxSize(5000, 5000);
        zoomableCanvas.setBackground(new Background(new BackgroundFill(Color.AZURE, null, null)));
        zoomableCanvas.getChildren().add(makeSplitView());

        scroll.setContent(zoomableCanvas);

        return scroll;
    }

    /**
     * Creates a side-by-side split view of two image views (original and filtered
     * images)
     * 
     * @return
     */
    private HBox makeSplitView() {
        HBox box = new HBox();
        box.getChildren().addAll(makeImageView("image-view-original", model.getImageOriginal()),
                makeImageView("image-view-filtered", model.getImageFiltered()));
        box.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        box.setPadding(new Insets(model.getPaddingSize()));
        box.setAlignment(Pos.TOP_LEFT);
        box.setSpacing(10);
        return box;
    }

    /**
     * Makes a single image view to be used to diplay an image
     * 
     * @param image
     * @return
     */
    private ScrollPane makeImageView(String id, Image image) {
        // display imageView and add original image inside
        ImageView iv = new ImageView(){
            @Override
            public void requestFocus() {
                //
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
        ScrollPane scroll = new ScrollPane() {
            @Override
            public void requestFocus() {
                //
            }
        };
        scroll.setContent(iv);
        scroll.setMaxSize(w, h);
        scroll.setMinSize(w, h);
        return scroll;
    }

    /**
     * Make a menu bar instance
     * 
     * @param menus
     * @return
     */
    private MenuBar makeMenuBar(String id, List<Pair<String, List<Pair<String, EventType<AppEvent>>>>> menus) {
        MenuBar menuBar = new MenuBar();

        for (Pair<String, List<Pair<String, EventType<AppEvent>>>> entry : menus) {
            Menu menu = new Menu(entry.getKey());

            for (Pair<String, EventType<AppEvent>> menuItemEntry : entry.getValue()) {

                MenuItem menuItem = new MenuItem(menuItemEntry.getKey());
                menuItem.setOnAction(event -> eventBus.fireEvent(new AppEvent(menuItemEntry.getValue())));
                menu.getItems().add(menuItem);
            }
            menuBar.getMenus().add(menu);
        }

        menuBar.setId(id);
        return menuBar;
    }

    // -- PUBLIC METHODS --

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
