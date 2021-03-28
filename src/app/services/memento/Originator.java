package app.services.memento;

import app.App;
import app.mvc.models.Model;
import javafx.util.Pair;

public class Originator {
  // the state to be saved to memento
  private Pair<String, Model> state;

  public void set(Pair<String, Model> state) {
    this.state = state;
    App.LOGGER.log("Originator - setting state ...");
  }
  
  public Memento saveToMemento() {
    App.LOGGER.log("Originator - Saving state to memento ...");
    return new Memento(this.state);
  }

  public void restoreFromMemento(Memento memento) {
    this.state = memento.getSavedState();
    App.LOGGER.log("Originator - Restoring state from memento ...");
  }

  public static class Memento {
    private final Pair<String, Model> state;
    public Memento(Pair<String, Model> stateToSave) {
      state = stateToSave;
    }
    // private to Mementos
    private Pair<String, Model> getSavedState() {
      return state;
    }
  }
}
