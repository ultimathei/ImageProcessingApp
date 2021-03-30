package app.services.actions;

import javafx.event.Event;

public interface ImageAction{
    void filterNegative(Event event);
    void flipHorizontal(Event event);
    void flipVertical(Event event);
    void resize(Event event);
    void pixelScale(Event event);
    void pixelShift(Event event);
    void zoomIn(Event event);
    void zoomOut(Event event);
    void zoomReset(Event event);
}
