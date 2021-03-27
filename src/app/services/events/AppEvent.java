package app.services.events;

import javafx.event.Event;
import javafx.event.EventType;

public class AppEvent extends Event {
    private static final long serialVersionUID = 1L;
    
    // first define Any event, the super event
    public static final EventType<AppEvent> ANY 
        = new EventType<>(Event.ANY, "FILE_EVENT");

    public static final EventType<AppEvent> OPEN
        = new EventType<>(ANY, "OPEN");
    
    public static final EventType<AppEvent> SAVE
        = new EventType<>(ANY, "SAVE");

    public static final EventType<AppEvent> QUIT
        = new EventType<>(ANY, "QUIT");

    public static final EventType<AppEvent> UNDO
        = new EventType<>(ANY, "UNDO");

    public static final EventType<AppEvent> REDO
        = new EventType<>(ANY, "REDO");

    public static final EventType<AppEvent> NEGATIVE
        = new EventType<>(ANY, "NEGATIVE");
    
    public static final EventType<AppEvent> FLIP_HORIZONTAL
        = new EventType<>(ANY, "FLIP_HORIZONTAL");

    public AppEvent(EventType<AppEvent> type) {
        super(type);
    }    
}
