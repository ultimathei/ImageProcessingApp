package app.services.events;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;

public interface EventBus {
    void fireEvent(Event event);
    void fireEvent(Event event, String payload);

    <T extends Event> void addEventHandler(EventType<T> type, EventHandler<? super T> handler);
    <T extends Event> void removeEventHandler(EventType<T> type, EventHandler<? super T> handler);
}
