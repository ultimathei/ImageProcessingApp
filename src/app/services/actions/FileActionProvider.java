package app.services.actions;

import app.*;
import app.mvc.controllers.Controller;
import app.services.events.*;
import javafx.application.Platform;

/**
 * Concrete implementation of the FileAction interface.
 * This component provides the apropriate methods for events 
 * related to the File menu.
 */
public class FileActionProvider implements FileAction {
    // -- CONSTRUCTOR --
    public FileActionProvider() {
        // get the generic event bus
        EventBus eventBus = ServiceLocator.INSTANCE.getService(EventBus.class);

        eventBus.addEventHandler(AppEvent.OPEN, event -> {
            if(open()) {
                App.LOGGER.log("image openned successfully!");
            } else {
                App.LOGGER.log("image could not be openned!");
            }
        });

        eventBus.addEventHandler(AppEvent.SAVE, event -> {
            if(save()) {
                App.LOGGER.log("image saved successfully!");
            } else {
                App.LOGGER.log("image could not be saved!");
            }
        });
        
        eventBus.addEventHandler(AppEvent.QUIT, event -> {
            if(quit()) {
                App.LOGGER.log("application quit successfully!");
                Platform.exit();
            } else {
                App.LOGGER.log("application could not be quit!");
            }
        });
    }

    @Override
    public boolean open() {
        App.LOGGER.log("open image here..");
        return Controller.getInstance().openImage();
    }
    
    @Override
    public boolean save() {
        App.LOGGER.log("save image here..");
        return true;
    }
    
    @Override
    public boolean quit() {
        App.LOGGER.log("quit here..");
        return Controller.getInstance().canIQuit();
    }
}
