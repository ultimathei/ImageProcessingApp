package app.services.events;

import javafx.event.Event;
import javafx.event.EventType;

public class AppEvent extends Event {
    private static final long serialVersionUID = 1L;
    
    // first define Any event, the super event
    public static final EventType<AppEvent> ANY 
        = new EventType<>(Event.ANY, "GENERIC_EVENT");

    public static final EventType<AppEvent> OPEN
        = new EventType<>(ANY, "Open file");
    
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

    public AppEvent(EventType<AppEvent> type) {
        super(type);
    }    
}
