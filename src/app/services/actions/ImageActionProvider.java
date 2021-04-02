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
        eventBus.addEventHandler(AppEvent.SET_ACTIVE_LAYER, this::setActiveLayer);
        eventBus.addEventHandler(AppEvent.SET_TRANSPARENCY, this::setTransparency);
        eventBus.addEventHandler(AppEvent.SHIFT_SCALE, this::shiftScale);
        eventBus.addEventHandler(AppEvent.SHIFT_SCALE_2, this::shiftScale2);
        eventBus.addEventHandler(AppEvent.LUT_LOG, this::lutLog);
        eventBus.addEventHandler(AppEvent.LUT_POW, this::lutPow);

        // testing
        // eventBus.addEventHandler(AppEvent.ZOOM_IN, this::zoomIn);
        // eventBus.addEventHandler(AppEvent.ZOOM_OUT, this::zoomOut);
        // eventBus.addEventHandler(AppEvent.ZOOM_RESET, this::zoomReset);
        // eventBus.addEventHandler(AppEvent.NEGATIVE_BTN, this::filterNegativeBtn);
    }

    // @Override
    // public void filterNegativeBtn(Event event) {
    // App.LOGGER.log("filter image as its negative here..");
    // if (Controller.getInstance().filterNegativeBtn()) {
    // App.LOGGER.log("success: " + event.getEventType());
    // Controller.getInstance().setHasChanged(true);
    // } else {
    // App.LOGGER.log("fail: " + event.getEventType());
    // }
    // }

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
        if (Controller.getInstance().transformFlipHorizontal()) {
            App.LOGGER.log("success: " + event.getEventType());
            Controller.getInstance().setHasChanged(true);
        } else {
            App.LOGGER.log("fail: " + event.getEventType());
        }
    }

    @Override
    public void flipVertical(Event event) {
        App.LOGGER.log("flip image vertically here..");
        if (Controller.getInstance().transformFlipVetical()) {
            App.LOGGER.log("success: " + event.getEventType());
            Controller.getInstance().setHasChanged(true);
        } else {
            App.LOGGER.log("fail: " + event.getEventType());
        }
    }

    @Override
    public void resize(Event event) {
        App.LOGGER.log("resize image here..");
        if (Controller.getInstance().displayResizeDialog()) {
            App.LOGGER.log("success: " + event.getEventType());
            Controller.getInstance().setHasChanged(true);
        } else {
            App.LOGGER.log("fail: " + event.getEventType());
        }
    }

    @Override
    public void pixelScale(Event event) {
        App.LOGGER.log("pixel scale image here..");
        if (Controller.getInstance().displayPixelScaleDialog()) {
            App.LOGGER.log("success: " + event.getEventType());
            Controller.getInstance().setHasChanged(true);
        } else {
            App.LOGGER.log("fail: " + event.getEventType());
        }
    }

    @Override
    public void pixelShift(Event event) {
        App.LOGGER.log("pixel shift image here..");
        if (Controller.getInstance().displayPixelShiftDialog()) {
            App.LOGGER.log("success: " + event.getEventType());
            Controller.getInstance().setHasChanged(true);
        } else {
            App.LOGGER.log("fail: " + event.getEventType());
        }
    }

    // @Override
    // public void zoomIn(Event event) {
    // App.LOGGER.log("zoom in canvas here..");
    // if (Controller.getInstance().zoomIn()) {
    // App.LOGGER.log("success: " + event.getEventType());
    // Controller.getInstance().setHasChanged(true);
    // } else {
    // App.LOGGER.log("fail: " + event.getEventType());
    // }
    // }

    // @Override
    // public void zoomOut(Event event) {
    // App.LOGGER.log("zoom out canvas here..");
    // if (Controller.getInstance().zoomOut()) {
    // App.LOGGER.log("success: " + event.getEventType());
    // Controller.getInstance().setHasChanged(true);
    // } else {
    // App.LOGGER.log("fail: " + event.getEventType());
    // }
    // }

    // @Override
    // public void zoomReset(Event event) {
    // App.LOGGER.log("zoom reset canvas here..");
    // if (Controller.getInstance().zoomReset()) {
    // App.LOGGER.log("success: " + event.getEventType());
    // Controller.getInstance().setHasChanged(true);
    // } else {
    // App.LOGGER.log("fail: " + event.getEventType());
    // }
    // }

    @Override
    public void setActiveLayer(AppEvent event) {
        App.LOGGER.log("Update active layer reference to: " + event.getPayload());
        if (Controller.getInstance().updateActiveLayerIndexById(event.getPayload())) {
            App.LOGGER.log("success: " + event.getEventType());
            Controller.getInstance().setHasChanged(true);
        } else {
            App.LOGGER.log("fail: " + event.getEventType());
        }
    }

    @Override
    public void setTransparency(AppEvent event) {
        App.LOGGER.log("Set transparency to: " + event.getAmount());
        if (Controller.getInstance().setTransparency(event.getAmount())) {
            App.LOGGER.log("success: " + event.getEventType());
            Controller.getInstance().setHasChanged(true);
        } else {
            App.LOGGER.log("fail: " + event.getEventType());
        }
    }

    @Override
    public void shiftScale(AppEvent event) {
        App.LOGGER2.log("Shift scale here");
        if (Controller.getInstance().applyFilterShiftScale(event.getShiftAmount(), event.getScaleAmount())) {
            App.LOGGER2.log("success: " + event.getEventType());
            // Controller.getInstance().setHasChanged(true);
        } else {
            App.LOGGER.log("fail: " + event.getEventType());
        }
    }

    @Override
    public void shiftScale2(AppEvent event) {
        App.LOGGER2.log("Shift scale 2 here");
        if (Controller.getInstance().applyFilterShiftScale2(event.getShiftAmount(), event.getScaleAmount())) {
            App.LOGGER2.log("success: " + event.getEventType());
            // Controller.getInstance().setHasChanged(true);
        } else {
            App.LOGGER.log("fail: " + event.getEventType());
        }
    }

    @Override
    public void lutLog(AppEvent event) {
        App.LOGGER2.log("Lut log here");
        if (Controller.getInstance().lutLog()) {
            App.LOGGER2.log("success: " + event.getEventType());
            // Controller.getInstance().setHasChanged(true);
        } else {
            App.LOGGER.log("fail: " + event.getEventType());
        }
    }

    @Override
    public void lutPow(AppEvent event) {
        App.LOGGER2.log("Lut log here");
        if (Controller.getInstance().lutPow(event.getPower())) {
            App.LOGGER2.log("success: " + event.getEventType());
            // Controller.getInstance().setHasChanged(true);
        } else {
            App.LOGGER.log("fail: " + event.getEventType());
        }
    }

    @Override
    public void bitAnd(AppEvent event) {
        Controller.getInstance().bitAnd(); 
    }

    @Override
    public void bitOr(AppEvent event) {
        Controller.getInstance().bitOr();

    }

    @Override
    public void bitNot(AppEvent event) {
        
        Controller.getInstance().bitNot();

    }

    @Override
    public void bitXor(AppEvent event) {
        Controller.getInstance().bitXor();
    }
}
