package app.services.actions;

import app.*;
import app.services.events.*;

public class EditActionProvider implements EditAction {    
    public EditActionProvider() {
        // get the generic event bus
        EventBus eventBus = ServiceLocator.INSTANCE.getService(EventBus.class);

        eventBus.addEventHandler(AppEvent.UNDO, event -> {
            boolean success = undo();
        });

        eventBus.addEventHandler(AppEvent.REDO, event -> {
            boolean success = redo();
        });
    }

    @Override
    public boolean undo() {
        App.LOGGER.log("undo here..");
        return true;
    }
    
    @Override
    public boolean redo() {
        App.LOGGER.log("redo here..");
        return true;
    }
}