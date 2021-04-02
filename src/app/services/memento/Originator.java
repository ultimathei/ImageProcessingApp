package app.services.memento;

import app.App;
import app.mvc.models.LayerStack;
import app.mvc.models.Model;
import javafx.util.Pair;

public class Originator {
  // the state to be saved to memento
  private Pair<String, Pair<Model, LayerStack>> state;

  public boolean set(Pair<String, Pair<Model, LayerStack>> state) {
    this.state = state;
    App.LOGGER.log("Originator - setting state ...");
    return (this.state != null);
  }
  
  public Memento saveToMemento() {
    App.LOGGER.log("Originator - Saving state to memento ...");
    return new Memento(this.state);
  }

  public Pair<Model, LayerStack> restoreFromMemento(Memento memento) {
    this.state = memento.getSavedState();
    App.LOGGER.log("Originator - Restoring state from memento ...");
    return this.state.getValue();
  }

  public static class Memento {
    private final Pair<String, Pair<Model, LayerStack>> state;
    public Memento(Pair<String, Pair<Model, LayerStack>> stateToSave) {
      state = stateToSave;
    }
    // private to Mementos
    private Pair<String, Pair<Model, LayerStack>> getSavedState() {
      return state;
    }
  }
}
