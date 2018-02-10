package view;

import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.scene.transform.Rotate;

public class VisualControlState {
    private String label;
    private double xPos;
    private double yPos;
    private HBox view;
    private boolean isFocused;

    public VisualControlState(String label, boolean isInitial, boolean isAccepting) {
        this.label = label;
        isFocused = false;
        generateControlStateView(label, isInitial, isAccepting);
    }

    private void generateControlStateView(String label, boolean isInitial, boolean isAccepting) {
        view = new HBox();
        StackPane controlStateView = new StackPane();
        view.getChildren().add(controlStateView);

        Circle state = new Circle();
        state.setStroke(Color.valueOf("388E3C"));
        state.setStrokeWidth(1);
        state.setRadius(26.0);
        state.setFill(Color.valueOf("455A64"));
        state.setCache(true);
        controlStateView.getChildren().add(state);


        if (isAccepting) {
            generateNestedAcceptingCircle(controlStateView);
        }
        if (isInitial) {
            generateInitialStateArrow();
            System.out.println("helloa");
        }

        Text stateLabel = new Text(label);
        stateLabel.setFill(Color.WHITE);
        stateLabel.setFont(Font.font(null, FontWeight.BOLD, 16));
        stateLabel.setBoundsType(TextBoundsType.VISUAL);
        controlStateView.getChildren().add(stateLabel);
    }

    private void generateNestedAcceptingCircle(StackPane controlStateView) {
        Circle nestedCircle = new Circle();
        nestedCircle.setStroke(Color.WHITE);
        nestedCircle.setFill(null);
        nestedCircle.setStrokeWidth(2);
        nestedCircle.setRadius(22.0);
        nestedCircle.setCache(true);
        controlStateView.getChildren().addAll(nestedCircle);
    }

    private void generateInitialStateArrow() {
        // the transform for the rotation arrow rotation
        Rotate rotation = new Rotate(-45);

        Point2D tan = new Point2D(-20, 26.5).normalize().multiply(10);
        Point2D p = rotation.transform(tan);
        LineTo a1 = new LineTo(p.getX(), p.getY());
        a1.setAbsolute(false);

        rotation.setAngle(-75);
        p = rotation.transform(tan);
        LineTo a2 = new LineTo(p.getX(), p.getY());
        a2.setAbsolute(false);

        tan = new Point2D(
                -10,
                -10
        ).normalize().multiply(10);
        MoveTo move = new MoveTo(10, 10);
        p = rotation.transform(tan);
        a1 = new LineTo(p.getX(), p.getY());
        a1.setAbsolute(false);
        rotation.setAngle(0);
        p = rotation.transform(tan);
        a2 = new LineTo(p.getX(), p.getY());
        a2.setAbsolute(false);

        Path arrowHead = new Path();
        arrowHead.getElements().addAll(move, a1, move, a2);
        arrowHead.setStroke(Color.BLACK);
        arrowHead.setStrokeWidth(2);

        StackPane initialArrowContainer = new StackPane();
        initialArrowContainer.setAlignment(Pos.CENTER_RIGHT);
        initialArrowContainer.getChildren().add(arrowHead);
        view.getChildren().add(0, initialArrowContainer);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getXPos() {
        return xPos;
    }

    public void setXPos(double xPos) {
        this.xPos = xPos;
        view.setLayoutX(xPos);
    }

    public double getYPos() {
        return yPos;
    }

    public void setYPos(double yPos) {
        this.yPos = yPos;
        view.setLayoutY(yPos);
    }

    public void setFocus(boolean focus) {
        isFocused = focus;
        view.setStyle("-fx-background-color:" + (isFocused ? "#2ab27b" : "transparent"));
    }

    public boolean isFocused() {
        return isFocused;
    }


    public HBox getView() {
        return view;
    }

    public double getWidth() {
        return 56 / 2;
    }


    public double getHeight() {
        return 56 / 2;
    }
}
