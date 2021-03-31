package app.services.events;

import app.App;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Group;

public class EventBusProvider implements EventBus {
    private Group group = new Group();


    @Override
    public void fireEvent(Event event) {
        group.fireEvent(event);
    }

    @Override
    public void fireEvent(Event event, String payload) {
        App.LOGGER.log("Event with payload fired, "+payload);
        group.fireEvent(event);
    }

    @Override
    public <T extends Event> void addEventHandler(EventType<T> type, EventHandler<? super T> handler) {
        group.addEventHandler(type, handler);
    }

    @Override
    public <T extends Event> void removeEventHandler(EventType<T> type, EventHandler<? super T> handler) {
        group.removeEventHandler(type, handler);
    }
}
