package app.services.events;

import javafx.event.Event;
import javafx.event.EventType;

public class AppEvent extends Event {
    private static final long serialVersionUID = 1L;
    private String payload;
    
    // first define Any event, the super event
    public static final EventType<AppEvent> ANY 
        = new EventType<>(Event.ANY, "GENERIC_EVENT");

    public static final EventType<AppEvent> SET_ACTIVE_LAYER
        = new EventType<>(ANY, "update active layer");
    
    public static final EventType<AppEvent> OPEN
        = new EventType<>(ANY, "Open file");

    public static final EventType<AppEvent> REMOVE_LAYER
        = new EventType<>(ANY, "Remove layer");
    
    public static final EventType<AppEvent> SAVE
        = new EventType<>(ANY, "Save file");

    public static final EventType<AppEvent> QUIT
        = new EventType<>(ANY, "Quit");

    public static final EventType<AppEvent> UNDO
        = new EventType<>(ANY, "Undo");

    public static final EventType<AppEvent> REDO
        = new EventType<>(ANY, "Redo");

    public static final EventType<AppEvent> NEGATIVE
        = new EventType<>(ANY, "Negative");
    
    public static final EventType<AppEvent> FLIP_HORIZONTAL
        = new EventType<>(ANY, "Flip horizontally");

    public static final EventType<AppEvent> FLIP_VERTICAL
        = new EventType<>(ANY, "Flip vetically");
    
    public static final EventType<AppEvent> RESIZE
        = new EventType<>(ANY, "Resize");

    public static final EventType<AppEvent> PIXEL_SCALE
        = new EventType<>(ANY, "Pixel scale");

    public static final EventType<AppEvent> PIXEL_SHIFT
        = new EventType<>(ANY, "Pixel shift");

    public static final EventType<AppEvent> ZOOM_IN
        = new EventType<>(ANY, "Zoom in");
    
    public static final EventType<AppEvent> ZOOM_OUT
        = new EventType<>(ANY, "Zoom out");

    public static final EventType<AppEvent> ZOOM_RESET
        = new EventType<>(ANY, "Zoom reset");

    public static final EventType<AppEvent> NEGATIVE_BTN
        = new EventType<>(ANY, "Negative_btn");

    // CONSTRUCTORs
    public AppEvent(EventType<AppEvent> type) {
        super(type);
    }
    public AppEvent(EventType<AppEvent> type, String payload) {
        super(type);
        this.payload = payload;
    }

    // GETTER
    public String getPayload(){
        return payload;
    }
}
