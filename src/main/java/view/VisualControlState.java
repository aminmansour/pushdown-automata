package view;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

public class VisualControlState {
    private String label;
    private double xPos;
    private double yPos;
    private StackPane view;
    private boolean isFocused;

    public VisualControlState(String label) {
        this.label = label;
        isFocused = false;
        generateControlStateView(label);
    }

    private void generateControlStateView(String label) {
        Circle c = new Circle();
        c.setStroke(Color.valueOf("388E3C"));
        c.setStrokeWidth(1);
        c.setRadius(26.0);
        c.setFill(Color.valueOf("455A64"));
        c.setCache(true);

        Text text = new Text(label);
        text.setFill(Color.WHITE);
        text.setFont(Font.font(null, FontWeight.BOLD, 16));
        text.setBoundsType(TextBoundsType.VISUAL);
        view = new StackPane();
        view.getChildren().addAll(c, text);
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


    public StackPane getView() {
        return view;
    }

    public double getWidth() {
        return 56 / 2;
    }


    public double getHeight() {
        return 56 / 2;
    }
}
