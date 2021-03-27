package app.services.events;

import javafx.event.Event;
import javafx.event.EventType;

public class ImageEvent extends Event {
    private static final long serialVersionUID = 1L;
    
    // first define Any event, the super event
    public static final EventType<ImageEvent> ANY 
        = new EventType<>(Event.ANY, "IMAGE_EVENT");

    public static final EventType<ImageEvent> NEGATIVE
        = new EventType<>(ANY, "NEGATIVE");
    
    public static final EventType<ImageEvent> FLIP_HORIZONTAL
        = new EventType<>(ANY, "FLIP_HORIZONTAL");

    public ImageEvent(EventType<ImageEvent> type) {
        super(type);
    }    
}
