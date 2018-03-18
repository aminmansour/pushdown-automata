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

    public void setPivot(double x, double y) {
        setTranslateX(x);
        setTranslateY(y);
    }


    public final DoubleProperty zoomFactorProperty() {
        return zoomFactor;
    }


}
