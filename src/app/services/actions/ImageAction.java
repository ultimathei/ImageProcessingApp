package app.services.actions;

public interface ImageAction{
    boolean filterNegative();
    boolean flipHorizontal();
    boolean flipVertical();
    boolean resize();
    boolean pixelScale();
}
