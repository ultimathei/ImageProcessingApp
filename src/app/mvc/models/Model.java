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

    // List of supported image formats
    private String[] formats = { "bmp", "gif", "tif", "jpeg", "jpg", "png" };

    // The width and height of the app
    private int viewerWidth = 512;
    private int viewerHeight = 512;

    private int paddingSize = 20;

    private String originalImgType;
    // The input image saved as a javafx image object
    private Image imageOriginal;
    // The modified image saved as a javafx image object
    private Image imageFiltered;
    // the current scale of the filtered image
    private double currentScale = 1.0;

    // ACTION HISTORY

    // option index for
    private int opIndex = 0;
    // last operation index reference
    private int lastOp = 0;
    // unsaved changes indicator
    private boolean hasChanged = false;

    // -- GETTERS --
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

    public int getOpIndex() {
        return opIndex;
    }

    public int getLastOpIndex() {
        return lastOp;
    }

    public String getOriginalImgType() {
        return originalImgType;
    }

    public Image getImageOriginal() {
        return imageOriginal;
    }

    public Image getImageFiltered() {
        return imageFiltered;
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


    // -- SETTERS --

    public void setOpIndex(int i) {
        opIndex = i;
    }

    public void setLastOpIndex(int i) {
        lastOp = i;
    }

    public void setOriginalImgType(String type) {
        originalImgType = type;
    }

    public Image setImageOriginal(Image newImage) {
        imageOriginal = newImage;
        return imageOriginal;
    }

    public Image setImageFiltered(Image newImage) {
        imageFiltered = newImage;
        return imageFiltered;
    }

    // public void setViewerWidth(int w) {
    //     viewerWidth = w;
    // }

    // public void setViewerHeight(int h) {
    //     viewerHeight = h;
    // }

    public void setHasChanged(boolean state) {
        hasChanged = state;
    }

    public boolean setCurrentScale(double newScale) {
        if(newScale == currentScale) return false;
        currentScale = newScale;
        return true;
    }
}
