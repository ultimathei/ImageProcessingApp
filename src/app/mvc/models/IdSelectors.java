package app.mvc.models;

public enum IdSelectors {
  INSTANCE;
  public static final String MENU = "app__menu";

  public static final String CANVAS = "app__canvas";
  public static final String CANVAS_SPLIT_BOX = "canvas__split-box";
  public static final String CANVAS_ORIGINAL = "canvas__image--original";
  public static final String CANVAS_RESULT = "canvas__image--filtered";

  public static final String SIDE_PANE = "app__sidepane";
  public static final String CONTROLS_PANE = "side__controls-pane";
  public static final String LAYERS_PANE = "side__layers-pane";
  
  public static final String CONTROLS_BUTTON = "controls-pane__button";
  public static final String CONTROLS_BUTTON_ZOOM_IN = "controls-pane__button--zoom-in";
  public static final String CONTROLS_BUTTON_ZOOM_OUT = "controls-pane__button--zoom-out";
  public static final String CONTROLS_BUTTON_ZOOM_RESET = "controls-pane__button--zoom-reset";


  public static final String LAYERS_LIST = "layers-list";
  public static final String LAYERS_LIST_ITEM = "layers-list-item";
  public static final String LAYERS_PANE_FOOTER = "layers-pane__footer";
  
  public static final String LAYER_BTN_ACTIVATE = "layer__button--activate";
}
