package app.services.actions;

import javafx.event.Event;

public interface FileAction{
    boolean open(Event event);
    boolean removeLayer(Event event);
    boolean save(Event event);
    boolean quit(Event event);
}
