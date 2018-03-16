package view;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.transform.Scale;

public class ZoomablePane extends BorderPane {
    Node content;
    private DoubleProperty zoomFactor = new SimpleDoubleProperty(1);
    private DoubleProperty centerX = new SimpleDoubleProperty(1);
    private DoubleProperty centerY = new SimpleDoubleProperty(1);

    public ZoomablePane(Node content) {
        Group displayContainer = new Group(content);
        this.content = content;
        getChildren().add(displayContainer);
        Scale scale = new Scale(1, 1);
        displayContainer.getTransforms().add(scale);
        setId("pdaDisplay");
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        zoomFactor.addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                scale.setX(newValue.doubleValue());
                scale.setY(newValue.doubleValue());
                scale.setPivotX(getWidth() / 2 + getLayoutX());
                scale.setPivotY(getHeight() / 2 + getLayoutY());
                requestLayout();
            }
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
        setTranslateX(getTranslateX() - x);
        setTranslateY(getTranslateY() - y);
    }

    public final Double getZoomFactor() {
        return zoomFactor.get();
    }

    public final void setZoomFactor(Double zoomFactor) {
        this.zoomFactor.set(zoomFactor);
    }

    public final DoubleProperty zoomFactorProperty() {
        return zoomFactor;
    }

    public final DoubleProperty centerXProperty() {
        return centerX;
    }

    public final DoubleProperty centerYProperty() {
        return centerY;
    }

}
