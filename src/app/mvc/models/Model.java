package app.mvc.models;

import app.services.events.AppEvent;
import app.utils.Pair;
import java.util.List;
import java.util.TreeSet;
import javafx.event.EventType;
import javafx.scene.image.Image;

/**
 * A singleton class to store the model (of MVC) of the application. using enum
 * for singleton.
 */
public enum Model {
    INSTANCE;

    private MenuModel menuModel = MenuModel.INSTANCE;

    private final String DEFAULT_IMAGE_PATH = "/Users/ultimathei/macDocs/code_local/ImageProcessingApp/src/images/Barbara.bmp";

    // List of supported image formats
    private String[] formats = { "bmp", "gif", "tif", "jpeg", "jpg", "png" };

    // DEFAULT VIEW SETTINGS
    private int viewerWidth = 512;
    private int viewerHeight = 512;
    private int paddingSize = 20;
    private int sidePaneWidth = 220;

    // The input image saved as a javafx image object
    private Image imageOriginal;
    // The modified image saved as a javafx image object
    private Image imageResult;
    // the current scale of the filtered image
    private double currentScale = 1.0; 

    public void updateModel(Image orig, Image res){
        imageOriginal = orig;
        imageResult = res;
    }

    // ACTION HISTORY -- not used yet
    // unsaved changes indicator
    private boolean hasChanged = false;
    // -- ACTION HISTORY NOT USED YET

    // -- GETTERS --
    public int getSidePaneWidth(){
        return sidePaneWidth;
    }
    // decorator to return menus List from menuModel
    public List<Pair<String, List<Pair<String, EventType<AppEvent>>>>> getMenuStructure() {
        return menuModel.getMenuStructure();
    }
    public String[] getFormats() {
        // sorted alphabetically and in lower case
        TreeSet<String> formatSet = new TreeSet<>();
        for (String s : formats) {
            formatSet.add(s.toLowerCase());
        }
        return formatSet.toArray(new String[0]);
    }
    public Image getImageOriginal() {
        return imageOriginal;
    }
    public Image getImageResult() {
        return imageResult;
    }
    public int getViewerWidth() {
        return viewerWidth;
    }
    public int getCanvasWidth() {
        return ( viewerWidth + paddingSize ) * 2;
    }
    public int getCanvasHeight() {
        return viewerHeight + paddingSize * 2;
    }
    public int getViewerHeight() {
        return viewerHeight;
    }
    public int getPaddingSize() {
        return paddingSize;
    }
    public boolean getHasChanged() {
        return hasChanged;
    }
    public double getCurrentScale() {
        return currentScale;
    }
    public String getDefaultImagePath(){
        return DEFAULT_IMAGE_PATH;
    }

    // -- SETTERS --
    public Image setImageOriginal(Image newImage) {
        imageOriginal = newImage;
        return imageOriginal;
    }
    public Image setImageResult(Image newImage) {
        imageResult = newImage;
        return imageResult;
    }
    public void setHasChanged(boolean state) {
        hasChanged = state;
    }
    public boolean setCurrentScale(double newScale) {
        if(newScale == currentScale) return false;
        currentScale = newScale;
        return true;
    }
    public boolean clearImages() {
        imageOriginal = null;
        imageResult = null;
        
        return true;
    }
}
