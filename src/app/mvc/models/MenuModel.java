package app.mvc.models;

import app.services.events.AppEvent;
import app.utils.Pair;
import java.util.ArrayList;
import java.util.List;
import javafx.event.EventType;

/**
 * A singleton class to store the model of the menu structure. Using enum for
 * singleton.
 */
public enum MenuModel {
  INSTANCE;

  private ArrayList<Pair<String, List<Pair<String, EventType<AppEvent>>>>> menuStructure = null;

  private void initMenus() {
    menuStructure = new ArrayList<>();
    menuStructure.add(new Pair<>("File", assembleFileMenu()));
    menuStructure.add(new Pair<>("Edit", assembleEditMenu()));
    menuStructure.add(new Pair<>("Image Filters", assembleFiltersMenu()));
  }

  public List<Pair<String, List<Pair<String, EventType<AppEvent>>>>> getMenuStructure() {
    if (menuStructure == null)
      initMenus();
    return menuStructure;
  }

  /**
   * Assembles the list to be used for the file menu
   * 
   * @return
   */
  private List<Pair<String, EventType<AppEvent>>> assembleFileMenu() {
    List<Pair<String, EventType<AppEvent>>> list = new ArrayList<>();
    list.add(new Pair<>("Open", AppEvent.OPEN));
    list.add(new Pair<>("Save", AppEvent.SAVE));
    list.add(new Pair<>("Quit", AppEvent.QUIT));
    return list;
  }

  /**
   * Assembles the list to be used for the edit menu
   * 
   * @return
   */
  private List<Pair<String, EventType<AppEvent>>> assembleEditMenu() {
    List<Pair<String, EventType<AppEvent>>> list = new ArrayList<>();
    list.add(new Pair<>("Undo", AppEvent.UNDO));
    list.add(new Pair<>("Redo", AppEvent.REDO));
    return list;
  }

  /**
   * Assembles the list to be used for the filters menu
   * 
   * @return
   */
  private List<Pair<String, EventType<AppEvent>>> assembleFiltersMenu() {
    List<Pair<String, EventType<AppEvent>>> list = new ArrayList<>();
    list.add(new Pair<>("Negative", AppEvent.NEGATIVE));
    list.add(new Pair<>("Flip Horizontally", AppEvent.FLIP_HORIZONTAL));
    return list;
  }
}
