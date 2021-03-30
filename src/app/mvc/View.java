package app.mvc;

import app.App;
import app.mvc.models.*;
import app.services.actions.*;
import app.services.events.*;
import app.utils.*;

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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
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

    // singleton -- private constructor
    private View() {
        super(root);
        eventBus = ServiceLocator.INSTANCE.getService(EventBus.class);
        ServiceLocator.INSTANCE.getService(ImageAction.class);
        ServiceLocator.INSTANCE.getService(FileAction.class);
        ServiceLocator.INSTANCE.getService(EditAction.class);
        model = Model.INSTANCE;

        root.setTop(makeMenuBar("app__menu", model.getMenuStructure()));
        root.setCenter(makeCanvas("app__canvas"));
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

        sidePane.setTop(makeEditPane("app__edit-pane"));
        sidePane.setCenter(makeLayersPane("app__layers-pane"));
        return sidePane;
    }

    private HBox makeLayersPane(String id) {
        HBox layersPane = new HBox();
        layersPane.setMinHeight(200);
        layersPane.setBackground(new Background(new BackgroundFill(Color.web("777777"), null, null)));
        layersPane.setId(id);

        return layersPane;
    }
    
    private HBox makeEditPane(String id) {
        HBox editPane = new HBox();
        editPane.setMinHeight(200);
        editPane.setBackground(new Background(new BackgroundFill(Color.web("eeeeee"), null, null)));
        editPane.setId(id);

        return editPane;
    }



    /**
     * 
     * @param id
     * @return
     */
    private ScrollPane makeCanvas(String id) {
        // usinga stack pane to better position
        StackPane wrapPane = new StackPane();
        double w = (double) model.getCanvasWidth();
        double h = (double) model.getCanvasHeight();
        wrapPane.setPrefSize(w,h);
        wrapPane.setMinSize(w,h);

        // make the canvas
        HBox hb = makeSplitView();
        wrapPane.getChildren().add(hb);
        wrapPane.setBackground(new Background(new BackgroundFill(Color.AZURE, null, null)));
        StackPane.setAlignment(hb, Pos.CENTER);

        ScrollPane scroll = new ScrollPane();
        scroll.setContent(wrapPane);
        scroll.setId(id);
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
        box.getChildren().addAll(
            makeImageView("image-view-original", model.getImageOriginal()), 
            makeImageView("image-view-filtered", model.getImageFiltered())
        );
        box.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        box.setPadding(new Insets(model.getPaddingSize()));
        box.setAlignment(Pos.CENTER);
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
        ImageView iv = new ImageView();
        if (image != null) {
            iv.setImage(image);
        }
        iv.setPreserveRatio(true);
        iv.setSmooth(true);
        iv.setCache(true);
        iv.setId(id);

        double w = model.getViewerWidth() + 3.0;
        double h = model.getViewerHeight() + 3.0;
        ScrollPane scroll = new ScrollPane();
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

    // -- NOT USED CURRENTLY --
    /**
     * An example for how to use buttons
     * 
     * @param text
     * @param eventType
     * @return
     */
    private Button makeControlButton(String text, EventType<AppEvent> eventType) {
        Button btn = new Button(text);
        btn.setPrefSize(120, 20);
        btn.setOnAction(event -> eventBus.fireEvent(new AppEvent(eventType)));

        return btn;
    }
}
