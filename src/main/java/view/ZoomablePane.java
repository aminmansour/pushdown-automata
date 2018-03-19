package view;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.transform.Scale;

/**
 * A special BorderPane which supports zoom and panning
 */
public class ZoomablePane extends BorderPane {

    Node content;
    private DoubleProperty zoomFactor = new SimpleDoubleProperty(1);

    /**
     * A basic constructor of the view
     *
     * @param content the content which this instance encapsulates
     */
    public ZoomablePane(Node content) {
        Group displayContainer = new Group(content);
        this.content = content;
        getChildren().add(displayContainer);
        Scale scale = new Scale(1, 1);
        displayContainer.getTransforms().add(scale);
        setId("pdaDisplay");
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        zoomFactor.addListener((observable, oldValue, newValue) -> {
            scale.setX(newValue.doubleValue());
            scale.setY(newValue.doubleValue());
            scale.setPivotX(getWidth() / 2 + getLayoutX());
            scale.setPivotY(getHeight() / 2 + getLayoutY());
            requestLayout();
        });


    }

    /**
     * defines a layout, overwriting initial values defined in a border pane
     */
    protected void layoutChildren() {
        Pos pos = Pos.TOP_LEFT;
        double width = getWidth();
        double height = getHeight();
        double top = getInsets().getTop();
        double right = getInsets().getRight();
        double left = getInsets().getLeft();
        double bottom = getInsets().getBottom();
        double contentWidth = (width - left - right) / zoomFactor.get();
        double contentHeight = (height - top - bottom) / zoomFactor.get();
        layoutInArea(content, left, top,
                contentWidth, contentHeight,
                0, null,
                pos.getHpos(),
                pos.getVpos());
    }

    /**
     * A method which allows to redefine the pan location of zoom. Specifically pertains to the
     * content that is encapsulated which in this view instance
     * @param x nex x-position to pan to
     * @param y new y-position to pan to
     */
    public void setPivot(double x, double y) {
        setTranslateX(x);
        setTranslateY(y);
    }


    /**
     * A method which returns the zoom factor property. This property defines the current zoom into
     * the content.
     * @return The zoom DoubleProperty value
     */
    public final DoubleProperty zoomFactorProperty() {
        return zoomFactor;
    }


}
