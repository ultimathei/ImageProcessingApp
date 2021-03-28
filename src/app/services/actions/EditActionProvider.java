package app.services.actions;

import app.*;
import app.services.events.*;

public class EditActionProvider implements EditAction {    
    public EditActionProvider() {
        // get the generic event bus
        EventBus eventBus = ServiceLocator.INSTANCE.getService(EventBus.class);

        eventBus.addEventHandler(AppEvent.UNDO, event -> {
            if(undo()){
                App.LOGGER.log("success: "+event.getEventType());
            } else {
                App.LOGGER.log("fail: "+event.getEventType());
            }
        });

        eventBus.addEventHandler(AppEvent.REDO, event -> {
            if(redo()){
                App.LOGGER.log("success: "+event.getEventType());
            } else {
                App.LOGGER.log("fail: "+event.getEventType());
            }
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