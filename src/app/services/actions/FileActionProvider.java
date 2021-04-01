package app.services.actions;

import app.*;
import app.mvc.controllers.Controller;
import app.services.events.*;
import javafx.event.Event;

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

        eventBus.addEventHandler(AppEvent.OPEN, this::open);
        eventBus.addEventHandler(AppEvent.REMOVE_LAYER, this::removeLayer);
        eventBus.addEventHandler(AppEvent.SAVE, this::save);
        eventBus.addEventHandler(AppEvent.QUIT, this::quit);
    }

    @Override
    public boolean open(Event event) {
        App.LOGGER.log("open image here..");
        return Controller.getInstance().newLayerFromFile(null);
    }
    
    @Override
    public boolean removeLayer(Event event) {
        App.LOGGER.log("remove layer here..");
        return Controller.getInstance().deleteLayer();
    }
    
    @Override
    public boolean save(Event event) {
        App.LOGGER.log("save image here..");
        return true;
    }
    
    @Override
    public boolean quit(Event event) {
        App.LOGGER.log("quit here..");
        return Controller.getInstance().tryQuit();
    }
}
