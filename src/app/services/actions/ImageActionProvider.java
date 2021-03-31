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
        eventBus.addEventHandler(AppEvent.ZOOM_IN, this::zoomIn);
        eventBus.addEventHandler(AppEvent.ZOOM_OUT, this::zoomOut);
        eventBus.addEventHandler(AppEvent.ZOOM_RESET, this::zoomReset);
        eventBus.addEventHandler(AppEvent.SET_ACTIVE_LAYER, this::setActiveLayer);
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

    @Override
    public void zoomIn(Event event) {
        App.LOGGER.log("zoom in canvas here..");
        if(Controller.getInstance().zoomIn()){
            App.LOGGER.log("success: " + event.getEventType());
            Controller.getInstance().setHasChanged(true);
        } else {
            App.LOGGER.log("fail: " + event.getEventType());
        }
    }

    @Override
    public void zoomOut(Event event) {
        App.LOGGER.log("zoom out canvas here..");
        if(Controller.getInstance().zoomOut()){
            App.LOGGER.log("success: " + event.getEventType());
            Controller.getInstance().setHasChanged(true);
        } else {
            App.LOGGER.log("fail: " + event.getEventType());
        }
    }

    @Override
    public void zoomReset(Event event) {
        App.LOGGER.log("zoom reset canvas here..");
        if(Controller.getInstance().zoomReset()){
            App.LOGGER.log("success: " + event.getEventType());
            Controller.getInstance().setHasChanged(true);
        } else {
            App.LOGGER.log("fail: " + event.getEventType());
        }
    }

    @Override
    public void setActiveLayer(AppEvent event) {
        App.LOGGER.log("Update active layer reference to: "+event.getPayload());
        if(Controller.getInstance().updateActiveLayerIndexById(event.getPayload())){
            App.LOGGER.log("success: " + event.getEventType());
            Controller.getInstance().setHasChanged(true);
        } else {
            App.LOGGER.log("fail: " + event.getEventType());
        }
    }
}
