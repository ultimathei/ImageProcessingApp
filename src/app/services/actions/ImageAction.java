package app.services.actions;

import app.services.events.AppEvent;
import javafx.event.Event;

public interface ImageAction{
    void filterNegative(Event event);
    void flipHorizontal(Event event);
    void flipVertical(Event event);
    void resize(Event event);
    void pixelScale(Event event);
    void pixelShift(Event event);
    void setActiveLayer(AppEvent event);
    void setTransparency(AppEvent event);
    void shiftScale(AppEvent event);
    void shiftScale2(AppEvent event);
    void lutLog(AppEvent event);
    void lutPow(AppEvent event);
    // void zoomIn(Event event);
    // void zoomOut(Event event);
    // void zoomReset(Event event);
    // void filterNegativeBtn(Event event);
}
