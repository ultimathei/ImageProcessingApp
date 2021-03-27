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
                App.LOGGER.log("image negated successfully!");
            } else {
                App.LOGGER.log("image could not be negated!");
            }
        });

        eventBus.addEventHandler(AppEvent.FLIP_HORIZONTAL, event -> {
            if(flipHorizontal()) {
                App.LOGGER.log("image flipped horizontally!");
            } else {
                App.LOGGER.log("image could not be flipped!");
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
        return true;
    }
}
