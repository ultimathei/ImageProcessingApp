package app.services.actions;

import app.*;
import app.mvc.controllers.Controller;
import app.services.events.*;
import javafx.event.Event;

public class ImageActionProvider implements ImageAction {
    public ImageActionProvider() {
        // get the generic event bus
        EventBus eventBus = ServiceLocator.INSTANCE.getService(EventBus.class);

        eventBus.addEventHandler(AppEvent.NEGATIVE, this::filterNegative);
        eventBus.addEventHandler(AppEvent.FLIP_HORIZONTAL, this::flipHorizontal);
        eventBus.addEventHandler(AppEvent.FLIP_VERTICAL, this::flipVertical);
        eventBus.addEventHandler(AppEvent.RESIZE, this::resize);
        eventBus.addEventHandler(AppEvent.PIXEL_SCALE, this::pixelScale);
        eventBus.addEventHandler(AppEvent.PIXEL_SHIFT, this::pixelShift);
    }

    @Override
    public void filterNegative(Event event) {
        App.LOGGER.log("filter image as its negative here..");
        if (Controller.getInstance().filterNegative()) {
            App.LOGGER.log("success: " + event.getEventType());
            Controller.getInstance().setHasChanged(true);
        } else {
            App.LOGGER.log("fail: " + event.getEventType());
        }
    }

    @Override
    public void flipHorizontal(Event event) {
        App.LOGGER.log("flip image horizontally here..");
        if(Controller.getInstance().transformFlipHorizontal()){
        App.LOGGER.log("success: " + event.getEventType());
            Controller.getInstance().setHasChanged(true);
        } else {
            App.LOGGER.log("fail: " + event.getEventType());
        }
    }

    @Override
    public void flipVertical(Event event) {
        App.LOGGER.log("flip image vertically here..");
        if(Controller.getInstance().transformFlipVetical()){
            App.LOGGER.log("success: " + event.getEventType());
            Controller.getInstance().setHasChanged(true);
        } else {
            App.LOGGER.log("fail: " + event.getEventType());
        }
    }

    @Override
    public void resize(Event event) {
        App.LOGGER.log("resize image here..");
        if(Controller.getInstance().displayResizeDialog()){
            App.LOGGER.log("success: " + event.getEventType());
            Controller.getInstance().setHasChanged(true);
        } else {
            App.LOGGER.log("fail: " + event.getEventType());
        }
    }

    @Override
    public void pixelScale(Event event) {
        App.LOGGER.log("pixel scale image here..");
        if(Controller.getInstance().displayPixelScaleDialog()){
            App.LOGGER.log("success: " + event.getEventType());
            Controller.getInstance().setHasChanged(true);
        } else {
            App.LOGGER.log("fail: " + event.getEventType());
        }
    }

    @Override
    public void pixelShift(Event event) {
        App.LOGGER.log("pixel shift image here..");
        if(Controller.getInstance().displayPixelShiftDialog()){
            App.LOGGER.log("success: " + event.getEventType());
            Controller.getInstance().setHasChanged(true);
        } else {
            App.LOGGER.log("fail: " + event.getEventType());
        }
    }
}
