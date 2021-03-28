package app.services.actions;

import app.*;
import app.mvc.controllers.Controller;
import app.services.events.*;

public class ImageActionProvider implements ImageAction {    
    public ImageActionProvider() {
        // get the generic event bus
        EventBus eventBus = ServiceLocator.INSTANCE.getService(EventBus.class);

        eventBus.addEventHandler(AppEvent.NEGATIVE, event -> {
            if(filterNegative()) {
                App.LOGGER.log("success: "+event.getEventType());
                Controller.getInstance().setHasChanged(true);
            } else {
                App.LOGGER.log("fail: "+event.getEventType());
            }
        });

        eventBus.addEventHandler(AppEvent.FLIP_HORIZONTAL, event -> {
            if(flipHorizontal()) {
                App.LOGGER.log("success: "+event.getEventType());
                Controller.getInstance().setHasChanged(true);
            } else {
                App.LOGGER.log("fail: "+event.getEventType());
            }
        });
        
        eventBus.addEventHandler(AppEvent.FLIP_VERTICAL, event -> {
            if(flipVertical()) {
                App.LOGGER.log("success: "+event.getEventType());
                Controller.getInstance().setHasChanged(true);
            } else {
                App.LOGGER.log("fail: "+event.getEventType());
            }
        });
    }

    @Override
    public boolean filterNegative() {
        App.LOGGER.log("filter image as its negative here..");
        return Controller.getInstance().filterNegative();
    }
    
    @Override
    public boolean flipHorizontal() {
        App.LOGGER.log("flip image horizontally here..");
        return Controller.getInstance().transformFlipHorizontal();
    }

    @Override
    public boolean flipVertical() {
        App.LOGGER.log("flip image vertically here..");
        return Controller.getInstance().transformFlipVetical();
    }
}
