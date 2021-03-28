package app.mvc.models;

import app.services.events.AppEvent;
import app.utils.Pair;
import java.util.List;
import java.util.TreeSet;
import javafx.event.EventType;
import javafx.scene.image.Image;

/**
 * A singleton class to store the model (of MVC) of the application.
 * using enum for singleton.
 */
public enum Model {
    INSTANCE;
    private MenuModel menuModel = MenuModel.INSTANCE;

    private boolean hasChanged = false;

    // List of supported image formats
    private String[] formats = { "bmp", "gif", "tif", "jpeg", "jpg", "png" };

    // option index for
    private int opIndex = 0;
    // last operation index reference
    private int lastOp = 0;

    // The width and height of the app
    private int appWidth = 512;
    private int appHeight = 512;
    private int paddingSize  = 10;

    private String originalImgType;
    // The input image saved as a javafx image object
    private Image imageOriginal;
    // The modified image saved as a javafx image object
    private Image imageFiltered;

    // decorator to return menus List from menuModel
    public List<Pair<String, List<Pair<String, EventType<AppEvent>>>>> getMenuStructure(){
        return menuModel.getMenuStructure();
    }


    // -- GETTERS --
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

    public int getAppWidth() {
        return appWidth;
    }

    public int getAppHeight() {
        return appHeight;
    }

    public int getPaddingSize() {
        return paddingSize;
    }

    public boolean getHasChanged() {
        return hasChanged;
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

    public void setAppWidth(int w) {
        appWidth = w;
    }

    public void setAppHeight(int h) {
        appHeight = h;
    }

    public void setHasChanged(boolean state) {
        hasChanged = state;
    }
}
